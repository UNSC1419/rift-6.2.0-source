package dev.nohus.rift.utils

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.format_duration_days
import dev.nohus.rift.generated.resources.format_duration_days_compact
import dev.nohus.rift.generated.resources.format_duration_hours
import dev.nohus.rift.generated.resources.format_duration_hours_compact
import dev.nohus.rift.generated.resources.format_duration_join_and
import dev.nohus.rift.generated.resources.format_duration_join_four
import dev.nohus.rift.generated.resources.format_duration_join_three
import dev.nohus.rift.generated.resources.format_duration_join_two
import dev.nohus.rift.generated.resources.format_duration_minutes
import dev.nohus.rift.generated.resources.format_duration_minutes_compact
import dev.nohus.rift.generated.resources.format_duration_seconds_compact
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.UiTextArgument
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val compactNumberFormatters = ThreadLocal.withInitial { mutableMapOf<Triple<Locale, Boolean, Int>, NumberFormat>() }
private val eveTime = ZoneId.of("UTC")
private val numberFormatters = ThreadLocal.withInitial { mutableMapOf<Pair<Locale, Boolean>, NumberFormat>() }
private val dateFormatter = DateTimeFormatter
    .ISO_LOCAL_DATE
    .withLocale(Locale.ROOT)
private val dateFormatterWithDateTime = DateTimeFormatter.ofPattern("MMM dd, HH:mm:ss", Locale.ROOT)
private val dateFormatterWithDateTime2 = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm", Locale.ROOT)
private val dateFormatterWithTime = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ROOT)

fun formatIskCompact(number: Long, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String = "${formatNumberCompact(number, locale)} ISK"
fun formatIskCompact(number: Double, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String = "${formatNumberCompact(number, locale)} ISK"
fun formatIsk(number: Long, withCents: Boolean, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String = "${formatNumber(number, withCents, locale)} ISK"
fun formatIsk(number: Double, withCents: Boolean, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String = "${formatNumber(number, withCents, locale)} ISK"
fun formatIskReadable(number: Long, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String = "${formatNumberReadable(number.toDouble(), isCompact = false, locale = locale)} ISK"
fun formatIskReadable(number: Double, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String = "${formatNumberReadable(number, isCompact = false, locale = locale)} ISK"

fun formatNumberCompact(number: Int, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String = formatNumberReadable(number.toDouble(), isCompact = true, locale = locale)
fun formatNumberCompact(number: Long, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String = formatNumberReadable(number.toDouble(), isCompact = true, locale = locale)
fun formatNumberCompact(number: Double, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String = formatNumberReadable(number, isCompact = true, locale = locale)
fun formatNumber(number: Int, withDecimals: Boolean = false, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String = getFormatter(withDecimals, locale).format(number)
fun formatNumber(number: Long, withDecimals: Boolean = false, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String = getFormatter(withDecimals, locale).format(number)
fun formatNumber(number: Double, withDecimals: Boolean = false, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String = getFormatter(withDecimals, locale).format(number)

private fun getFormatter(withDecimals: Boolean, locale: Locale): NumberFormat {
    return numberFormatters.get().getOrPut(locale to withDecimals) {
        NumberFormat.getInstance(locale).apply {
            minimumFractionDigits = if (withDecimals) 2 else 0
            maximumFractionDigits = if (withDecimals) 2 else 0
        }
    }
}

private fun formatNumberReadable(number: Double, significantDigits: Int = 3, isCompact: Boolean, locale: Locale): String {
    val rounded = roundToSignificant(number, significantDigits)
    if (!rounded.isFinite()) return NumberFormat.getInstance(locale).format(rounded)
    if (kotlin.math.abs(rounded) < 1000) {
        return NumberFormat.getInstance(locale).apply {
            maximumFractionDigits = BigDecimal.valueOf(rounded).scale().coerceAtLeast(0)
        }.format(rounded)
    }
    val formatter = compactNumberFormatters.get().getOrPut(Triple(locale, isCompact, significantDigits)) {
        NumberFormat.getCompactNumberInstance(locale, if (isCompact) NumberFormat.Style.SHORT else NumberFormat.Style.LONG).apply {
            maximumFractionDigits = significantDigits - 1
            roundingMode = RoundingMode.HALF_UP
        }
    }
    return formatter.format(rounded)
}

/**
 * Rounds a number to a certain number of most significant digits
 */
fun roundToSignificant(number: Double, significantDigits: Int): Double {
    require(significantDigits > 0)
    if (!number.isFinite()) return number
    return BigDecimal.valueOf(number).round(MathContext(significantDigits, RoundingMode.HALF_UP)).toDouble()
}

fun formatDurationNumeric(duration: Duration, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String {
    return String.format(locale, "%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart())
}

fun formatDurationCompact(duration: Duration): UiText {
    val parts = buildList {
        if (duration.toDays() >= 1) add(Res.string.format_duration_days_compact.toUiText(UiTextArgument.Integer(duration.toDays())))
        if (duration.toHoursPart() >= 1) add(Res.string.format_duration_hours_compact.toUiText(UiTextArgument.Integer(duration.toHoursPart().toLong())))
        if (duration.toMinutesPart() >= 1) add(Res.string.format_duration_minutes_compact.toUiText(UiTextArgument.Integer(duration.toMinutesPart().toLong())))
        if (duration.toMinutes() == 0L) add(Res.string.format_duration_seconds_compact.toUiText(UiTextArgument.Integer(duration.seconds)))
    }
    return joinDurationParts(parts, useConjunction = false)
}

fun formatDurationLong(duration: Duration): UiText {
    val parts = buildList {
        if (duration.toDays() >= 1) add(Res.plurals.format_duration_days.toUiText(duration.toDays().coerceAtMost(Int.MAX_VALUE.toLong()).toInt(), UiTextArgument.Integer(duration.toDays())))
        if (duration.toHoursPart() >= 1) add(Res.plurals.format_duration_hours.toUiText(duration.toHoursPart(), UiTextArgument.Integer(duration.toHoursPart().toLong())))
        if (duration.toMinutesPart() >= 1) add(Res.plurals.format_duration_minutes.toUiText(duration.toMinutesPart(), UiTextArgument.Integer(duration.toMinutesPart().toLong())))
    }.take(2)
    return joinDurationParts(parts, useConjunction = true)
}

private fun joinDurationParts(parts: List<UiText>, useConjunction: Boolean): UiText = when (parts.size) {
    0 -> "".toUiText()
    1 -> parts.single()
    else -> {
        val resource = when {
            useConjunction -> Res.string.format_duration_join_and
            parts.size == 2 -> Res.string.format_duration_join_two
            parts.size == 3 -> Res.string.format_duration_join_three
            else -> Res.string.format_duration_join_four
        }
        resource.toUiText(*parts.map { UiTextArgument.Text(it) }.toTypedArray())
    }
}

fun formatDateTime(instant: Instant, timezone: ZoneId = eveTime, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String {
    val time: ZonedDateTime = ZonedDateTime.ofInstant(instant, timezone)
    val isToday = time.toLocalDate() == LocalDate.now(timezone)
    val formatter = if (isToday) dateFormatterWithTime else dateFormatterWithDateTime
    return formatter.withLocale(locale).format(ZonedDateTime.ofInstant(instant, timezone)) + " ${timezone.getName(locale)}"
}

fun formatDateTime2(instant: Instant, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String = dateFormatterWithDateTime2.withLocale(locale).format(ZonedDateTime.ofInstant(instant, eveTime))

fun formatDate(instant: Instant, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String {
    return dateFormatter.withLocale(locale).format(ZonedDateTime.ofInstant(instant, eveTime))
}

fun formatDate(date: LocalDate, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String {
    return dateFormatter.withLocale(locale).format(date)
}

fun formatTime(instant: Instant, timezone: ZoneId, locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String {
    val time = ZonedDateTime.ofInstant(instant, timezone)
    return dateFormatterWithTime.withLocale(locale).format(time)
}

fun formatDuration(duration: Duration): UiText {
    val parts = buildList {
        duration.toDays().takeIf { it > 0 }?.let { add(Res.string.format_duration_days_compact.toUiText(it.toUiTextArgument())) }
        duration.toHoursPart().takeIf { it > 0 }?.let { add(Res.string.format_duration_hours_compact.toUiText(it.toUiTextArgument())) }
        duration.toMinutesPart().takeIf { it > 0 }?.let { add(Res.string.format_duration_minutes_compact.toUiText(it.toUiTextArgument())) }
        add(Res.string.format_duration_seconds_compact.toUiText(UiTextArgument.Integer(duration.toSecondsPart().toLong())))
    }
    return joinDurationParts(parts, useConjunction = false)
}

val Int.plural: String get() {
    return if (this != 1) "s" else ""
}

val Int.invertedPlural: String get() {
    return if (this == 1) "s" else ""
}

val Long.plural: String get() {
    return if (this != 1L) "s" else ""
}

val String.article: String get() {
    return when {
        isEmpty() -> ""
        lowercase().firstOrNull { it.isLetter() } in listOf('e', 'u', 'i', 'o', 'a') -> "an"
        else -> "a"
    }
}

fun ZoneId.getName(locale: Locale = Locale.getDefault(Locale.Category.FORMAT)): String {
    return if (this == ZoneId.of("UTC")) "EVE" else getDisplayName(TextStyle.SHORT, locale)
}
