package dev.nohus.rift.notifications.eve

import dev.nohus.rift.alerts.AlertsTriggerController
import dev.nohus.rift.characters.repositories.LocalCharactersRepository
import dev.nohus.rift.network.Result
import dev.nohus.rift.network.esi.EsiApi
import dev.nohus.rift.network.esi.models.CharactersIdNotification
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.network.requests.expires
import dev.nohus.rift.sso.scopes.ScopeGroups
import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.types.TypeDetails
import dev.nohus.rift.types.TypeDetailsLoader
import dev.nohus.rift.utils.delay
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.time.Duration
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

private val logger = KotlinLogging.logger {}

@Single
class EveNotificationsRepository(
    private val esiApi: EsiApi,
    private val localCharactersRepository: LocalCharactersRepository,
    private val typeDetailsLoader: TypeDetailsLoader,
    private val eveNotificationReferencedIdsExtractor: EveNotificationReferencedIdsExtractor,
    private val alertsTriggerController: AlertsTriggerController,
    private val settings: Settings,
) {

    data class EveNotifications(
        val notifications: List<EveNotification> = emptyList(),
        val typeDetails: TypeDetails = TypeDetails(emptyMap()),
    )

    private val _notifications = MutableStateFlow<EveNotifications>(EveNotifications())
    val notifications = _notifications.asStateFlow()

    private val updateInterval = Duration.ofMinutes(1)
    private val cacheExpirySafetyMargin = Duration.ofSeconds(1)

    @OptIn(FlowPreview::class)
    suspend fun start() = coroutineScope {
        val updateRequests = Channel<Unit>(Channel.CONFLATED)
        launch {
            localCharactersRepository.characters.debounce(500.milliseconds).collect {
                updateRequests.send(Unit)
            }
        }

        var scheduledUpdate: Job? = null
        while (true) {
            updateRequests.receive()
            scheduledUpdate?.cancel()
            val expires = updateNotifications()
            val now = Instant.now()
            val nextUpdate = expires?.plus(cacheExpirySafetyMargin) ?: now.plus(updateInterval)
            scheduledUpdate = launch {
                delay(Duration.between(now, nextUpdate))
                updateRequests.send(Unit)
            }
        }
    }

    private suspend fun updateNotifications(): Instant? = withContext(Dispatchers.Default) {
        val characterIds = localCharactersRepository.characters.value
            .filter { ScopeGroups.readNotifications in it.scopes }
            .map { it.characterId }
        val results = coroutineScope {
            characterIds.map { characterId ->
                async {
                    characterId to esiApi.getCharactersIdNotifications(Originator.EveNotifications, characterId)
                }
            }.awaitAll()
        }
        val now = Instant.now()
        val nextExpires = results.mapNotNull { [_, result] -> result.success?.headers?.expires }
            .filter { expires -> expires.isAfter(now) && expires.isBefore(now.plus(updateInterval)) }
            .minOrNull()

        val newRecipientCharacterIds = mutableMapOf<Long, MutableSet<Int>>()
        val lastKnownTimestamps = settings.eveNotificationLastKnownTimestamps.toMutableMap()
        val fetchedNotifications = results.flatMap { [characterId, result] ->
            when (result) {
                is Result.Success -> {
                    val characterNotifications = result.data.body.map { it.toDomain(characterId) }
                    val latestTimestamp = characterNotifications.maxOfOrNull { it.timestamp.toEpochMilli() }
                    val lastKnownTimestamp = lastKnownTimestamps[characterId]
                    if (lastKnownTimestamp == null) {
                        // This character was never loaded before
                        if (latestTimestamp != null) {
                            lastKnownTimestamps[characterId] = latestTimestamp
                        }
                    } else {
                        characterNotifications
                            .filter { it.timestamp.toEpochMilli() > lastKnownTimestamp }
                            .forEach {
                                newRecipientCharacterIds.getOrPut(it.notificationId) { mutableSetOf() } += characterId
                            }
                        if (latestTimestamp != null && latestTimestamp > lastKnownTimestamp) {
                            lastKnownTimestamps[characterId] = latestTimestamp
                        }
                    }
                    characterNotifications
                }
                is Result.Failure -> {
                    logger.error { "Could not update EVE notifications for character $characterId" }
                    _notifications.value.notifications.filter { characterId in it.characterIds }
                }
            }
        }
        val notifications = fetchedNotifications
            .groupBy { it.notificationId }
            .values
            .map { duplicates ->
                duplicates.first().copy(characterIds = duplicates.flatMap { it.characterIds }.distinct())
            }
        val typeDetails = loadTypeDetails(notifications)
        _notifications.value = EveNotifications(notifications, typeDetails)
        notifications.filter { it.notificationId in newRecipientCharacterIds }
            .sortedBy { it.timestamp }
            .forEach { notification ->
                alertsTriggerController.onNewEveNotification(
                    notification,
                    typeDetails,
                    newRecipientCharacterIds.getValue(notification.notificationId),
                )
            }
        if (lastKnownTimestamps != settings.eveNotificationLastKnownTimestamps) {
            settings.eveNotificationLastKnownTimestamps = lastKnownTimestamps
        }

        nextExpires
    }

    private suspend fun loadTypeDetails(notifications: List<EveNotification>): TypeDetails {
        val referencedIds = notifications.fold(EveNotificationReferencedIdsExtractor.ReferencedIds()) { ids, notification ->
            ids + eveNotificationReferencedIdsExtractor.extract(notification)
        }

        return typeDetailsLoader.loadTypeDetails(
            originator = Originator.EveNotifications,
            uncategorizedIds = referencedIds.uncategorizedIds,
            structureIdsWithCharacterId = referencedIds.structureIdsWithCharacterId,
            stationIds = referencedIds.stationIds,
            characterIds = referencedIds.characterIds,
            corporationIds = referencedIds.corporationIds,
            allianceIds = referencedIds.allianceIds,
            factionIds = referencedIds.factionIds,
            systemIds = referencedIds.systemIds,
            moonIds = referencedIds.moonIds,
            typeIds = referencedIds.typeIds,
            onLoadingStateChanged = {}
        )
    }

    private fun CharactersIdNotification.toDomain(characterId: Int) = EveNotification(
        characterIds = listOf(characterId),
        notificationId = notificationId,
        senderId = senderId,
        senderType = senderType,
        timestamp = timestamp,
        type = type,
        isRead = isRead,
        text = text,
    )
}
