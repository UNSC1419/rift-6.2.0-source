package dev.nohus.rift.repositories

import dev.nohus.rift.characters.repositories.LocalCharactersRepository
import dev.nohus.rift.network.Result
import dev.nohus.rift.network.esi.EsiApi
import dev.nohus.rift.network.esi.models.WarParticipant
import dev.nohus.rift.network.esi.models.WarsId
import dev.nohus.rift.network.esi.pagination.fetchOffsetIdPaginated
import dev.nohus.rift.network.everef.EveRefApi
import dev.nohus.rift.network.requests.Originator
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.koin.core.annotation.Single
import java.time.Instant
import kotlin.time.Duration.Companion.minutes

private val logger = KotlinLogging.logger {}

@Single
class WarsRepository(
    private val esiApi: EsiApi,
    private val eveRefApi: EveRefApi,
    private val localCharactersRepository: LocalCharactersRepository,
) {

    @Volatile
    private var wars: Map<Long, WarsId> = emptyMap()
    private var newestWarId: Long? = null
    private var pendingWarIds: Set<Long> = emptySet()

    internal suspend fun start() {
        while (true) {
            val loaded = loadWars()
            delay(if (loaded) 60.minutes else 5.minutes)
        }
    }

    fun isWarTarget(allianceId: Int?, corporationId: Int?): Boolean {
        if (allianceId == null && corporationId == null) return false
        val localCharacters = localCharactersRepository.characters.value.mapNotNull { it.info }
        if (localCharacters.isEmpty()) return false
        val now = Instant.now()
        return wars.values.any { war ->
            val started = war.started ?: return@any false
            if (started > now || war.finished?.let { it <= now } == true) return@any false

            if (war.aggressor.matchesEntity(allianceId, corporationId)) {
                localCharacters.any { war.matchesDefenders(it.allianceId, it.corporationId) }
            } else if (war.matchesDefenders(allianceId, corporationId)) {
                localCharacters.any { war.aggressor.matchesEntity(it.allianceId, it.corporationId) }
            } else {
                false
            }
        }
    }

    private suspend fun loadWars(): Boolean {
        val currentWars = if (newestWarId == null) {
            eveRefApi.getCurrentWars(Originator.Wars).success ?: run {
                logger.warn { "Could not load current wars from EVE Ref" }
                return false
            }
        } else {
            wars
        }
        val knownWarId = newestWarId ?: currentWars.keys.maxOrNull()
        if (knownWarId == null) {
            logger.error { "Cannot discover new wars without an EVE Ref war ID to start from" }
            return false
        }

        val newIds = discoverNewWarIds(knownWarId)
        val ids = currentWars.keys + pendingWarIds + newIds.orEmpty()
        logger.debug { "Loading ${ids.size} active, pending or new wars" }
        val details = fetchWarDetails(ids)
        val failedIds = details.filterValues { it == null }.keys

        val now = Instant.now()
        wars = ids.mapNotNull { id ->
            val war = details[id] ?: currentWars[id] ?: return@mapNotNull null
            if (war.finished == null || war.finished > now) id to war else null
        }.toMap()
        pendingWarIds = failedIds
        newestWarId = maxOf(knownWarId, newIds?.maxOrNull() ?: knownWarId)
        if (failedIds.isNotEmpty()) {
            logger.warn { "Could not refresh ${failedIds.size} wars" }
        }
        logger.debug { "Loaded ${wars.size} active wars" }
        return newIds != null && failedIds.isEmpty()
    }

    private suspend fun discoverNewWarIds(knownWarId: Long): List<Long>? {
        var hasReachedKnownWars = false
        return fetchOffsetIdPaginated(getOffsetId = { it }) { maxWarId ->
            if (hasReachedKnownWars) {
                Result.Success(emptyList())
            } else {
                esiApi.getWars(Originator.Wars, maxWarId).map { ids ->
                    hasReachedKnownWars = ids.any { it <= knownWarId }
                    ids.filter { it > knownWarId }
                }
            }
        }.onFailure {
            logger.warn(it) { "Could not discover new wars" }
        }.success
    }

    private suspend fun fetchWarDetails(ids: Set<Long>): Map<Long, WarsId?> = coroutineScope {
        val semaphore = Semaphore(10)
        val requests = ids.associateWith { id ->
            async {
                semaphore.withPermit {
                    try {
                        esiApi.getWarsId(Originator.Wars, id).success
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        logger.warn(e) { "Could not load war $id" }
                        null
                    }
                }
            }
        }
        requests.mapValues { it.value.await() }
    }

    private fun WarsId.matchesDefenders(allianceId: Int?, corporationId: Int?): Boolean {
        return defender.matchesEntity(allianceId, corporationId) || allies.any { it.matchesEntity(allianceId, corporationId) }
    }

    private fun WarParticipant.matchesEntity(allianceId: Int?, corporationId: Int?): Boolean {
        return (this.corporationId != null && this.corporationId == corporationId) ||
            (this.allianceId != null && this.allianceId == allianceId)
    }
}
