package dev.nohus.rift.notifications.history

import dev.nohus.rift.alerts.Alert
import dev.nohus.rift.notifications.NotificationsController.Notification
import java.time.Instant

data class NotificationHistoryItem(
    val timestamp: Instant,
    val notification: Notification,
    val alert: Alert? = null,
    val reason: NotificationHistoryReason? = null,
)
