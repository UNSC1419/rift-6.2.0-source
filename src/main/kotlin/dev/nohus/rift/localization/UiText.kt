package dev.nohus.rift.localization

import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource

@Immutable
sealed interface UiText {
    data class Literal(val value: String) : UiText

    class Resource(
        val resource: StringResource,
        vararg arguments: UiTextArgument,
    ) : UiText {
        val arguments: List<UiTextArgument> = arguments.toList()
    }

    class Plural(
        val resource: PluralStringResource,
        val quantity: Int,
        vararg arguments: UiTextArgument,
    ) : UiText {
        val arguments: List<UiTextArgument> = arguments.toList()
    }
}

@Immutable
sealed interface UiTextArgument {
    data class Text(val value: UiText) : UiTextArgument
    data class Literal(val value: String) : UiTextArgument
    data class Integer(val value: Long, val useGrouping: Boolean = false) : UiTextArgument
    data class Decimal(val value: Double, val fractionDigits: Int = 2) : UiTextArgument
}

fun String.toUiText() = UiText.Literal(this)
fun StringResource.toUiText(vararg arguments: UiTextArgument) = UiText.Resource(this, *arguments)
fun PluralStringResource.toUiText(quantity: Int, vararg arguments: UiTextArgument) = UiText.Plural(this, quantity, *arguments)

fun UiText.toUiTextArgument() = UiTextArgument.Text(this)
fun String.toUiTextArgument() = UiTextArgument.Literal(this)
fun Int.toUiTextArgument(useGrouping: Boolean = false) = UiTextArgument.Integer(this.toLong(), useGrouping)
fun Long.toUiTextArgument(useGrouping: Boolean = false) = UiTextArgument.Integer(this, useGrouping)
fun Double.toUiTextArgument(fractionDigits: Int = 2) = UiTextArgument.Decimal(this, fractionDigits)
