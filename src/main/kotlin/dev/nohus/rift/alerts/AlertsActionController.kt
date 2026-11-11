package dev.nohus.rift.alerts

import androidx.compose.ui.text.AnnotatedString
import dev.nohus.rift.alerts.AlertsTriggerController.AlertLocationMatch
import dev.nohus.rift.compose.text.ParseEveFormattedTextUseCase
import dev.nohus.rift.compose.text.toPlainString
import dev.nohus.rift.contacts.ContactsRepository
import dev.nohus.rift.gamelogs.GameLogAction
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.alerts_chat_message_body
import dev.nohus.rift.generated.resources.alerts_chat_message_title
import dev.nohus.rift.generated.resources.alerts_intel_character_distance_body
import dev.nohus.rift.generated.resources.alerts_intel_named_character_distance_body
import dev.nohus.rift.generated.resources.alerts_intel_same_character_system_body
import dev.nohus.rift.generated.resources.alerts_intel_same_named_character_system_body
import dev.nohus.rift.generated.resources.alerts_intel_same_system_body
import dev.nohus.rift.generated.resources.alerts_intel_system_distance_body
import dev.nohus.rift.generated.resources.alerts_intel_system_title
import dev.nohus.rift.generated.resources.alerts_jabber_message_title
import dev.nohus.rift.generated.resources.alerts_ping_announcement_title
import dev.nohus.rift.generated.resources.alerts_ping_custom_title
import dev.nohus.rift.generated.resources.alerts_ping_fleet_body
import dev.nohus.rift.generated.resources.alerts_ping_fleet_doctrine_body
import dev.nohus.rift.generated.resources.alerts_ping_fleet_formup_body
import dev.nohus.rift.generated.resources.alerts_ping_fleet_formup_doctrine_body
import dev.nohus.rift.generated.resources.alerts_ping_fleet_title
import dev.nohus.rift.generated.resources.alerts_ping_peacetime_title
import dev.nohus.rift.generated.resources.alerts_ping_strategic_title
import dev.nohus.rift.generated.resources.alerts_planet_attention_character_body
import dev.nohus.rift.intel.state.SystemEntity
import dev.nohus.rift.jabber.JabberInputModel
import dev.nohus.rift.localization.AppLocale
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.UiTextArgument
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.logs.parse.ChannelChatMessage
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.notifications.GameActionNotificationType
import dev.nohus.rift.notifications.IntelNotificationReportType
import dev.nohus.rift.notifications.IntelNotificationTitle
import dev.nohus.rift.notifications.NotificationText
import dev.nohus.rift.notifications.NotificationsController
import dev.nohus.rift.notifications.NotificationsController.Notification
import dev.nohus.rift.notifications.getText
import dev.nohus.rift.notifications.getTitleText
import dev.nohus.rift.notifications.getMessageText
import dev.nohus.rift.notifications.eve.EveNotification
import dev.nohus.rift.notifications.eve.EveNotificationTextParser
import dev.nohus.rift.notifications.system.SendNotificationUseCase
import dev.nohus.rift.pings.FormupLocation
import dev.nohus.rift.pings.PapType
import dev.nohus.rift.pings.PingModel
import dev.nohus.rift.planetaryindustry.PlanetaryIndustryRepository.ColonyItem
import dev.nohus.rift.push.PushNotificationController
import dev.nohus.rift.repositories.NotificationTypesRepository
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.repositories.SolarSystemsRepository.MapSolarSystem
import dev.nohus.rift.repositories.character.CharacterDetailsRepository
import dev.nohus.rift.repositories.character.CharactersRepository
import dev.nohus.rift.types.TypeDetails
import dev.nohus.rift.types.TypesRepository
import dev.nohus.rift.types.TypesRepository.Type
import dev.nohus.rift.utils.sound.SoundPlayer
import dev.nohus.rift.utils.sound.SoundPlayer.SoundSource
import dev.nohus.rift.utils.sound.SoundsRepository
import dev.nohus.rift.windowing.WindowManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import java.util.Locale
import kotlin.io.path.absolutePathString

@Single
class AlertsActionController(
    private val appLocale: AppLocale,
    private val parseEveFormattedTextUseCase: ParseEveFormattedTextUseCase,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val pushNotificationController: PushNotificationController,
    private val soundPlayer: SoundPlayer,
    private val soundsRepository: SoundsRepository,
    private val notificationsController: NotificationsController,
    private val solarSystemsRepository: SolarSystemsRepository,
    private val typesRepository: TypesRepository,
    private val charactersRepository: CharactersRepository,
    private val characterDetailsRepository: CharacterDetailsRepository,
    private val windowManager: WindowManager,
    private val contactsRepository: ContactsRepository,
    private val eveNotificationTextParser: EveNotificationTextParser,
    private val notificationTypesRepository: NotificationTypesRepository,
) {

    private val scope = CoroutineScope(Job())
    private var lastSoundTimestamp = Instant.EPOCH

    fun triggerIntelAlert(
        alert: Alert,
        matchingEntities: List<Pair<IntelReportType, List<SystemEntity>>>,
        entities: List<SystemEntity>,
        locationMatch: AlertLocationMatch,
        solarSystem: MapSolarSystem,
    ) {
        val title = getNotificationTitle(matchingEntities)
        val plainTextTitle = Res.string.alerts_intel_system_title.toUiText(title.getText().toUiTextArgument(), solarSystem.name.toUiTextArgument())
        val message = getNotificationMessage(locationMatch)
        val notification = Notification.IntelNotification(title, locationMatch, entities, solarSystem)
        triggerAlert(alert, notification, plainTextTitle, message)
    }

    fun triggerGameActionAlert(alert: Alert, action: GameLogAction, characterId: Int) {
        val text = getNotificationText(action)
        val title = text.getTitleText()
        val message = text.getMessageText()
        val type = getNotificationItemType(action)
        val iconUrl = if (type != null) {
            "https://images.evetech.net/types/${type.id}/icon"
        } else if (action is GameLogAction.InvitedToConversation && action.characterId != null) {
            "https://images.evetech.net/characters/${action.characterId}/portrait"
        } else {
            null
        }
        if (action is GameLogAction.InvitedToConversation && action.characterId != null) {
            scope.launch {
                val notification = getGameActionNotification(action, text, characterId, type)
                triggerAlert(alert, notification, title, message, iconUrl, isMessageFormatted = text is NotificationText.GameAction)
            }
        } else {
            val notification = Notification.TextNotification(text, characterId, type)
            triggerAlert(alert, notification, title, message, iconUrl, isMessageFormatted = text is NotificationText.GameAction)
        }
    }

    private suspend fun getGameActionNotification(
        action: GameLogAction.InvitedToConversation,
        text: NotificationText,
        characterId: Int,
        type: Type?,
    ): Notification.TextNotification {
        val inviterCharacterId = action.characterId ?: throw IllegalArgumentException("Conversation invite has no character ID")
        val details = characterDetailsRepository.getCharacterDetails(Originator.Alerts, inviterCharacterId)
        return Notification.TextNotification(
            text = text,
            characterId = characterId,
            type = type,
            relatedCharacter = Notification.Character(
                id = inviterCharacterId,
                name = details?.name ?: action.characterName,
                standing = details?.standingLevel,
            ),
        )
    }

    fun triggerChatMessageAlert(alert: Alert, chatMessage: ChannelChatMessage, highlight: String?) {
        scope.launch {
            val characterId = charactersRepository.getCharacterId(Originator.Alerts, chatMessage.chatMessage.author)
            val standing = if (characterId != null) {
                characterDetailsRepository.getCharacterDetails(Originator.Alerts, characterId)?.standingLevel
            } else {
                null
            }
            val title = Res.string.alerts_chat_message_title.toUiText(chatMessage.metadata.channelName.toUiTextArgument())
            val message = Res.string.alerts_chat_message_body.toUiText(
                chatMessage.chatMessage.author.toUiTextArgument(),
                chatMessage.chatMessage.message.toUiTextArgument(),
            )
            val notification = Notification.ChatMessageNotification(
                channel = chatMessage.metadata.channelName,
                messages = listOf(
                    Notification.ChatMessage(
                        message = chatMessage.chatMessage.message,
                        highlight = highlight,
                        sender = chatMessage.chatMessage.author,
                        senderCharacterId = characterId,
                        senderStanding = standing,
                    ),
                ),
            )
            triggerAlert(alert, notification, title, message)
        }
    }

    fun triggerJabberMessageAlert(
        alert: Alert,
        chat: String,
        sender: String,
        message: String,
        highlight: String?,
        isDirectMessage: Boolean,
    ) {
        scope.launch {
            val title = Res.string.alerts_jabber_message_title.toUiText(chat.toUiTextArgument())
            val notification = Notification.JabberMessageNotification(
                chat = chat,
                message = message,
                highlight = highlight,
                sender = sender,
            )
            val inputModel = if (isDirectMessage) {
                JabberInputModel.DirectMessage(user = sender)
            } else {
                JabberInputModel.Channel(channel = chat)
            }
            triggerAlert(alert, notification, title, message.toUiText(), jabberInputModel = inputModel)
        }
    }

    fun triggerJabberPingAlert(alert: Alert, ping: PingModel) {
        val [title, message] = when (ping) {
            is PingModel.FleetPing -> {
                val title = when (ping.papType) {
                    PapType.Peacetime -> Res.string.alerts_ping_peacetime_title.toUiText(ping.fleetCommander.name.toUiTextArgument())
                    PapType.Strategic -> Res.string.alerts_ping_strategic_title.toUiText(ping.fleetCommander.name.toUiTextArgument())
                    is PapType.Text -> Res.string.alerts_ping_custom_title.toUiText(ping.papType.text.toUiTextArgument(), ping.fleetCommander.name.toUiTextArgument())
                    else -> Res.string.alerts_ping_fleet_title.toUiText(ping.fleetCommander.name.toUiTextArgument())
                }
                val formup = ping.formupLocations.takeIf { it.isNotEmpty() }?.joinToString {
                    when (it) {
                        is FormupLocation.System -> solarSystemsRepository.getSystemName(it.id) ?: "${it.id}"
                        is FormupLocation.Text -> it.text
                    }
                }
                val doctrine = ping.doctrine?.text
                val message = when {
                    formup != null && doctrine != null -> Res.string.alerts_ping_fleet_formup_doctrine_body.toUiText(formup.toUiTextArgument(), doctrine.toUiTextArgument(), ping.description.toUiTextArgument())
                    formup != null -> Res.string.alerts_ping_fleet_formup_body.toUiText(formup.toUiTextArgument(), ping.description.toUiTextArgument())
                    doctrine != null -> Res.string.alerts_ping_fleet_doctrine_body.toUiText(doctrine.toUiTextArgument(), ping.description.toUiTextArgument())
                    else -> Res.string.alerts_ping_fleet_body.toUiText(ping.description.toUiTextArgument())
                }
                title to message
            }
            is PingModel.PlainText -> {
                Res.string.alerts_ping_announcement_title.toUiText(ping.sender.toString().toUiTextArgument()) to ping.text.toUiText()
            }
        }
        val iconUrl = (ping as? PingModel.FleetPing)?.fleetCommander?.id?.let { id ->
            "https://images.evetech.net/characters/$id/portrait"
        }
        triggerAlert(alert, null, title, message, iconUrl)
    }

    fun triggerPlanetaryIndustryAlert(alert: Alert, colonyItem: ColonyItem) {
        scope.launch {
            val duration = Duration.between(Instant.now(), colonyItem.ffwdColony.currentSimTime)
            val text = NotificationText.PlanetaryIndustry(
                planetName = colonyItem.colony.planet.name,
                attentionIn = duration.takeIf { it >= Duration.ofMinutes(5) },
            )
            val title = text.getTitleText()
            val message = text.getMessageText()
            val systemMessage = colonyItem.characterName?.let { characterName ->
                Res.string.alerts_planet_attention_character_body.toUiText(
                    characterName.toUiTextArgument(),
                    message.toUiTextArgument(),
                )
            } ?: message
            val planetType = typesRepository.getType(colonyItem.colony.planet.type.typeId)
            val notification = Notification.TextNotification(text, colonyItem.colony.characterId, planetType)
            triggerAlert(alert, notification, title, systemMessage)
        }
    }

    fun triggerEveNotificationAlert(alert: Alert, eveNotification: EveNotification, typeDetails: TypeDetails) {
        val receiverId = eveNotification.characterIds.firstOrNull()
        val parsedNotification = eveNotification.text?.let { text ->
            eveNotificationTextParser.parse(
                type = eveNotification.type,
                text = text,
                typeDetails = typeDetails,
                senderId = eveNotification.senderId,
                receiverId = receiverId?.toLong() ?: 0L,
                createdTimestamp = eveNotification.timestamp,
            )
        }
        val title = parsedNotification?.subject?.toPlainString()
            ?: notificationTypesRepository.getTitle(eveNotification.type)
            ?: eveNotification.type
        val message = parsedNotification?.body?.toPlainString() ?: "No notification details"
        val notification = Notification.EveNotification(eveNotification)
        triggerAlert(alert, notification, title, message)
    }

    private fun triggerAlert(
        alert: Alert,
        notification: Notification?,
        title: String,
        message: String,
        iconUrl: String? = null,
        jabberInputModel: JabberInputModel? = null,
    ) {
        triggerAlert(alert, notification, title.toUiText(), message.toUiText(), iconUrl, jabberInputModel)
    }

    private fun triggerAlert(
        alert: Alert,
        notification: Notification?,
        title: UiText,
        message: UiText,
        iconUrl: String? = null,
        jabberInputModel: JabberInputModel? = null,
        isMessageFormatted: Boolean = false,
    ) {
        alert.actions.forEach { action ->
            when (action) {
                AlertAction.RiftNotification -> if (notification != null) sendRiftNotification(notification, alert)
                AlertAction.SystemNotification -> sendSystemNotification(title, message, isMessageFormatted)
                AlertAction.PushNotification -> sendPushNotification(title, message, iconUrl, isMessageFormatted)
                is AlertAction.Sound -> {
                    withSoundCooldown {
                        val sound = soundsRepository.getSounds().firstOrNull { it.id == action.id } ?: return@withSoundCooldown
                        soundPlayer.play(sound.resource, SoundSource.Alert)
                    }
                }
                is AlertAction.CustomSound -> {
                    withSoundCooldown {
                        soundPlayer.playFile(action.path, SoundSource.Alert)
                    }
                }
                AlertAction.ShowPing -> windowManager.onWindowOpen(WindowManager.RiftWindow.Pings)
                AlertAction.ShowJabberMessage -> jabberInputModel?.let {
                    windowManager.onWindowOpen(WindowManager.RiftWindow.Jabber, inputModel = it)
                }
                AlertAction.ShowColonies -> windowManager.onWindowOpen(WindowManager.RiftWindow.PlanetaryIndustry)
            }
        }
    }

    private fun withSoundCooldown(block: () -> Unit) {
        val duration = Duration.between(lastSoundTimestamp, Instant.now())
        if (duration >= Duration.ofMillis(200)) {
            lastSoundTimestamp = Instant.now()
            block()
        }
    }

    private fun getNotificationTitle(matchingEntities: List<Pair<IntelReportType, List<SystemEntity>>>): IntelNotificationTitle.Report {
        return matchingEntities.let {
            if (it.any { it.first is IntelReportType.LabeledContacts }) {
                val [labeledContacts, entities] = it.first { it.first is IntelReportType.LabeledContacts }
                val expectedLabels = (labeledContacts as IntelReportType.LabeledContacts).labels
                val matchedLabels = entities.filterIsInstance<SystemEntity.Character>().map { character ->
                    val ids = listOfNotNull(character.characterId, character.details.corporationId, character.details.allianceId)
                    contactsRepository.getLabels(ids).filter { label ->
                        expectedLabels.any { expectedLabel -> label.owner.id == expectedLabel.ownerId && label.id == expectedLabel.id }
                    }
                }.flatten().map { it.name }.distinct()
                IntelNotificationTitle.Report(IntelNotificationReportType.LabeledContacts, matchedLabels)
            } else if (it.any { it.first is IntelReportType.SpecificCharacters }) {
                IntelNotificationTitle.Report(IntelNotificationReportType.SpecificCharacter)
            } else if (it.any { it.first is IntelReportType.SpecificShipClasses }) {
                IntelNotificationTitle.Report(IntelNotificationReportType.SpecificShipClass)
            } else if (it.any { it.first is IntelReportType.AnyCharacter }) {
                IntelNotificationTitle.Report(IntelNotificationReportType.Hostile)
            } else if (it.any { it.first is IntelReportType.GateCamp }) {
                IntelNotificationTitle.Report(IntelNotificationReportType.GateCamp)
            } else if (it.any { it.first is IntelReportType.AnyShip }) {
                IntelNotificationTitle.Report(IntelNotificationReportType.HostileShip)
            } else if (it.any { it.first is IntelReportType.Bubbles }) {
                IntelNotificationTitle.Report(IntelNotificationReportType.Bubbles)
            } else if (it.any { it.first is IntelReportType.Wormhole }) {
                IntelNotificationTitle.Report(IntelNotificationReportType.Wormhole)
            } else {
                IntelNotificationTitle.Report(IntelNotificationReportType.Intel)
            }
        }
    }

    /**
     * Only used for text notifications which cannot show rich content
     */
    private fun getNotificationMessage(locationMatch: AlertLocationMatch): UiText {
        return when (locationMatch) {
            is AlertLocationMatch.System -> {
                when (val distance = locationMatch.distance) {
                    0 -> Res.string.alerts_intel_same_system_body.toUiText(locationMatch.system.name.toUiTextArgument())
                    else -> Res.plurals.alerts_intel_system_distance_body.toUiText(distance, distance.toUiTextArgument(), locationMatch.system.name.toUiTextArgument())
                }
            }

            is AlertLocationMatch.Character -> {
                if (locationMatch.characterName != null) {
                    when (val distance = locationMatch.distance) {
                        0 -> Res.string.alerts_intel_same_named_character_system_body.toUiText(locationMatch.characterName.toUiTextArgument())
                        else -> Res.plurals.alerts_intel_named_character_distance_body.toUiText(distance, distance.toUiTextArgument(), locationMatch.characterName.toUiTextArgument())
                    }
                } else {
                    when (val distance = locationMatch.distance) {
                        0 -> Res.string.alerts_intel_same_character_system_body.toUiText()
                        else -> Res.plurals.alerts_intel_character_distance_body.toUiText(distance, distance.toUiTextArgument())
                    }
                }
            }
        }
    }

    private fun getNotificationText(action: GameLogAction): NotificationText {
        return when (action) {
            is GameLogAction.UnderAttack -> NotificationText.GameAction(GameActionNotificationType.UnderAttack, action.target)
            is GameLogAction.Attacking -> NotificationText.GameAction(GameActionNotificationType.Attacking, action.target)
            is GameLogAction.BeingWarpScrambled -> NotificationText.GameAction(GameActionNotificationType.WarpScrambled, action.target)
            is GameLogAction.Decloaked -> NotificationText.GameAction(GameActionNotificationType.Decloaked, action.by)
            is GameLogAction.CombatStopped -> NotificationText.GameAction(GameActionNotificationType.CombatStopped, action.target)
            is GameLogAction.RanOutOfCharges -> NotificationText.GameAction(GameActionNotificationType.OutOfCharges, action.module)
            is GameLogAction.InvitedToConversation -> NotificationText.GameAction(GameActionNotificationType.ConversationInvite, action.characterName)
            is GameLogAction.AsteroidDepleted -> NotificationText.GameAction(GameActionNotificationType.AsteroidDepleted, action.module)
            GameLogAction.CloneJumping -> throw IllegalStateException("Not used")
            is GameLogAction.Generic -> NotificationText.Literal(
                action.type.replaceFirstChar { it.titlecase(Locale.US) },
                AnnotatedString(action.message),
            )
        }
    }

    private fun getNotificationItemType(action: GameLogAction): Type? {
        return when (action) {
            is GameLogAction.UnderAttack -> typesRepository.getType(action.target)
            is GameLogAction.Attacking -> typesRepository.getType(action.target)
            is GameLogAction.BeingWarpScrambled -> typesRepository.getType(action.target)
            is GameLogAction.Decloaked -> typesRepository.getType(action.by)
            is GameLogAction.CombatStopped -> typesRepository.getType(action.target)
            GameLogAction.CloneJumping -> throw IllegalStateException("Not used")
            is GameLogAction.RanOutOfCharges -> typesRepository.getType(action.module)
            is GameLogAction.InvitedToConversation -> null
            is GameLogAction.AsteroidDepleted -> typesRepository.getType(action.module)
            is GameLogAction.Generic -> typesRepository.findTypeInText(action.message)
        }
    }

    private fun sendRiftNotification(notification: Notification, alert: Alert) {
        notificationsController.show(notification, alert = alert)
    }

    private suspend fun resolveNotificationMessage(message: UiText, isMessageFormatted: Boolean): String {
        val resolvedMessage = message.resolve(appLocale)
        return if (isMessageFormatted) {
            parseEveFormattedTextUseCase(resolvedMessage, decodeEntities = true).toPlainString()
        } else {
            resolvedMessage
        }
    }

    private fun sendSystemNotification(title: UiText, message: UiText, isMessageFormatted: Boolean) {
        scope.launch {
            val resolvedTitle = title.resolve(appLocale)
            val resolvedMessage = resolveNotificationMessage(message, isMessageFormatted)
            sendNotificationUseCase(
                appName = "RIFT Intel Fusion Tool",
                iconPath = Path.of("icon/icon-512.png").absolutePathString(),
                summary = resolvedTitle,
                body = resolvedMessage,
                timeout = 8,
            )
        }
    }

    private fun sendPushNotification(title: UiText, message: UiText, iconUrl: String?, isMessageFormatted: Boolean) {
        scope.launch {
            val plainMessage = if (isMessageFormatted) {
                resolveNotificationMessage(message, isMessageFormatted).toUiText()
            } else {
                message
            }
            pushNotificationController.sendPushNotification(title, plainMessage, iconUrl)
        }
    }
}
