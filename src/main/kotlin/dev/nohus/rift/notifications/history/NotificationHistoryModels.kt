package dev.nohus.rift.notifications.history

import dev.nohus.rift.alerts.Alert
import dev.nohus.rift.network.esi.models.NotificationSenderType
import dev.nohus.rift.notifications.GameActionNotificationType
import dev.nohus.rift.notifications.IntelNotificationReportType
import dev.nohus.rift.standings.Standing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoredNotificationHistoryItem(
    val timestamp: Long,
    val notification: StoredNotification,
    val alert: Alert? = null,
    val reason: String? = null,
    val reasonId: StoredNotificationReason? = null,
)

@Serializable
enum class StoredNotificationReason {
    @SerialName("sovereignty_upgrades_imported")
    SovereigntyUpgradesImported,
}

@Serializable
sealed interface StoredNotification {
    @Serializable
    @SerialName("Text")
    data class Text(
        val title: String,
        val message: String,
        val annotations: List<StoredAnnotation>,
        val characterId: Int?,
        val typeId: Int?,
        val relatedCharacter: StoredRelatedCharacter?,
    ) : StoredNotification

    @Serializable
    @SerialName("GameAction")
    data class GameAction(
        val actionType: GameActionNotificationType,
        val subject: String,
        val characterId: Int?,
        val typeId: Int?,
        val relatedCharacter: StoredRelatedCharacter?,
    ) : StoredNotification

    @Serializable
    @SerialName("PlanetaryIndustry")
    data class PlanetaryIndustry(
        val planetName: String,
        val attentionInSeconds: Long?,
        val characterId: Int?,
        val typeId: Int?,
        val relatedCharacter: StoredRelatedCharacter?,
    ) : StoredNotification

    @Serializable
    @SerialName("ChatMessage")
    data class ChatMessage(val channel: String, val messages: List<StoredChatMessage>) : StoredNotification

    @Serializable
    @SerialName("JabberMessage")
    data class JabberMessage(
        val chat: String,
        val message: String,
        val highlight: String?,
        val sender: String,
    ) : StoredNotification

    @Serializable
    @SerialName("Intel")
    data class Intel(
        val title: String,
        val locationMatch: StoredLocationMatch,
        val systemEntities: List<StoredSystemEntity>,
        val solarSystemId: Int,
    ) : StoredNotification

    @Serializable
    @SerialName("IntelReport")
    data class IntelReport(
        val reportType: IntelNotificationReportType,
        val labels: List<String>,
        val locationMatch: StoredLocationMatch,
        val systemEntities: List<StoredSystemEntity>,
        val solarSystemId: Int,
    ) : StoredNotification

    @Serializable
    @SerialName("SovereigntyUpgradeImport")
    data class SovereigntyUpgradeImport(
        val systemId: Int,
        val upgradeTypeIds: List<Int>,
    ) : StoredNotification

    @Serializable
    @SerialName("EveNotification")
    data class EveNotification(
        val characterIds: List<Int> = emptyList(),
        val notificationId: Long,
        val senderId: Long,
        val senderType: NotificationSenderType,
        val timestamp: Long,
        @SerialName("notificationType")
        val type: String,
        val isRead: Boolean?,
        val text: String?,
    ) : StoredNotification
}

@Serializable
data class StoredAnnotation(val tag: String, val value: String, val start: Int, val end: Int)

@Serializable
data class StoredRelatedCharacter(val id: Int, val name: String, val standing: Standing?)

@Serializable
data class StoredChatMessage(
    val message: String,
    val highlight: String?,
    val sender: String,
    val senderCharacterId: Int?,
    val senderStanding: Standing?,
)

@Serializable
sealed interface StoredLocationMatch {
    @Serializable
    @SerialName("System")
    data class System(val systemId: Int, val distance: Int) : StoredLocationMatch

    @Serializable
    @SerialName("Character")
    data class Character(val characterId: Int, val characterName: String?, val distance: Int) : StoredLocationMatch
}

@Serializable
sealed interface StoredSystemEntity {
    @Serializable
    @SerialName("Character")
    data class Character(val name: String, val characterId: Int, val details: StoredCharacterDetails) : StoredSystemEntity

    @Serializable
    @SerialName("UnspecifiedCharacter")
    data class UnspecifiedCharacter(val count: Int) : StoredSystemEntity

    @Serializable
    @SerialName("Ship")
    data class Ship(val typeId: Int, val count: Int, val standing: Standing?) : StoredSystemEntity

    @Serializable
    @SerialName("Gate")
    data class Gate(val systemId: Int, val isAnsiblex: Boolean, val distanceKm: Int?) : StoredSystemEntity

    @Serializable
    @SerialName("Celestial")
    data class Celestial(val name: String, val distanceKm: Int) : StoredSystemEntity

    @Serializable
    @SerialName("Killmail")
    data class Killmail(
        val url: String,
        val shipTypeId: Int?,
        val typeName: String?,
        val victim: StoredKillmailVictim,
    ) : StoredSystemEntity

    @Serializable
    @SerialName("Wormhole")
    data object Wormhole : StoredSystemEntity

    @Serializable
    @SerialName("Spike")
    data object Spike : StoredSystemEntity

    @Serializable
    @SerialName("Ess")
    data object Ess : StoredSystemEntity

    @Serializable
    @SerialName("Skyhook")
    data object Skyhook : StoredSystemEntity

    @Serializable
    @SerialName("GateCamp")
    data object GateCamp : StoredSystemEntity

    @Serializable
    @SerialName("CombatProbes")
    data object CombatProbes : StoredSystemEntity

    @Serializable
    @SerialName("NoVisual")
    data object NoVisual : StoredSystemEntity

    @Serializable
    @SerialName("Bubbles")
    data object Bubbles : StoredSystemEntity
}

@Serializable
data class StoredKillmailVictim(
    val characterId: Int?,
    val details: StoredCharacterDetails?,
    val corporationId: Int?,
    val corporationName: String?,
    val corporationTicker: String?,
    val allianceId: Int?,
    val allianceName: String?,
    val allianceTicker: String?,
    val standing: Standing?,
)

@Serializable
data class StoredCharacterDetails(
    val characterId: Int,
    val name: String,
    val corporationId: Int,
    val corporationName: String,
    val corporationTicker: String,
    val corporationRoles: List<String>,
    val allianceId: Int?,
    val allianceName: String?,
    val allianceTicker: String?,
    val standing: Float,
    val standingLevel: Standing,
    val corporationTitle: String?,
    val title: String?,
    val achievementScore: Int,
    val characterLabels: List<String>,
    val corporationLabels: List<String>,
    val allianceLabels: List<String>,
    val birthday: Long,
    val factionId: Int?,
    val securityStatus: Double?,
)
