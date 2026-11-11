package dev.nohus.rift.repositories

import dev.nohus.rift.network.Result
import dev.nohus.rift.network.esi.EsiApi
import dev.nohus.rift.network.esi.EsiErrorException
import dev.nohus.rift.network.esi.models.UniverseName
import dev.nohus.rift.network.esi.models.UniverseNamesCategory
import dev.nohus.rift.network.requests.Originator
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single
import kotlin.collections.chunked
import kotlin.collections.plusAssign
import kotlin.collections.toSet

private val logger = KotlinLogging.logger {}

@Single
class NamesRepository(
    private val esiApi: EsiApi,
) {

    private val resolutionMutex = Mutex()

    @Volatile
    private var names: Map<Long, UniverseName> = emptyMap()

    /**
     * Returns an already known name for the ID
     */
    fun getName(id: Int): String? {
        return names[id.toLong()]?.name
    }

    @JvmName("getNameLong")
    fun getName(id: Long): String? {
        return names[id]?.name
    }

    fun getCategory(id: Int): UniverseNamesCategory? {
        return names[id.toLong()]?.category
    }

    @JvmName("getCategoryLong")
    fun getCategory(id: Long): UniverseNamesCategory? {
        return names[id]?.category
    }

    suspend fun resolveNames(originator: Originator, ids: List<Int>) {
        resolveNames(originator, ids.map { it.toLong() })
    }

    @JvmName("resolveNamesLong")
    suspend fun resolveNames(originator: Originator, ids: List<Long>) {
        resolveNames(originator, ids.toSet())
    }

    suspend fun resolveNames(originator: Originator, ids: Set<Long>) {
        resolutionMutex.withLock {
            var unresolvedIdChunks = ids
                .filter { it !in names }
                .filterNot { IdRanges.isSpawnedItem(it) }
                .chunked(1000)
            val resolved = mutableListOf<UniverseName>()
            val invalidIds = mutableSetOf<Long>()

            while (unresolvedIdChunks.isNotEmpty()) {
                val failedIdChunks = mutableListOf<List<Long>>()
                coroutineScope {
                    unresolvedIdChunks.map { chunk ->
                        async {
                            chunk to esiApi.postUniverseNames(originator, chunk)
                        }
                    }.awaitAll().forEach { [chunk, result] ->
                        when (result) {
                            is Result.Success -> resolved += result.data
                            is Result.Failure -> {
                                if ((result.cause as? EsiErrorException)?.error?.error == "Ensure all IDs are valid before resolving.") {
                                    if (chunk.size > 1) {
                                        val midpoint = chunk.size / 2
                                        failedIdChunks += listOf(chunk.take(midpoint), chunk.drop(midpoint))
                                    } else {
                                        invalidIds += chunk.single()
                                    }
                                }
                            }
                        }
                    }
                }
                unresolvedIdChunks = failedIdChunks
            }
            if (invalidIds.isNotEmpty()) {
                logger.error { "Could not resolve invalid IDs (${invalidIds.size}): ${invalidIds.sorted()}" }
            }

            val newNames = resolved.associateBy { it.id }
            if (newNames.isNotEmpty()) names += newNames
        }
    }
}
