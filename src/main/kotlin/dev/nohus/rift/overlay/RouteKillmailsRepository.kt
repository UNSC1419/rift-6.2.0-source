package dev.nohus.rift.overlay

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.overlay_route_stargate_name
import dev.nohus.rift.intel.state.IntelStateController
import dev.nohus.rift.intel.state.IntelStateController.Dated
import dev.nohus.rift.intel.state.SystemEntity
import dev.nohus.rift.killboard.Killmail
import dev.nohus.rift.killboard.KillmailLocationResolver
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.network.Result
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.network.zkillboard.ZkillboardApi
import dev.nohus.rift.network.zkillboardr2z2.EsiKillmail
import dev.nohus.rift.repositories.Position
import dev.nohus.rift.repositories.SolarSystemsRepository.MapSolarSystem
import dev.nohus.rift.utils.delay
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.time.Duration
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

private val logger = KotlinLogging.logger {}
private val killmailIdPattern = Regex("/kill/(\\d+)/")
private val lookback = Duration.ofHours(1)
private val historyRetention = Duration.ofHours(2)
private val failureRetryDelay = Duration.ofMinutes(1)
private const val pageSize = 200
private const val maxPages = 1

@Single
class RouteKillmailsRepository(
    private val zkillboardApi: ZkillboardApi,
    private val locationResolver: KillmailLocationResolver,
    intelStateController: IntelStateController,
) {
    data class LocationGroup(
        val location: String?,
        val count: Int,
        val locationText: UiText?,
    )

    data class SystemKills(
        val count: Int?,
        val groups: List<LocationGroup>,
        val isUnavailable: Boolean,
    )

    private data class KillmailRecord(
        val id: Long,
        val time: Instant,
        val systemId: Int,
        val location: String?,
        val locationText: UiText? = location?.toUiText(),
    )

    private sealed interface History {
        val checkedAt: Instant

        data class Ready(override val checkedAt: Instant, val killmails: List<KillmailRecord>) : History
        data class Failed(override val checkedAt: Instant) : History
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val watchedSystemIds = MutableStateFlow<List<Int>>(emptyList())
    private val history = MutableStateFlow<Map<Int, History>>(emptyMap())
    private val liveKillmails = MutableStateFlow<Map<Long, KillmailRecord>>(emptyMap())
    private val now = MutableStateFlow(Instant.now())
    private val refreshRequests = Channel<Unit>(Channel.CONFLATED)
    private val intelKillmails = intelStateController.state
        .map(::collectIntelKillmails)
        .stateIn(scope, SharingStarted.Eagerly, emptyMap())

    val state: StateFlow<Map<Int, SystemKills>> = combine(
        watchedSystemIds, history, liveKillmails, intelKillmails, now,
    ) { systemIds, history, live, intel, currentTime ->
        buildSystemSummaries(systemIds, history, live, intel, currentTime)
    }.stateIn(scope, SharingStarted.Eagerly, emptyMap())

    init {
        scope.launch { refreshHistoryWhenRequested() }
        scope.launch { expireOldKillmailsEachMinute() }
    }

    fun watchSystems(systemIds: List<Int>) {
        val distinctIds = systemIds.distinct()
        if (watchedSystemIds.value == distinctIds) return
        watchedSystemIds.value = distinctIds
        refreshRequests.trySend(Unit)
    }

    fun submitLiveKillmail(killmail: Killmail) {
        val record = createKillmailRecord(killmail)
        val currentTime = Instant.now()
        if (record.time < currentTime.minus(lookback)) return
        now.value = currentTime
        liveKillmails.update { it + (record.id to record) }
    }

    private suspend fun refreshHistoryWhenRequested() {
        for (request in refreshRequests) refreshWatchedSystems()
    }

    private suspend fun refreshWatchedSystems() {
        for (systemId in watchedSystemIds.value) {
            if (systemId !in watchedSystemIds.value || !shouldRefreshHistory(systemId, Instant.now())) continue
            if (history.value[systemId] is History.Failed) {
                history.update { it - systemId }
            }
            when (val result = fetchSystemKillmails(systemId)) {
                is Result.Success -> {
                    history.update { it + (systemId to History.Ready(Instant.now(), result.data)) }
                    now.value = Instant.now()
                }
                is Result.Failure -> {
                    logger.warn(result.cause) { "Could not load route killmails for system $systemId" }
                    history.update { it + (systemId to History.Failed(Instant.now())) }
                }
            }
            delay(100.milliseconds)
        }
    }

    private fun shouldRefreshHistory(systemId: Int, currentTime: Instant): Boolean {
        return when (val cached = history.value[systemId]) {
            is History.Ready -> cached.checkedAt.plus(lookback) <= currentTime
            is History.Failed -> cached.checkedAt.plus(failureRetryDelay) <= currentTime
            null -> true
        }
    }

    private suspend fun expireOldKillmailsEachMinute() {
        while (true) {
            delay(Duration.ofMinutes(1))
            val currentTime = Instant.now()
            now.value = currentTime
            liveKillmails.update { killmails ->
                killmails.filterValues { it.time >= currentTime.minus(lookback) }
            }
            history.update { cached ->
                cached.filter { [systemId, value] ->
                    systemId in watchedSystemIds.value || value.checkedAt.plus(historyRetention) > currentTime
                }
            }
            refreshRequests.trySend(Unit)
        }
    }

    private fun buildSystemSummaries(
        systemIds: List<Int>,
        history: Map<Int, History>,
        live: Map<Long, KillmailRecord>,
        intel: Map<Long, KillmailRecord>,
        currentTime: Instant,
    ): Map<Int, SystemKills> {
        val cutoff = currentTime.minus(lookback)
        val liveBySystem = live.values.groupBy { it.systemId }
        val intelBySystem = intel.values.groupBy { it.systemId }
        return systemIds.associateWith { systemId ->
            val cached = history[systemId]
            val historical = (cached as? History.Ready)?.killmails.orEmpty()
            val killmails = (liveBySystem[systemId].orEmpty() + historical + intelBySystem[systemId].orEmpty())
                .filter { it.time >= cutoff && it.time <= currentTime }
                .distinctBy { it.id }
            SystemKills(
                count = killmails.size.takeIf { cached is History.Ready },
                groups = groupKillmailsByLocation(killmails),
                isUnavailable = cached is History.Failed,
            )
        }
    }

    private fun groupKillmailsByLocation(killmails: List<KillmailRecord>): List<LocationGroup> {
        return killmails.groupBy { it.location }
            .map { [location, records] -> LocationGroup(location, records.size, records.first().locationText) }
            .sortedWith(compareBy<LocationGroup> { it.location == null }.thenByDescending { it.count }.thenBy { it.location })
    }

    private fun collectIntelKillmails(
        state: Map<MapSolarSystem, List<Dated<SystemEntity>>>,
    ): Map<Long, KillmailRecord> {
        return state.flatMap { [system, entities] ->
            entities.mapNotNull { dated ->
                val killmail = dated.item as? SystemEntity.Killmail ?: return@mapNotNull null
                val id = killmailIdPattern.find(killmail.url)?.groupValues?.get(1)?.toLongOrNull()
                    ?: return@mapNotNull null
                KillmailRecord(id, dated.timestamp, system.id, location = null)
            }
        }.associateBy { it.id }
    }

    private suspend fun fetchSystemKillmails(systemId: Int): Result<List<KillmailRecord>> {
        val killmails = mutableListOf<KillmailRecord>()
        for (page in 1..maxPages) {
            when (val result = zkillboardApi.getSystemKillmails(Originator.Killmails, systemId, page)) {
                is Result.Success -> {
                    killmails += result.data.map(::createKillmailRecord)
                    if (result.data.size < pageSize) return Result.Success(killmails)
                }
                is Result.Failure -> return result
            }
        }
        return Result.Failure(IllegalStateException("Route killmail history exceeded $maxPages pages for system $systemId"))
    }

    private fun createKillmailRecord(killmail: EsiKillmail): KillmailRecord {
        val position = killmail.victim.position?.let { Position(it.x, it.y, it.z) }
        return createKillmailRecord(
            id = killmail.killmailId,
            time = killmail.killmailTime,
            systemId = killmail.solarSystemId.toInt(),
            position = position,
            shipTypeId = killmail.victim.shipTypeId.toInt(),
        )
    }

    private fun createKillmailRecord(killmail: Killmail): KillmailRecord {
        return createKillmailRecord(
            id = killmail.killmailId,
            time = killmail.killmailTime,
            systemId = killmail.solarSystemId,
            position = killmail.position,
            shipTypeId = killmail.victim.shipTypeId,
        )
    }

    private fun createKillmailRecord(
        id: Long,
        time: Instant,
        systemId: Int,
        position: Position?,
        shipTypeId: Int?,
    ): KillmailRecord {
        val entity = locationResolver.resolve(systemId, position, shipTypeId)
        // Preserve the original grouping and sorting key independently of translated display text
        val location = when (entity) {
            is SystemEntity.Gate -> "${entity.system2.name} stargate"
            is SystemEntity.Celestial -> entity.celestial.name
            else -> null
        }
        val locationText = when (entity) {
            is SystemEntity.Gate -> Res.string.overlay_route_stargate_name.toUiText(entity.system2.name.toUiTextArgument())
            else -> location?.toUiText()
        }
        return KillmailRecord(id, time, systemId, location, locationText)
    }
}
