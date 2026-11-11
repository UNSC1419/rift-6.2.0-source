package dev.nohus.rift.notifications.history

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.notification_history_sovereignty_import_reason
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText

sealed interface NotificationHistoryReason {
    data class Literal(val text: String) : NotificationHistoryReason
    data object SovereigntyUpgradesImported : NotificationHistoryReason
}

fun NotificationHistoryReason.toUiText(): UiText = when (this) {
    is NotificationHistoryReason.Literal -> text.toUiText()
    NotificationHistoryReason.SovereigntyUpgradesImported -> Res.string.notification_history_sovereignty_import_reason.toUiText()
}
