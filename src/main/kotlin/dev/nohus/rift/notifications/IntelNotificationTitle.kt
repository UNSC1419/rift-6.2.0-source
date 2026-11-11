package dev.nohus.rift.notifications

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.alerts_intel_bubbles_title
import dev.nohus.rift.generated.resources.alerts_intel_gate_camp_title
import dev.nohus.rift.generated.resources.alerts_intel_hostile_ship_title
import dev.nohus.rift.generated.resources.alerts_intel_hostile_title
import dev.nohus.rift.generated.resources.alerts_intel_labeled_contacts_title
import dev.nohus.rift.generated.resources.alerts_intel_specific_character_title
import dev.nohus.rift.generated.resources.alerts_intel_specific_ship_class_title
import dev.nohus.rift.generated.resources.alerts_intel_title
import dev.nohus.rift.generated.resources.alerts_intel_wormhole_title
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface IntelNotificationTitle {
    data class Literal(val value: String) : IntelNotificationTitle

    data class Report(
        val type: IntelNotificationReportType,
        val labels: List<String> = emptyList(),
    ) : IntelNotificationTitle
}

@Serializable
enum class IntelNotificationReportType {
    @SerialName("labeled_contacts")
    LabeledContacts,

    @SerialName("specific_character")
    SpecificCharacter,

    @SerialName("specific_ship_class")
    SpecificShipClass,

    @SerialName("hostile")
    Hostile,

    @SerialName("gate_camp")
    GateCamp,

    @SerialName("hostile_ship")
    HostileShip,

    @SerialName("bubbles")
    Bubbles,

    @SerialName("wormhole")
    Wormhole,

    @SerialName("intel")
    Intel,
}

fun IntelNotificationTitle.getText(): UiText = when (this) {
    is IntelNotificationTitle.Literal -> value.toUiText()
    is IntelNotificationTitle.Report -> when (type) {
        IntelNotificationReportType.LabeledContacts -> Res.string.alerts_intel_labeled_contacts_title.toUiText(labels.joinToString(", ").toUiTextArgument())
        IntelNotificationReportType.SpecificCharacter -> Res.string.alerts_intel_specific_character_title.toUiText()
        IntelNotificationReportType.SpecificShipClass -> Res.string.alerts_intel_specific_ship_class_title.toUiText()
        IntelNotificationReportType.Hostile -> Res.string.alerts_intel_hostile_title.toUiText()
        IntelNotificationReportType.GateCamp -> Res.string.alerts_intel_gate_camp_title.toUiText()
        IntelNotificationReportType.HostileShip -> Res.string.alerts_intel_hostile_ship_title.toUiText()
        IntelNotificationReportType.Bubbles -> Res.string.alerts_intel_bubbles_title.toUiText()
        IntelNotificationReportType.Wormhole -> Res.string.alerts_intel_wormhole_title.toUiText()
        IntelNotificationReportType.Intel -> Res.string.alerts_intel_title.toUiText()
    }
}
