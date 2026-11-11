package dev.nohus.rift.postkillmail

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.killmail_delay_eight_hours
import dev.nohus.rift.generated.resources.killmail_delay_no_delay
import dev.nohus.rift.generated.resources.killmail_delay_one_day
import dev.nohus.rift.generated.resources.killmail_delay_one_hour
import dev.nohus.rift.generated.resources.killmail_delay_three_days
import dev.nohus.rift.generated.resources.killmail_delay_three_hours
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText

enum class KillmailPostingDelay(
    val value: Int,
    val displayText: UiText,
) {
    NoDelay(0, Res.string.killmail_delay_no_delay.toUiText()),
    OneHour(1, Res.string.killmail_delay_one_hour.toUiText()),
    ThreeHours(2, Res.string.killmail_delay_three_hours.toUiText()),
    EightHours(3, Res.string.killmail_delay_eight_hours.toUiText()),
    OneDay(4, Res.string.killmail_delay_one_day.toUiText()),
    ThreeDays(5, Res.string.killmail_delay_three_days.toUiText()),
    ;

    companion object {
        fun fromValue(value: Int): KillmailPostingDelay {
            return entries.firstOrNull { it.value == value } ?: NoDelay
        }
    }
}
