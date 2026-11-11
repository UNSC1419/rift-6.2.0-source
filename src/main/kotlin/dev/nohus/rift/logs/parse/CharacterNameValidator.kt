package dev.nohus.rift.logs.parse

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.character_name_validation_characters
import dev.nohus.rift.generated.resources.character_name_validation_edges
import dev.nohus.rift.generated.resources.character_name_validation_invalid
import dev.nohus.rift.generated.resources.character_name_validation_length
import dev.nohus.rift.generated.resources.character_name_validation_parts
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import org.koin.core.annotation.Single

@Single
class CharacterNameValidator {

    fun isValid(name: String): Boolean {
        return PLAYER_NAME_REGEX.matches(name) && PLAYER_NAME_REGEX2.matches(name) && PLAYER_NAME_REGEX3.matches(name)
    }

    fun getInvalidReason(name: String): UiText {
        return when {
            !PLAYER_NAME_REASON_CHARACTERS_REGEX.matches(name) ->
                Res.string.character_name_validation_characters.toUiText()
            !PLAYER_NAME_REASON_LENGTH_REGEX.matches(name) ->
                Res.string.character_name_validation_length.toUiText()
            !PLAYER_NAME_REGEX2.matches(name) ->
                Res.string.character_name_validation_edges.toUiText()
            !PLAYER_NAME_REGEX3.matches(name) ->
                Res.string.character_name_validation_parts.toUiText()
            else -> Res.string.character_name_validation_invalid.toUiText()
        }
    }

    companion object {
        private val PLAYER_NAME_REGEX = """[A-z0-9 '-]{3,37}""".toRegex()
        private val PLAYER_NAME_REGEX2 = """[^ '-][A-z0-9 '-]+[^ '-]""".toRegex()
        private val PLAYER_NAME_REGEX3 = """^(?<first>[A-z0-9'-]{1,25})(?<middle> [A-z0-9'-]{1,35})?(?<last> [A-z0-9'-]{1,12})?$""".toRegex()

        // PLAYER_NAME_REGEX, separated into two for reason giving
        private val PLAYER_NAME_REASON_CHARACTERS_REGEX = """[A-z0-9 '-]*""".toRegex()
        private val PLAYER_NAME_REASON_LENGTH_REGEX = """.{3,37}""".toRegex()
    }
}
