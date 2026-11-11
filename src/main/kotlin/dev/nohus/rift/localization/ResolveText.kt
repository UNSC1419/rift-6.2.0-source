package dev.nohus.rift.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import java.text.NumberFormat
import java.util.Locale

val LocalAppLocale = staticCompositionLocalOf { Locale.ENGLISH }

@Composable
fun UiText.resolve(): String {
    val locale = LocalAppLocale.current
    return when (this) {
        is UiText.Literal -> this.value
        is UiText.Resource -> stringResource(this.resource, *this.arguments.map { it.resolveArgument(locale) }.toTypedArray())
        is UiText.Plural -> pluralStringResource(this.resource, this.quantity, *this.arguments.map { it.resolveArgument(locale) }.toTypedArray())
    }
}

suspend fun UiText.resolve(appLocale: AppLocale): String {
    return when (this) {
        is UiText.Literal -> this.value
        is UiText.Resource -> appLocale.getString(this.resource, *this.arguments.map { it.resolveArgument(appLocale) }.toTypedArray())
        is UiText.Plural -> appLocale.getPluralString(this.resource, this.quantity, *this.arguments.map { it.resolveArgument(appLocale) }.toTypedArray())
    }
}

@Composable
private fun UiTextArgument.resolveArgument(locale: Locale): String = when (this) {
    is UiTextArgument.Text -> value.resolve()
    else -> formatArgument(locale)
}

private suspend fun UiTextArgument.resolveArgument(appLocale: AppLocale): String = when (this) {
    is UiTextArgument.Text -> value.resolve(appLocale)
    else -> formatArgument(appLocale.locale)
}

private fun UiTextArgument.formatArgument(locale: Locale): String = when (this) {
    is UiTextArgument.Text -> error("Resolve nested messages before formatting")
    is UiTextArgument.Literal -> value
    is UiTextArgument.Integer -> NumberFormat.getIntegerInstance(locale).apply {
        isGroupingUsed = useGrouping
    }.format(value)
    is UiTextArgument.Decimal -> NumberFormat.getNumberInstance(locale).apply {
        minimumFractionDigits = fractionDigits
        maximumFractionDigits = fractionDigits
    }.format(value)
}
