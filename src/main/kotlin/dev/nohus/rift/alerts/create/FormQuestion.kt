package dev.nohus.rift.alerts.create

import dev.nohus.rift.contacts.ContactsRepository.Label
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.utils.sound.Sound

sealed class FormQuestion(
    open val title: UiText,
) {
    data class SingleChoiceQuestion(
        override val title: UiText,
        val items: List<FormChoiceItem>,
    ) : FormQuestion(title)

    data class MultipleChoiceQuestion(
        override val title: UiText,
        val items: List<FormChoiceItem>,
    ) : FormQuestion(title)

    data class SystemQuestion(
        override val title: UiText,
        val allowEmpty: Boolean,
    ) : FormQuestion(title)

    data class JumpsRangeQuestion(
        override val title: UiText,
    ) : FormQuestion(title)

    data class OwnedCharacterQuestion(
        override val title: UiText,
    ) : FormQuestion(title)

    data class IntelChannelQuestion(
        override val title: UiText,
    ) : FormQuestion(title)

    data class SoundQuestion(
        override val title: UiText,
    ) : FormQuestion(title)

    data class SpecificCharactersQuestion(
        override val title: UiText,
        val allowEmpty: Boolean,
    ) : FormQuestion(title)

    data class CombatTargetQuestion(
        override val title: UiText,
        val placeholder: UiText,
        val allowEmpty: Boolean,
    ) : FormQuestion(title)

    data class PlanetaryIndustryColoniesQuestion(
        override val title: UiText,
    ) : FormQuestion(title)

    data class FreeformTextQuestion(
        override val title: UiText,
        val placeholder: UiText,
        val allowEmpty: Boolean,
        val isRegexAllowed: Boolean = false,
    ) : FormQuestion(title)

    data class ContactsLabelQuestion(
        override val title: UiText,
    ) : FormQuestion(title)
}

data class FormChoiceItem(
    val id: Int,
    val text: UiText,
    val description: UiText? = null,
    // Canonical ship-class value stored in the trigger, independent of its displayed caption
    val canonicalName: String? = null,
)

sealed interface FormAnswer {
    data class SingleChoiceAnswer(
        val id: Int,
    ) : FormAnswer

    data class MultipleChoiceAnswer(
        val items: List<FormChoiceItem>,
    ) : FormAnswer {
        val ids get() = items.map { it.id }
    }

    data class SystemAnswer(
        val system: String,
    ) : FormAnswer

    data class JumpsRangeAnswer(
        val minJumps: Int,
        val maxJumps: Int,
    ) : FormAnswer

    data class CharacterAnswer(
        val characterId: Int,
    ) : FormAnswer

    data class IntelChannelAnswer(
        val channel: String,
    ) : FormAnswer

    sealed interface SoundAnswer : FormAnswer {
        data class BuiltInSound(
            val item: Sound,
        ) : SoundAnswer

        data class CustomSound(
            val path: String,
        ) : SoundAnswer
    }

    data class SpecificCharactersAnswer(
        val characters: List<String>,
    ) : FormAnswer

    data class PlanetaryIndustryColoniesAnswer(
        val colonies: List<String>,
    ) : FormAnswer

    data class FreeformTextAnswer(
        val text: String,
        val isRegex: Boolean = false,
    ) : FormAnswer

    data class ContactsLabelAnswer(
        val labels: List<Label>,
    ) : FormAnswer
}
