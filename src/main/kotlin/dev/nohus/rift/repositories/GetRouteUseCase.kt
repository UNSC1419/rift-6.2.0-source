package dev.nohus.rift.repositories

import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single
import java.util.ArrayDeque
import java.util.PriorityQueue
import java.util.UUID
import kotlin.math.exp

@Serializable
enum class RoutePreference {
    Shortest,
    Safer,
    LessSecure,
    Insecure,
}

@Single
class GetRouteUseCase(
    private val starGatesRepository: StarGatesRepository,
    private val jumpBridgesRepository: JumpBridgesRepository,
    private val solarSystemsRepository: SolarSystemsRepository,
) {
    private data class WeightedStep(val system: Int, val cost: Double)

    // Make crossing an undesired security band much more expensive than a small detour.
    private val securityPenaltyCost = exp(0.15 * 50)

    // Keeping directions explicit because jump bridge connections can be one-way.
    private data class Adjacency(
        val outgoing: Map<Int, IntArray>,
        val incoming: Map<Int, IntArray>,
    )

    @Volatile private var cachedAdjacencyNoJumpBridges: Adjacency? = null
    @Volatile private var cachedAdjacencyWithJumpBridges: Adjacency? = null
    @Volatile private var cachedAdjacencyWithJumpBridgesKey: UUID? = null
    private val cacheLock = Any()

    /**
     * Returns system IDs from origin through destination, or null when no route exists
     */
    operator fun invoke(
        fromSolarSystem: Int,
        toSolarSystem: Int,
        withJumpBridges: Boolean,
        preference: RoutePreference,
    ): List<Int>? {
        if (fromSolarSystem == toSolarSystem) return listOf(fromSolarSystem)

        val adjacency = getAdjacency(withJumpBridges)
        if (fromSolarSystem !in adjacency.outgoing || toSolarSystem !in adjacency.incoming) {
            return null
        }
        return when (preference) {
            RoutePreference.Shortest -> bidirectionalBfs(
                startSolarSystem = fromSolarSystem,
                goalSolarSystem = toSolarSystem,
                adjacency = adjacency
            )
            RoutePreference.Safer, RoutePreference.LessSecure, RoutePreference.Insecure -> findWeightedRoute(
                startSolarSystem = fromSolarSystem,
                goalSolarSystem = toSolarSystem,
                adjacency = adjacency,
                preference = preference,
            )
        }
    }

    private fun getAdjacency(withJumpBridges: Boolean): Adjacency {
        return if (withJumpBridges) {
            val currentKey = jumpBridgesRepository.connectionsKey
            val cachedKey = cachedAdjacencyWithJumpBridgesKey
            val cached = cachedAdjacencyWithJumpBridges

            if (cached != null && cachedKey == currentKey) {
                cached
            } else {
                synchronized(cacheLock) {
                    // Another caller may have filled the cache while this caller waited
                    val cachedAfterLock = cachedAdjacencyWithJumpBridges
                    val keyAfterLock = cachedAdjacencyWithJumpBridgesKey
                    if (cachedAfterLock != null && keyAfterLock == currentKey) {
                        cachedAfterLock
                    } else {
                        buildAdjacency(withJumpBridges = true).also {
                            cachedAdjacencyWithJumpBridges = it
                            cachedAdjacencyWithJumpBridgesKey = currentKey
                        }
                    }
                }
            }
        } else {
            cachedAdjacencyNoJumpBridges ?: synchronized(cacheLock) {
                cachedAdjacencyNoJumpBridges ?: buildAdjacency(withJumpBridges = false).also {
                    cachedAdjacencyNoJumpBridges = it
                }
            }
        }
    }

    private fun buildAdjacency(withJumpBridges: Boolean): Adjacency {
        val connections = starGatesRepository.connections.toMutableList()
        if (withJumpBridges) {
            val bridgeConnections = jumpBridgesRepository.getConnections().map { it.from.id to it.to.id }
            connections += bridgeConnections
        }
        return Adjacency(
            outgoing = connections.groupBy({ it.first }, { it.second })
                .mapValues { [_, neighbors] -> neighbors.toIntArray() },
            incoming = connections.groupBy({ it.second }, { it.first })
                .mapValues { [_, neighbors] -> neighbors.toIntArray() },
        )
    }

    /**
     * Bidirectional breadth-first search for when every edge has equal cost
     */
    private fun bidirectionalBfs(
        startSolarSystem: Int,
        goalSolarSystem: Int,
        adjacency: Adjacency,
    ): List<Int>? {
        val frontierStart = ArrayDeque<Int>().apply { add(startSolarSystem) }
        val frontierGoal = ArrayDeque<Int>().apply { add(goalSolarSystem) }

        // Each side records how it reached a system so the two halves can be joined later
        val parentFromStart = HashMap<Int, Int>(128)
        val parentFromGoal = HashMap<Int, Int>(128)

        val visitedStart = HashSet<Int>(128).apply { add(startSolarSystem) }
        val visitedGoal = HashSet<Int>(128).apply { add(goalSolarSystem) }

        fun expandFrontier(
            frontier: ArrayDeque<Int>,
            connections: Map<Int, IntArray>,
            visitedSelf: MutableSet<Int>,
            visitedOther: Set<Int>,
            parentSelf: MutableMap<Int, Int>,
        ): Int? {
            val levelSize = frontier.size
            repeat(levelSize) {
                val current = frontier.removeFirst()
                val neighbors = connections[current] ?: return@repeat
                for (neighbor in neighbors) {
                    // Zarzakh can be an endpoint, but a route cannot pass through it
                    if (IdRanges.isZarzakh(neighbor) && neighbor != startSolarSystem && neighbor != goalSolarSystem) {
                        continue
                    }
                    if (!visitedSelf.add(neighbor)) continue
                    parentSelf[neighbor] = current
                    // The first shared system connects the two recorded path halves
                    if (neighbor in visitedOther) return neighbor
                    frontier.addLast(neighbor)
                }
            }
            return null
        }

        while (frontierStart.isNotEmpty() && frontierGoal.isNotEmpty()) {
            val meeting = if (frontierStart.size <= frontierGoal.size) {
                expandFrontier(frontierStart, adjacency.outgoing, visitedStart, visitedGoal, parentFromStart)
            } else {
                expandFrontier(frontierGoal, adjacency.incoming, visitedGoal, visitedStart, parentFromGoal)
            }
            if (meeting != null) {
                return reconstructBidirectionalPath(
                    meeting,
                    parentFromStart,
                    parentFromGoal,
                    startSolarSystem,
                    goalSolarSystem,
                )
            }
        }

        return null
    }

    /**
     * Dijkstra's algorithm for when security preferences assign different cost to entering systems.
     * It finds the lowest-cost route, which may take more jumps.
     */
    private fun findWeightedRoute(
        startSolarSystem: Int,
        goalSolarSystem: Int,
        adjacency: Adjacency,
        preference: RoutePreference,
    ): List<Int>? {
        val frontier = PriorityQueue(compareBy<WeightedStep> { it.cost })
        val costs = HashMap<Int, Double>()
        val parents = HashMap<Int, Int>()
        costs[startSolarSystem] = 0.0
        frontier.add(WeightedStep(startSolarSystem, 0.0))

        while (frontier.isNotEmpty()) {
            val step = frontier.remove()
            val current = step.system
            val currentCost = step.cost
            if (currentCost > costs.getValue(current)) continue
            if (current == goalSolarSystem) {
                val route = ArrayList<Int>()
                var system = current
                route.add(system)
                while (system != startSolarSystem) {
                    system = parents.getValue(system)
                    route.add(system)
                }
                route.reverse()
                return route
            }

            for (neighbor in adjacency.outgoing[current] ?: continue) {
                // Zarzakh can be an endpoint, but a route cannot pass through it
                if (IdRanges.isZarzakh(neighbor) && neighbor != goalSolarSystem) continue
                val security = solarSystemsRepository.getSystem(neighbor)?.security ?: 0.0
                val edgeCost = when (preference) {
                    RoutePreference.Shortest -> 1.0
                    RoutePreference.Safer -> when {
                        security <= 0.0 -> 2 * securityPenaltyCost
                        security < 0.45 -> securityPenaltyCost
                        else -> 0.90
                    }
                    RoutePreference.LessSecure -> when {
                        security <= 0.0 -> 2 * securityPenaltyCost
                        security < 0.45 -> 0.90
                        else -> securityPenaltyCost
                    }
                    RoutePreference.Insecure -> when {
                        security <= 0.0 -> 0.90
                        security < 0.45 -> securityPenaltyCost
                        else -> 2 * securityPenaltyCost
                    }
                }
                val newCost = currentCost + edgeCost
                if (newCost < costs.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    costs[neighbor] = newCost
                    parents[neighbor] = current
                    frontier.add(WeightedStep(neighbor, newCost))
                }
            }
        }
        return null
    }

    private fun reconstructBidirectionalPath(
        meet: Int,
        parentFromStart: Map<Int, Int>,
        parentFromGoal: Map<Int, Int>,
        startSolarSystem: Int,
        goalSolarSystem: Int,
    ): List<Int> {
        // Follow forward-search parents from the meeting point back to the origin
        val left = ArrayList<Int>()
        var current = meet
        left.add(current)
        while (current != startSolarSystem) {
            val parent = parentFromStart[current] ?: break
            current = parent
            left.add(current)
        }
        left.reverse()

        // Goal-search parents point toward the destination, already in the needed order
        val right = ArrayList<Int>()
        current = meet
        while (current != goalSolarSystem) {
            val parent = parentFromGoal[current] ?: break
            current = parent
            right.add(current)
        }

        // The right half starts after the meeting system, so append it only once
        if (left.isNotEmpty() && right.isNotEmpty() && left.last() == meet) {
            left.addAll(right)
            return left
        }

        return left + right
    }
}
