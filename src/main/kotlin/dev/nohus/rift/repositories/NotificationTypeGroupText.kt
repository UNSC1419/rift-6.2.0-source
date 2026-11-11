package dev.nohus.rift.repositories

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.notification_history_type_group_agents
import dev.nohus.rift.generated.resources.notification_history_type_group_bills
import dev.nohus.rift.generated.resources.notification_history_type_group_ccp
import dev.nohus.rift.generated.resources.notification_history_type_group_contacts
import dev.nohus.rift.generated.resources.notification_history_type_group_corporate
import dev.nohus.rift.generated.resources.notification_history_type_group_faction_warfare
import dev.nohus.rift.generated.resources.notification_history_type_group_hypernet
import dev.nohus.rift.generated.resources.notification_history_type_group_insurance
import dev.nohus.rift.generated.resources.notification_history_type_group_insurgencies
import dev.nohus.rift.generated.resources.notification_history_type_group_miscellaneous
import dev.nohus.rift.generated.resources.notification_history_type_group_old
import dev.nohus.rift.generated.resources.notification_history_type_group_skill_training
import dev.nohus.rift.generated.resources.notification_history_type_group_sovereignty
import dev.nohus.rift.generated.resources.notification_history_type_group_structures
import dev.nohus.rift.generated.resources.notification_history_type_group_war
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText

fun NotificationTypeGroup.getDisplayText(): UiText {
    return when (name) {
        "Agents" -> Res.string.notification_history_type_group_agents.toUiText()
        "Bills" -> Res.string.notification_history_type_group_bills.toUiText()
        "CCP Notifications" -> Res.string.notification_history_type_group_ccp.toUiText()
        "Contacts" -> Res.string.notification_history_type_group_contacts.toUiText()
        "Corporate" -> Res.string.notification_history_type_group_corporate.toUiText()
        "Faction Warfare" -> Res.string.notification_history_type_group_faction_warfare.toUiText()
        "HyperNet" -> Res.string.notification_history_type_group_hypernet.toUiText()
        "Insurance" -> Res.string.notification_history_type_group_insurance.toUiText()
        "Insurgencies" -> Res.string.notification_history_type_group_insurgencies.toUiText()
        "Miscellaneous" -> Res.string.notification_history_type_group_miscellaneous.toUiText()
        "Old" -> Res.string.notification_history_type_group_old.toUiText()
        "Skill Training" -> Res.string.notification_history_type_group_skill_training.toUiText()
        "Sovereignty" -> Res.string.notification_history_type_group_sovereignty.toUiText()
        "Structures" -> Res.string.notification_history_type_group_structures.toUiText()
        "War" -> Res.string.notification_history_type_group_war.toUiText()
        else -> name.toUiText()
    }
}
