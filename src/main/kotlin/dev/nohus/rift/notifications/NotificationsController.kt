package dev.nohus.rift.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.nohus.rift.alerts.Alert
import dev.nohus.rift.alerts.AlertsTriggerController.AlertLocationMatch
import dev.nohus.rift.intel.state.SystemEntity
import dev.nohus.rift.notifications.NotificationsController.Notification.ChatMessageNotification
import dev.nohus.rift.notifications.history.NotificationHistoryReason
import dev.nohus.rift.notifications.history.NotificationHistoryRepository
import dev.nohus.rift.repositories.SolarSystemsRepository.MapSolarSystem
import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.standings.Standing
import dev.nohus.rift.types.TypesRepository.Type
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.milliseconds
import dev.nohus.rift.notifications.eve.EveNotification as EveNotificationModel

@Single
class NotificationsController(
    private val settings: Settings,
    private val notificationHistoryRepository: NotificationHistoryRepository,
) {

    sealed interface Notification {
        data class TextNotification(
            val text: NotificationText,
            val characterId: Int?, // Associated character
            val type: Type?, // Associated type ID
            val relatedCharacter: Character? = null,
        ) : Notification {
            companion object {
                const val STYLE_TAG = "Style"
                const val STYLE_VALUE = "Primary"
            }
        }

        data class Character(
            val id: Int,
            val name: String,
            val standing: Standing?,
        )

        data class ChatMessageNotification(
            val channel: String,
            val messages: List<ChatMessage>,
        ) : Notification

        data class ChatMessage(
            val message: String,
            val highlight: String?,
            val sender: String,
            val senderCharacterId: Int?,
            val senderStanding: Standing?,
        )

        data class JabberMessageNotification(
            val chat: String,
            val message: String,
            val highlight: String?,
            val sender: String,
        ) : Notification

        data class IntelNotification(
            val title: IntelNotificationTitle,
            val locationMatch: AlertLocationMatch,
            val systemEntities: List<SystemEntity>,
            val solarSystem: MapSolarSystem,
        ) : Notification

        data class SovereigntyUpgradeImportNotification(
            val system: MapSolarSystem,
            val upgrades: List<Type>,
        ) : Notification

        data class EveNotification(
            val notification: EveNotificationModel,
        ) : Notification
    }

    private val maxShownNotifications = 25
    private val notificationDuration = 8000L
    private val notificationsStateFlow = MutableStateFlow<List<Notification>>(emptyList())

    fun show(notification: Notification, alert: Alert? = null, reason: NotificationHistoryReason? = null) {
        notificationHistoryRepository.add(notification, alert, reason)
        notificationsStateFlow.update {
            collapseChatNotifications(it + notification).takeLast(maxShownNotifications)
        }
    }

    private fun collapseChatNotifications(notifications: List<Notification>): List<Notification> {
        return mutableListOf<Notification>().apply {
            for (notification in notifications) {
                val previous = lastOrNull()
                if (previous is ChatMessageNotification && notification is ChatMessageNotification &&
                    previous.channel == notification.channel
                ) {
                    this[lastIndex] = ChatMessageNotification(
                        channel = notification.channel,
                        messages = previous.messages + notification.messages,
                    )
                } else {
                    this += notification
                }
            }
        }
    }

    private fun hide(notification: Notification) {
        notificationsStateFlow.update { it - notification }
    }

    @Composable
    fun composeNotification() {
        val notifications by notificationsStateFlow.collectAsState()
        if (notifications.isNotEmpty()) {
            key(notifications) {
                var isHeld by remember { mutableStateOf(false) }
                LaunchedEffect(notifications, isHeld) {
                    if (!isHeld) {
                        delay(notificationDuration.milliseconds)
                        notificationsStateFlow.update { emptyList() }
                    }
                }
                NotificationWindow(
                    notifications = notifications,
                    position = settings.notificationPosition,
                    onHoldDisappearance = { isHeld = it },
                    onCloseClick = { hide(it) },
                )
            }
        }
    }
}
