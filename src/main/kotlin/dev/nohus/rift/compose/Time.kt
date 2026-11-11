package dev.nohus.rift.compose

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.format_relative_time_just_now
import dev.nohus.rift.generated.resources.format_relative_time_minutes
import dev.nohus.rift.generated.resources.format_relative_time_seconds
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.utils.getName
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun getRelativeTime(timestamp: Instant, displayTimezone: ZoneId, now: Instant = Instant.now()): String {
    val duration = Duration.between(timestamp, now)
    return if (duration.toSeconds() < 5) {
        "just now"
    } else if (duration.toSeconds() < 60) {
        "${duration.toSeconds()} seconds ago"
    } else if (duration.toMinutes() < 2) {
        "1 minute ago"
    } else if (duration.toMinutes() < 60) {
        "${duration.toMinutes()} minutes ago"
    } else {
        val time = ZonedDateTime.ofInstant(timestamp, displayTimezone)
        val timezoneName = displayTimezone.getName()
        val formatted = if (duration.toHours() < 12) {
            DateTimeFormatter.ofPattern("HH:mm").format(time)
        } else {
            DateTimeFormatter.ofPattern("d MMM, HH:mm").format(time)
        }
        "$formatted $timezoneName"
    }
}

// TODO: Check

// Opt in migrated consumers while preserving the existing EVE notification timestamp presentation
fun getRelativeTimeText(timestamp: Instant, displayTimezone: ZoneId, now: Instant = Instant.now()): UiText {
    val duration = Duration.between(timestamp, now)
    return when {
        duration.toSeconds() < 5 -> Res.string.format_relative_time_just_now.toUiText()
        duration.toSeconds() < 60 -> {
            val seconds = duration.toSeconds()
            Res.plurals.format_relative_time_seconds.toUiText(seconds.toInt(), seconds.toString().toUiTextArgument())
        }
        duration.toMinutes() < 60 -> {
            val minutes = duration.toMinutes()
            Res.plurals.format_relative_time_minutes.toUiText(minutes.toInt(), minutes.toString().toUiTextArgument())
        }
        else -> getRelativeTime(timestamp, displayTimezone, now).toUiText()
    }
}
