package dev.nohus.rift.notifications.history

import androidx.compose.ui.text.buildAnnotatedString
import dev.nohus.rift.alerts.AlertsTriggerController.AlertLocationMatch
import dev.nohus.rift.intel.state.SystemEntity
import dev.nohus.rift.notifications.IntelNotificationTitle
import dev.nohus.rift.notifications.NotificationText
import dev.nohus.rift.notifications.NotificationsController.Notification
import dev.nohus.rift.notifications.eve.EveNotification
import dev.nohus.rift.repositories.CelestialsRepository
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.repositories.character.CharacterDetailsRepository.CharacterDetails
import dev.nohus.rift.types.TypesRepository
import org.koin.core.annotation.Single
import java.time.Duration
import java.time.Instant

@Single
class NotificationHistoryMapper(
    private val solarSystemsRepository: SolarSystemsRepository,
    private val typesRepository: TypesRepository,
    private val celestialsRepository: CelestialsRepository,
) {
    fun toDto(item: NotificationHistoryItem) = StoredNotificationHistoryItem(
        timestamp = item.timestamp.toEpochMilli(),
        notification = item.notification.toDto(),
        alert = item.alert,
        reason = (item.reason as? NotificationHistoryReason.Literal)?.text,
        reasonId = when (item.reason) {
            NotificationHistoryReason.SovereigntyUpgradesImported -> StoredNotificationReason.SovereigntyUpgradesImported
            is NotificationHistoryReason.Literal, null -> null
        },
    )

    fun toDomain(item: StoredNotificationHistoryItem): NotificationHistoryItem? {
        return NotificationHistoryItem(
            timestamp = Instant.ofEpochMilli(item.timestamp),
            notification = item.notification.toDomain() ?: return null,
            alert = item.alert,
            reason = when (item.reasonId) {
                StoredNotificationReason.SovereigntyUpgradesImported -> NotificationHistoryReason.SovereigntyUpgradesImported
                null -> item.reason?.let { NotificationHistoryReason.Literal(it) }
            },
        )
    }

    private fun Notification.toDto(): StoredNotification = when (this) {
        is Notification.TextNotification -> when (val text = text) {
            is NotificationText.Literal -> StoredNotification.Text(
                title = text.title,
                message = text.message.text,
                annotations = text.message.getStringAnnotations(0, text.message.length).map {
                    StoredAnnotation(it.tag, it.item, it.start, it.end)
                },
                characterId = characterId,
                typeId = type?.id,
                relatedCharacter = relatedCharacter?.let { StoredRelatedCharacter(it.id, it.name, it.standing) },
            )
            is NotificationText.PlanetaryIndustry -> StoredNotification.PlanetaryIndustry(
                planetName = text.planetName,
                attentionInSeconds = text.attentionIn?.seconds,
                characterId = characterId,
                typeId = type?.id,
                relatedCharacter = relatedCharacter?.let { StoredRelatedCharacter(it.id, it.name, it.standing) },
            )
            is NotificationText.GameAction -> StoredNotification.GameAction(
                actionType = text.type,
                subject = text.subject,
                characterId = characterId,
                typeId = type?.id,
                relatedCharacter = relatedCharacter?.let { StoredRelatedCharacter(it.id, it.name, it.standing) },
            )
        }
        is Notification.ChatMessageNotification -> StoredNotification.ChatMessage(
            channel = channel,
            messages = messages.map {
                StoredChatMessage(it.message, it.highlight, it.sender, it.senderCharacterId, it.senderStanding)
            },
        )
        is Notification.JabberMessageNotification -> StoredNotification.JabberMessage(chat, message, highlight, sender)
        is Notification.IntelNotification -> when (val title = title) {
            is IntelNotificationTitle.Literal -> StoredNotification.Intel(
                title.value,
                locationMatch.toDto(),
                systemEntities.map { it.toDto() },
                solarSystem.id,
            )
            is IntelNotificationTitle.Report -> StoredNotification.IntelReport(
                title.type,
                title.labels,
                locationMatch.toDto(),
                systemEntities.map { it.toDto() },
                solarSystem.id,
            )
        }
        is Notification.SovereigntyUpgradeImportNotification -> StoredNotification.SovereigntyUpgradeImport(
            system.id,
            upgrades.map { it.id },
        )
        is Notification.EveNotification -> StoredNotification.EveNotification(
            characterIds = notification.characterIds,
            notificationId = notification.notificationId,
            senderId = notification.senderId,
            senderType = notification.senderType,
            timestamp = notification.timestamp.toEpochMilli(),
            type = notification.type,
            isRead = notification.isRead,
            text = notification.text,
        )
    }

    private fun StoredNotification.toDomain(): Notification? {
        return when (this) {
            is StoredNotification.Text -> Notification.TextNotification(
                text = NotificationText.Literal(
                    title = title,
                    message = buildAnnotatedString {
                        append(message)
                        annotations.forEach { addStringAnnotation(it.tag, it.value, it.start, it.end) }
                    },
                ),
                characterId = characterId,
                type = typeId?.let { typesRepository.getTypeOrPlaceholder(it) },
                relatedCharacter = relatedCharacter?.let { Notification.Character(it.id, it.name, it.standing) },
            )
            is StoredNotification.PlanetaryIndustry -> Notification.TextNotification(
                text = NotificationText.PlanetaryIndustry(planetName, attentionInSeconds?.let { Duration.ofSeconds(it) }),
                characterId = characterId,
                type = typeId?.let { typesRepository.getTypeOrPlaceholder(it) },
                relatedCharacter = relatedCharacter?.let { Notification.Character(it.id, it.name, it.standing) },
            )
            is StoredNotification.GameAction -> Notification.TextNotification(
                text = NotificationText.GameAction(actionType, subject),
                characterId = characterId,
                type = typeId?.let { typesRepository.getTypeOrPlaceholder(it) },
                relatedCharacter = relatedCharacter?.let { Notification.Character(it.id, it.name, it.standing) },
            )
            is StoredNotification.ChatMessage -> Notification.ChatMessageNotification(
                channel,
                messages.map { Notification.ChatMessage(it.message, it.highlight, it.sender, it.senderCharacterId, it.senderStanding) },
            )
            is StoredNotification.JabberMessage -> Notification.JabberMessageNotification(chat, message, highlight, sender)
            is StoredNotification.Intel -> Notification.IntelNotification(
                IntelNotificationTitle.Literal(title),
                locationMatch.toDomain() ?: return null,
                systemEntities.mapNotNull { it.toDomain() },
                solarSystemsRepository.getSystem(solarSystemId) ?: return null,
            )
            is StoredNotification.IntelReport -> Notification.IntelNotification(
                IntelNotificationTitle.Report(reportType, labels),
                locationMatch.toDomain() ?: return null,
                systemEntities.mapNotNull { it.toDomain() },
                solarSystemsRepository.getSystem(solarSystemId) ?: return null,
            )
            is StoredNotification.SovereigntyUpgradeImport -> Notification.SovereigntyUpgradeImportNotification(
                solarSystemsRepository.getSystem(systemId) ?: return null,
                upgradeTypeIds.map { typesRepository.getTypeOrPlaceholder(it) },
            )
            is StoredNotification.EveNotification -> Notification.EveNotification(
                EveNotification(
                    characterIds = characterIds,
                    notificationId = notificationId,
                    senderId = senderId,
                    senderType = senderType,
                    timestamp = Instant.ofEpochMilli(timestamp),
                    type = type,
                    isRead = isRead,
                    text = text,
                ),
            )
        }
    }

    private fun AlertLocationMatch.toDto(): StoredLocationMatch = when (this) {
        is AlertLocationMatch.System -> StoredLocationMatch.System(system.id, distance)
        is AlertLocationMatch.Character -> StoredLocationMatch.Character(characterId, characterName, distance)
    }

    private fun StoredLocationMatch.toDomain(): AlertLocationMatch? {
        return when (this) {
            is StoredLocationMatch.System -> AlertLocationMatch.System(
                solarSystemsRepository.getSystem(systemId) ?: return null,
                distance,
            )
            is StoredLocationMatch.Character -> AlertLocationMatch.Character(characterId, characterName, distance)
        }
    }

    private fun SystemEntity.toDto(): StoredSystemEntity = when (this) {
        is SystemEntity.Character -> StoredSystemEntity.Character(name, characterId, details.toDto())
        is SystemEntity.UnspecifiedCharacter -> StoredSystemEntity.UnspecifiedCharacter(count)
        is SystemEntity.Ship -> StoredSystemEntity.Ship(type.id, count, standing)
        is SystemEntity.Gate -> StoredSystemEntity.Gate(system2.id, isAnsiblex, distanceKm)
        is SystemEntity.Celestial -> StoredSystemEntity.Celestial(celestial.name, distanceKm)
        is SystemEntity.Killmail -> StoredSystemEntity.Killmail(url, ship?.id, typeName, victim.toDto())
        SystemEntity.Wormhole -> StoredSystemEntity.Wormhole
        SystemEntity.Spike -> StoredSystemEntity.Spike
        SystemEntity.Ess -> StoredSystemEntity.Ess
        SystemEntity.Skyhook -> StoredSystemEntity.Skyhook
        SystemEntity.GateCamp -> StoredSystemEntity.GateCamp
        SystemEntity.CombatProbes -> StoredSystemEntity.CombatProbes
        SystemEntity.NoVisual -> StoredSystemEntity.NoVisual
        SystemEntity.Bubbles -> StoredSystemEntity.Bubbles
        else -> error("Unsupported system entity: $this")
    }

    private fun StoredSystemEntity.toDomain(): SystemEntity? {
        return when (this) {
            is StoredSystemEntity.Character -> SystemEntity.Character(name, characterId, details.toDomain())
            is StoredSystemEntity.UnspecifiedCharacter -> SystemEntity.UnspecifiedCharacter(count)
            is StoredSystemEntity.Ship -> SystemEntity.Ship(typesRepository.getTypeOrPlaceholder(typeId), count, standing)
            is StoredSystemEntity.Gate -> SystemEntity.Gate(
                solarSystemsRepository.getSystem(systemId) ?: return null,
                isAnsiblex,
                distanceKm,
            )
            is StoredSystemEntity.Celestial -> SystemEntity.Celestial(
                celestialsRepository.getCelestial(name) ?: return null,
                distanceKm,
            )
            is StoredSystemEntity.Killmail -> SystemEntity.Killmail(
                url,
                shipTypeId?.let { typesRepository.getTypeOrPlaceholder(it) },
                typeName,
                victim.toDomain(),
            )
            StoredSystemEntity.Wormhole -> SystemEntity.Wormhole
            StoredSystemEntity.Spike -> SystemEntity.Spike
            StoredSystemEntity.Ess -> SystemEntity.Ess
            StoredSystemEntity.Skyhook -> SystemEntity.Skyhook
            StoredSystemEntity.GateCamp -> SystemEntity.GateCamp
            StoredSystemEntity.CombatProbes -> SystemEntity.CombatProbes
            StoredSystemEntity.NoVisual -> SystemEntity.NoVisual
            StoredSystemEntity.Bubbles -> SystemEntity.Bubbles
        }
    }

    private fun SystemEntity.KillmailVictim.toDto() = StoredKillmailVictim(
        characterId, details?.toDto(), corporationId, corporationName, corporationTicker,
        allianceId, allianceName, allianceTicker, standing,
    )

    private fun StoredKillmailVictim.toDomain() = SystemEntity.KillmailVictim(
        characterId, details?.toDomain(), corporationId, corporationName, corporationTicker,
        allianceId, allianceName, allianceTicker, standing,
    )

    private fun CharacterDetails.toDto() = StoredCharacterDetails(
        characterId, name, corporationId, corporationName, corporationTicker, corporationRoles,
        allianceId, allianceName, allianceTicker, standing, standingLevel, corporationTitle, title,
        achievementScore, characterLabels, corporationLabels, allianceLabels, birthday.toEpochMilli(),
        factionId, securityStatus,
    )

    private fun StoredCharacterDetails.toDomain() = CharacterDetails(
        characterId, name, corporationId, corporationName, corporationTicker, corporationRoles,
        null, allianceId, allianceName, allianceTicker, standing, standingLevel, corporationTitle, title,
        achievementScore, characterLabels, corporationLabels, allianceLabels, Instant.ofEpochMilli(birthday),
        factionId, securityStatus,
    )
}
