package dev.nohus.rift.notifications.eve

import dev.nohus.rift.network.esi.models.NotificationSenderType
import java.time.Instant

data class EveNotification(
    val characterIds: List<Int>,
    val notificationId: Long,
    val senderId: Long,
    val senderType: NotificationSenderType,
    val timestamp: Instant,
    val type: String,
    val isRead: Boolean?,
    val text: String?,
)
