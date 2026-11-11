package dev.nohus.rift.alerts.create

import dev.nohus.rift.alerts.create.FormQuestion.CombatTargetQuestion
import dev.nohus.rift.alerts.create.FormQuestion.ContactsLabelQuestion
import dev.nohus.rift.alerts.create.FormQuestion.FreeformTextQuestion
import dev.nohus.rift.alerts.create.FormQuestion.JumpsRangeQuestion
import dev.nohus.rift.alerts.create.FormQuestion.MultipleChoiceQuestion
import dev.nohus.rift.alerts.create.FormQuestion.OwnedCharacterQuestion
import dev.nohus.rift.alerts.create.FormQuestion.PlanetaryIndustryColoniesQuestion
import dev.nohus.rift.alerts.create.FormQuestion.SingleChoiceQuestion
import dev.nohus.rift.alerts.create.FormQuestion.SoundQuestion
import dev.nohus.rift.alerts.create.FormQuestion.SpecificCharactersQuestion
import dev.nohus.rift.alerts.create.FormQuestion.SystemQuestion
import dev.nohus.rift.configurationpack.ConfigurationPackRepository
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.alerts_create_action_jabber_message_title
import dev.nohus.rift.generated.resources.alerts_create_action_jabber_ping_title
import dev.nohus.rift.generated.resources.alerts_create_action_planetary_industry_title
import dev.nohus.rift.generated.resources.alerts_create_action_play_sound_name
import dev.nohus.rift.generated.resources.alerts_create_action_push_notification_description
import dev.nohus.rift.generated.resources.alerts_create_action_push_notification_name
import dev.nohus.rift.generated.resources.alerts_create_action_rift_notification_description
import dev.nohus.rift.generated.resources.alerts_create_action_rift_notification_name
import dev.nohus.rift.generated.resources.alerts_create_action_show_colonies_description
import dev.nohus.rift.generated.resources.alerts_create_action_show_colonies_name
import dev.nohus.rift.generated.resources.alerts_create_action_show_jabber_message_description
import dev.nohus.rift.generated.resources.alerts_create_action_show_jabber_message_name
import dev.nohus.rift.generated.resources.alerts_create_action_show_ping_description
import dev.nohus.rift.generated.resources.alerts_create_action_show_ping_name
import dev.nohus.rift.generated.resources.alerts_create_action_sound_title
import dev.nohus.rift.generated.resources.alerts_create_action_system_notification_description
import dev.nohus.rift.generated.resources.alerts_create_action_system_notification_name
import dev.nohus.rift.generated.resources.alerts_create_action_title
import dev.nohus.rift.generated.resources.alerts_create_chat_message_channel_any_name
import dev.nohus.rift.generated.resources.alerts_create_chat_message_channel_specific_name
import dev.nohus.rift.generated.resources.alerts_create_chat_message_channel_type_title
import dev.nohus.rift.generated.resources.alerts_create_chat_message_message_containing_placeholder
import dev.nohus.rift.generated.resources.alerts_create_chat_message_message_containing_title
import dev.nohus.rift.generated.resources.alerts_create_chat_message_sender_exclude_self_no_name
import dev.nohus.rift.generated.resources.alerts_create_chat_message_sender_exclude_self_title
import dev.nohus.rift.generated.resources.alerts_create_chat_message_sender_exclude_self_yes_name
import dev.nohus.rift.generated.resources.alerts_create_chat_message_sender_placeholder
import dev.nohus.rift.generated.resources.alerts_create_chat_message_sender_title
import dev.nohus.rift.generated.resources.alerts_create_chat_message_specific_channel_placeholder
import dev.nohus.rift.generated.resources.alerts_create_chat_message_specific_channel_title
import dev.nohus.rift.generated.resources.alerts_create_cooldown_10_minutes_name
import dev.nohus.rift.generated.resources.alerts_create_cooldown_1_minute_name
import dev.nohus.rift.generated.resources.alerts_create_cooldown_2_minutes_name
import dev.nohus.rift.generated.resources.alerts_create_cooldown_30_seconds_name
import dev.nohus.rift.generated.resources.alerts_create_cooldown_5_minutes_name
import dev.nohus.rift.generated.resources.alerts_create_cooldown_none_name
import dev.nohus.rift.generated.resources.alerts_create_cooldown_title
import dev.nohus.rift.generated.resources.alerts_create_eve_notification_character_any_name
import dev.nohus.rift.generated.resources.alerts_create_eve_notification_character_offline_name
import dev.nohus.rift.generated.resources.alerts_create_eve_notification_character_online_name
import dev.nohus.rift.generated.resources.alerts_create_eve_notification_character_specific_name
import dev.nohus.rift.generated.resources.alerts_create_eve_notification_character_status_any_name
import dev.nohus.rift.generated.resources.alerts_create_eve_notification_character_status_title
import dev.nohus.rift.generated.resources.alerts_create_eve_notification_character_type_title
import dev.nohus.rift.generated.resources.alerts_create_eve_notification_specific_character_title
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_asteroid_depleted_description
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_asteroid_depleted_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_attacking_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_being_warp_scrambled_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_combat_stopped_description
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_combat_stopped_duration_10_seconds_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_combat_stopped_duration_1_minute_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_combat_stopped_duration_20_seconds_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_combat_stopped_duration_2_minutes_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_combat_stopped_duration_30_seconds_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_combat_stopped_duration_5_minutes_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_combat_stopped_duration_title
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_combat_stopped_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_combat_target_title
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_custom_description
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_custom_message_placeholder
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_custom_message_title
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_custom_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_decloaked_exceptions_placeholder
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_decloaked_exceptions_title
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_decloaked_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_in_combat_description
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_in_combat_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_invited_to_conversation_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_run_out_of_charges_name
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_title
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_under_attack_description
import dev.nohus.rift.generated.resources.alerts_create_game_action_type_under_attack_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_location_any_owned_character_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_location_any_undocked_character_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_location_jumps_range_title
import dev.nohus.rift.generated.resources.alerts_create_intel_report_location_owned_character_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_location_owned_character_title
import dev.nohus.rift.generated.resources.alerts_create_intel_report_location_system_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_location_system_title
import dev.nohus.rift.generated.resources.alerts_create_intel_report_location_title
import dev.nohus.rift.generated.resources.alerts_create_intel_report_location_undocked_character_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_type_any_character_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_type_any_ship_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_type_bubbles_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_type_ess_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_type_gate_camp_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_type_labeled_contacts_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_type_labeled_contacts_title
import dev.nohus.rift.generated.resources.alerts_create_intel_report_type_skyhook_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_type_specific_characters_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_type_specific_characters_title
import dev.nohus.rift.generated.resources.alerts_create_intel_report_type_specific_ship_classes_name
import dev.nohus.rift.generated.resources.alerts_create_intel_report_type_specific_ship_classes_title
import dev.nohus.rift.generated.resources.alerts_create_intel_report_type_title
import dev.nohus.rift.generated.resources.alerts_create_intel_report_type_wormhole_name
import dev.nohus.rift.generated.resources.alerts_create_jabber_message_channel_any_name
import dev.nohus.rift.generated.resources.alerts_create_jabber_message_channel_direct_message_name
import dev.nohus.rift.generated.resources.alerts_create_jabber_message_channel_specific_name
import dev.nohus.rift.generated.resources.alerts_create_jabber_message_channel_type_title
import dev.nohus.rift.generated.resources.alerts_create_jabber_message_message_containing_placeholder
import dev.nohus.rift.generated.resources.alerts_create_jabber_message_message_containing_title
import dev.nohus.rift.generated.resources.alerts_create_jabber_message_sender_placeholder
import dev.nohus.rift.generated.resources.alerts_create_jabber_message_sender_title
import dev.nohus.rift.generated.resources.alerts_create_jabber_message_specific_channel_placeholder
import dev.nohus.rift.generated.resources.alerts_create_jabber_message_specific_channel_title
import dev.nohus.rift.generated.resources.alerts_create_jabber_ping_fleet_commander_title
import dev.nohus.rift.generated.resources.alerts_create_jabber_ping_fleet_doctrine_placeholder
import dev.nohus.rift.generated.resources.alerts_create_jabber_ping_fleet_doctrine_title
import dev.nohus.rift.generated.resources.alerts_create_jabber_ping_fleet_formup_system_title
import dev.nohus.rift.generated.resources.alerts_create_jabber_ping_fleet_pap_type_any_name
import dev.nohus.rift.generated.resources.alerts_create_jabber_ping_fleet_pap_type_peacetime_name
import dev.nohus.rift.generated.resources.alerts_create_jabber_ping_fleet_pap_type_strategic_name
import dev.nohus.rift.generated.resources.alerts_create_jabber_ping_fleet_pap_type_title
import dev.nohus.rift.generated.resources.alerts_create_jabber_ping_target_placeholder
import dev.nohus.rift.generated.resources.alerts_create_jabber_ping_target_title
import dev.nohus.rift.generated.resources.alerts_create_jabber_ping_type_fleet_name
import dev.nohus.rift.generated.resources.alerts_create_jabber_ping_type_message_name
import dev.nohus.rift.generated.resources.alerts_create_jabber_ping_type_title
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_alert_before_12_hours_name
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_alert_before_15_minutes_name
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_alert_before_1_hour_name
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_alert_before_24_hours_name
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_alert_before_2_hours_name
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_alert_before_30_minutes_name
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_alert_before_4_hours_name
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_alert_before_5_minutes_name
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_alert_before_8_hours_name
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_alert_before_none_name
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_alert_before_title
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_colonies_title
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_event_type_extractor_inactive_description
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_event_type_extractor_inactive_name
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_event_type_idle_description
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_event_type_idle_name
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_event_type_not_setup_description
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_event_type_not_setup_name
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_event_type_storage_full_description
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_event_type_storage_full_name
import dev.nohus.rift.generated.resources.alerts_create_planetary_industry_event_type_title
import dev.nohus.rift.generated.resources.alerts_create_trigger_chat_message_name
import dev.nohus.rift.generated.resources.alerts_create_trigger_eve_notification_name
import dev.nohus.rift.generated.resources.alerts_create_trigger_game_action_name
import dev.nohus.rift.generated.resources.alerts_create_trigger_intel_reported_name
import dev.nohus.rift.generated.resources.alerts_create_trigger_jabber_message_name
import dev.nohus.rift.generated.resources.alerts_create_trigger_jabber_ping_name
import dev.nohus.rift.generated.resources.alerts_create_trigger_planetary_industry_name
import dev.nohus.rift.generated.resources.alerts_create_trigger_title
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.repositories.ShipTypesRepository

@Suppress("PropertyName")
class CreateAlertQuestions(
    shipTypesRepository: ShipTypesRepository,
    configurationPackRepository: ConfigurationPackRepository,
) {
    private var id = 0

    // Alert trigger
    val ALERT_TRIGGER_INTEL_REPORTED = FormChoiceItem(id = id++, text = Res.string.alerts_create_trigger_intel_reported_name.toUiText())
    val ALERT_TRIGGER_GAME_ACTION = FormChoiceItem(id = id++, text = Res.string.alerts_create_trigger_game_action_name.toUiText())
    val ALERT_TRIGGER_PLANETARY_INDUSTRY = FormChoiceItem(id = id++, text = Res.string.alerts_create_trigger_planetary_industry_name.toUiText())
    val ALERT_TRIGGER_EVE_NOTIFICATION = FormChoiceItem(id = id++, text = Res.string.alerts_create_trigger_eve_notification_name.toUiText())
    val ALERT_TRIGGER_CHAT_MESSAGE = FormChoiceItem(id = id++, text = Res.string.alerts_create_trigger_chat_message_name.toUiText())
    val ALERT_TRIGGER_JABBER_PING = FormChoiceItem(id = id++, text = Res.string.alerts_create_trigger_jabber_ping_name.toUiText())
    val ALERT_TRIGGER_JABBER_MESSAGE = FormChoiceItem(id = id++, text = Res.string.alerts_create_trigger_jabber_message_name.toUiText())
    val ALERT_TRIGGER_QUESTION = SingleChoiceQuestion(
        title = Res.string.alerts_create_trigger_title.toUiText(),
        items = buildList {
            add(ALERT_TRIGGER_INTEL_REPORTED)
            add(ALERT_TRIGGER_GAME_ACTION)
            add(ALERT_TRIGGER_PLANETARY_INDUSTRY)
            add(ALERT_TRIGGER_EVE_NOTIFICATION)
            add(ALERT_TRIGGER_CHAT_MESSAGE)
            if (configurationPackRepository.isJabberEnabled()) {
                add(ALERT_TRIGGER_JABBER_PING)
                add(ALERT_TRIGGER_JABBER_MESSAGE)
            }
        },
    )

    // EVE notification, target character
    val EVE_NOTIFICATION_CHARACTER_ANY = FormChoiceItem(id = id++, text = Res.string.alerts_create_eve_notification_character_any_name.toUiText())
    val EVE_NOTIFICATION_CHARACTER_SPECIFIC = FormChoiceItem(id = id++, text = Res.string.alerts_create_eve_notification_character_specific_name.toUiText())
    val EVE_NOTIFICATION_CHARACTER_TYPE_QUESTION = SingleChoiceQuestion(
        title = Res.string.alerts_create_eve_notification_character_type_title.toUiText(),
        items = listOf(
            EVE_NOTIFICATION_CHARACTER_ANY,
            EVE_NOTIFICATION_CHARACTER_SPECIFIC,
        ),
    )
    val EVE_NOTIFICATION_SPECIFIC_CHARACTER_QUESTION = OwnedCharacterQuestion(
        title = Res.string.alerts_create_eve_notification_specific_character_title.toUiText(),
    )

    // EVE notification, target character status
    val EVE_NOTIFICATION_CHARACTER_ONLINE = FormChoiceItem(id = id++, text = Res.string.alerts_create_eve_notification_character_online_name.toUiText())
    val EVE_NOTIFICATION_CHARACTER_OFFLINE = FormChoiceItem(id = id++, text = Res.string.alerts_create_eve_notification_character_offline_name.toUiText())
    val EVE_NOTIFICATION_CHARACTER_STATUS_ANY = FormChoiceItem(id = id++, text = Res.string.alerts_create_eve_notification_character_status_any_name.toUiText())
    val EVE_NOTIFICATION_CHARACTER_STATUS_QUESTION = SingleChoiceQuestion(
        title = Res.string.alerts_create_eve_notification_character_status_title.toUiText(),
        items = listOf(
            EVE_NOTIFICATION_CHARACTER_ONLINE,
            EVE_NOTIFICATION_CHARACTER_OFFLINE,
            EVE_NOTIFICATION_CHARACTER_STATUS_ANY,
        ),
    )

    // Intel report type
    val INTEL_REPORT_TYPE_ANY_CHARACTER = FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_type_any_character_name.toUiText())
    val INTEL_REPORT_TYPE_SPECIFIC_CHARACTERS = FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_type_specific_characters_name.toUiText())
    val INTEL_REPORT_TYPE_LABELED_CONTACTS = FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_type_labeled_contacts_name.toUiText())
    val INTEL_REPORT_TYPE_ANY_SHIP = FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_type_any_ship_name.toUiText())
    val INTEL_REPORT_TYPE_SPECIFIC_SHIP_CLASSES = FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_type_specific_ship_classes_name.toUiText())
    val INTEL_REPORT_TYPE_WORMHOLE = FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_type_wormhole_name.toUiText())
    val INTEL_REPORT_TYPE_GATE_CAMP = FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_type_gate_camp_name.toUiText())
    val INTEL_REPORT_TYPE_BUBBLES = FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_type_bubbles_name.toUiText())
    val INTEL_REPORT_TYPE_ESS = FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_type_ess_name.toUiText())
    val INTEL_REPORT_TYPE_SKYHOOK = FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_type_skyhook_name.toUiText())
    val INTEL_REPORT_TYPE_QUESTION = MultipleChoiceQuestion(
        title = Res.string.alerts_create_intel_report_type_title.toUiText(),
        items = listOf(
            INTEL_REPORT_TYPE_ANY_CHARACTER,
            INTEL_REPORT_TYPE_SPECIFIC_CHARACTERS,
            INTEL_REPORT_TYPE_LABELED_CONTACTS,
            INTEL_REPORT_TYPE_ANY_SHIP,
            INTEL_REPORT_TYPE_SPECIFIC_SHIP_CLASSES,
            INTEL_REPORT_TYPE_WORMHOLE,
            INTEL_REPORT_TYPE_GATE_CAMP,
            INTEL_REPORT_TYPE_BUBBLES,
            INTEL_REPORT_TYPE_ESS,
            INTEL_REPORT_TYPE_SKYHOOK,
        ),
    )

    // Intel report type, specific characters
    val INTEL_REPORT_TYPE_SPECIFIC_CHARACTERS_QUESTION = SpecificCharactersQuestion(
        title = Res.string.alerts_create_intel_report_type_specific_characters_title.toUiText(),
        allowEmpty = false,
    )

    // Intel report type, labeled contacts
    val INTEL_REPORT_TYPE_LABELED_CONTACTS_QUESTION = ContactsLabelQuestion(
        title = Res.string.alerts_create_intel_report_type_labeled_contacts_title.toUiText(),
    )

    // Intel report type, specific ship classes
    val INTEL_REPORT_TYPE_SPECIFIC_SHIP_CLASSES_QUESTION = MultipleChoiceQuestion(
        title = Res.string.alerts_create_intel_report_type_specific_ship_classes_title.toUiText(),
        items = shipTypesRepository.getShipClasses()
            .sorted()
            .mapIndexed { index, shipClass -> FormChoiceItem(index, shipClass.toUiText(), canonicalName = shipClass) },
    )

    // Intel report location
    val INTEL_REPORT_LOCATION_SYSTEM = FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_location_system_name.toUiText())
    val INTEL_REPORT_LOCATION_ANY_OWNED_CHARACTER =
        FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_location_any_owned_character_name.toUiText())
    val INTEL_REPORT_LOCATION_OWNED_CHARACTER =
        FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_location_owned_character_name.toUiText())
    val INTEL_REPORT_LOCATION_ANY_UNDOCKED_CHARACTER =
        FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_location_any_undocked_character_name.toUiText())
    val INTEL_REPORT_LOCATION_UNDOCKED_CHARACTER =
        FormChoiceItem(id = id++, text = Res.string.alerts_create_intel_report_location_undocked_character_name.toUiText())
    val INTEL_REPORT_LOCATION_QUESTION = SingleChoiceQuestion(
        title = Res.string.alerts_create_intel_report_location_title.toUiText(),
        items = listOf(
            INTEL_REPORT_LOCATION_SYSTEM,
            INTEL_REPORT_LOCATION_ANY_OWNED_CHARACTER,
            INTEL_REPORT_LOCATION_OWNED_CHARACTER,
            INTEL_REPORT_LOCATION_ANY_UNDOCKED_CHARACTER,
            INTEL_REPORT_LOCATION_UNDOCKED_CHARACTER,
        ),
    )

    // Intel report location, system
    val INTEL_REPORT_LOCATION_SYSTEM_QUESTION = SystemQuestion(
        title = Res.string.alerts_create_intel_report_location_system_title.toUiText(),
        allowEmpty = false,
    )

    // Intel report location, specific character
    val INTEL_REPORT_LOCATION_OWNED_CHARACTER_QUESTION = OwnedCharacterQuestion(
        title = Res.string.alerts_create_intel_report_location_owned_character_title.toUiText(),
    )

    // Intel report location, jumps range
    val INTEL_REPORT_LOCATION_JUMPS_RANGE_QUESTION = JumpsRangeQuestion(
        title = Res.string.alerts_create_intel_report_location_jumps_range_title.toUiText(),
    )

    // Game action type
    val GAME_ACTION_TYPE_IN_COMBAT = FormChoiceItem(
        id = id++,
        text = Res.string.alerts_create_game_action_type_in_combat_name.toUiText(),
        description = Res.string.alerts_create_game_action_type_in_combat_description.toUiText(),
    )
    val GAME_ACTION_TYPE_UNDER_ATTACK = FormChoiceItem(
        id = id++,
        text = Res.string.alerts_create_game_action_type_under_attack_name.toUiText(),
        description = Res.string.alerts_create_game_action_type_under_attack_description.toUiText(),
    )
    val GAME_ACTION_TYPE_ATTACKING = FormChoiceItem(
        id = id++,
        text = Res.string.alerts_create_game_action_type_attacking_name.toUiText(),
    )
    val GAME_ACTION_TYPE_COMBAT_STOPPED = FormChoiceItem(
        id = id++,
        text = Res.string.alerts_create_game_action_type_combat_stopped_name.toUiText(),
        description = Res.string.alerts_create_game_action_type_combat_stopped_description.toUiText(),
    )
    val GAME_ACTION_TYPE_BEING_WARP_SCRAMBLED = FormChoiceItem(
        id = id++,
        text = Res.string.alerts_create_game_action_type_being_warp_scrambled_name.toUiText(),
    )
    val GAME_ACTION_TYPE_DECLOAKED = FormChoiceItem(
        id = id++,
        text = Res.string.alerts_create_game_action_type_decloaked_name.toUiText(),
    )
    val GAME_ACTION_TYPE_RUN_OUT_OF_CHARGES = FormChoiceItem(
        id = id++,
        text = Res.string.alerts_create_game_action_type_run_out_of_charges_name.toUiText(),
    )
    val GAME_ACTION_TYPE_INVITED_TO_CONVERSATION = FormChoiceItem(
        id = id++,
        text = Res.string.alerts_create_game_action_type_invited_to_conversation_name.toUiText(),
    )
    val GAME_ACTION_TYPE_ASTEROID_DEPLETED = FormChoiceItem(
        id = id++,
        text = Res.string.alerts_create_game_action_type_asteroid_depleted_name.toUiText(),
        description = Res.string.alerts_create_game_action_type_asteroid_depleted_description.toUiText(),
    )
    val GAME_ACTION_TYPE_CUSTOM = FormChoiceItem(
        id = id++,
        text = Res.string.alerts_create_game_action_type_custom_name.toUiText(),
        description = Res.string.alerts_create_game_action_type_custom_description.toUiText(),
    )
    val GAME_ACTION_TYPE_QUESTION = MultipleChoiceQuestion(
        title = Res.string.alerts_create_game_action_type_title.toUiText(),
        items = listOf(
            GAME_ACTION_TYPE_IN_COMBAT,
            GAME_ACTION_TYPE_UNDER_ATTACK,
            GAME_ACTION_TYPE_ATTACKING,
            GAME_ACTION_TYPE_BEING_WARP_SCRAMBLED,
            GAME_ACTION_TYPE_DECLOAKED,
            GAME_ACTION_TYPE_COMBAT_STOPPED,
            GAME_ACTION_TYPE_RUN_OUT_OF_CHARGES,
            GAME_ACTION_TYPE_INVITED_TO_CONVERSATION,
            GAME_ACTION_TYPE_ASTEROID_DEPLETED,
            GAME_ACTION_TYPE_CUSTOM,
        ),
    )

    // Game action type, combat target
    val GAME_ACTION_TYPE_COMBAT_TARGET_QUESTION = CombatTargetQuestion(
        title = Res.string.alerts_create_game_action_type_combat_target_title.toUiText(),
        placeholder = "Dark Blood".toUiText(),
        allowEmpty = true,
    )

    // Game action type, decloak exceptions
    val GAME_ACTION_TYPE_DECLOAKED_EXCEPTIONS_QUESTION = FreeformTextQuestion(
        title = Res.string.alerts_create_game_action_type_decloaked_exceptions_title.toUiText(),
        placeholder = Res.string.alerts_create_game_action_type_decloaked_exceptions_placeholder.toUiText(),
        allowEmpty = true,
    )

    // Game action type, combat stopped, duration
    val GAME_ACTION_TYPE_COMBAT_STOPPED_DURATION_10_SECONDS = FormChoiceItem(id = id++, text = Res.string.alerts_create_game_action_type_combat_stopped_duration_10_seconds_name.toUiText())
    val GAME_ACTION_TYPE_COMBAT_STOPPED_DURATION_20_SECONDS = FormChoiceItem(id = id++, text = Res.string.alerts_create_game_action_type_combat_stopped_duration_20_seconds_name.toUiText())
    val GAME_ACTION_TYPE_COMBAT_STOPPED_DURATION_30_SECONDS = FormChoiceItem(id = id++, text = Res.string.alerts_create_game_action_type_combat_stopped_duration_30_seconds_name.toUiText())
    val GAME_ACTION_TYPE_COMBAT_STOPPED_DURATION_1_MINUTE = FormChoiceItem(id = id++, text = Res.string.alerts_create_game_action_type_combat_stopped_duration_1_minute_name.toUiText())
    val GAME_ACTION_TYPE_COMBAT_STOPPED_DURATION_2_MINUTES = FormChoiceItem(id = id++, text = Res.string.alerts_create_game_action_type_combat_stopped_duration_2_minutes_name.toUiText())
    val GAME_ACTION_TYPE_COMBAT_STOPPED_DURATION_5_MINUTES = FormChoiceItem(id = id++, text = Res.string.alerts_create_game_action_type_combat_stopped_duration_5_minutes_name.toUiText())
    val GAME_ACTION_TYPE_COMBAT_STOPPED_DURATION_QUESTION = SingleChoiceQuestion(
        title = Res.string.alerts_create_game_action_type_combat_stopped_duration_title.toUiText(),
        items = listOf(
            GAME_ACTION_TYPE_COMBAT_STOPPED_DURATION_10_SECONDS,
            GAME_ACTION_TYPE_COMBAT_STOPPED_DURATION_20_SECONDS,
            GAME_ACTION_TYPE_COMBAT_STOPPED_DURATION_30_SECONDS,
            GAME_ACTION_TYPE_COMBAT_STOPPED_DURATION_1_MINUTE,
            GAME_ACTION_TYPE_COMBAT_STOPPED_DURATION_2_MINUTES,
            GAME_ACTION_TYPE_COMBAT_STOPPED_DURATION_5_MINUTES,
        ),
    )

    // Game action type, custom game log message
    val GAME_ACTION_TYPE_CUSTOM_MESSAGE_QUESTION = FreeformTextQuestion(
        title = Res.string.alerts_create_game_action_type_custom_message_title.toUiText(),
        placeholder = Res.string.alerts_create_game_action_type_custom_message_placeholder.toUiText(),
        allowEmpty = false,
        isRegexAllowed = true,
    )

    // Planetary Industry event type
    val PLANETARY_INDUSTRY_EVENT_TYPE_NOT_SETUP = FormChoiceItem(
        id = id++,
        text = Res.string.alerts_create_planetary_industry_event_type_not_setup_name.toUiText(),
        description = Res.string.alerts_create_planetary_industry_event_type_not_setup_description.toUiText(),
    )
    val PLANETARY_INDUSTRY_EVENT_TYPE_EXTRACTOR_INACTIVE = FormChoiceItem(
        id = id++,
        text = Res.string.alerts_create_planetary_industry_event_type_extractor_inactive_name.toUiText(),
        description = Res.string.alerts_create_planetary_industry_event_type_extractor_inactive_description.toUiText(),
    )
    val PLANETARY_INDUSTRY_EVENT_TYPE_STORAGE_FULL = FormChoiceItem(
        id = id++,
        text = Res.string.alerts_create_planetary_industry_event_type_storage_full_name.toUiText(),
        description = Res.string.alerts_create_planetary_industry_event_type_storage_full_description.toUiText(),
    )
    val PLANETARY_INDUSTRY_EVENT_TYPE_IDLE = FormChoiceItem(
        id = id++,
        text = Res.string.alerts_create_planetary_industry_event_type_idle_name.toUiText(),
        description = Res.string.alerts_create_planetary_industry_event_type_idle_description.toUiText(),
    )
    val PLANETARY_INDUSTRY_EVENT_TYPE_QUESTION = MultipleChoiceQuestion(
        title = Res.string.alerts_create_planetary_industry_event_type_title.toUiText(),
        items = listOf(
            PLANETARY_INDUSTRY_EVENT_TYPE_EXTRACTOR_INACTIVE,
            PLANETARY_INDUSTRY_EVENT_TYPE_STORAGE_FULL,
            PLANETARY_INDUSTRY_EVENT_TYPE_IDLE,
            PLANETARY_INDUSTRY_EVENT_TYPE_NOT_SETUP,
        ),
    )

    // Planetary Industry colony filter
    val PLANETARY_INDUSTRY_COLONIES_QUESTION = PlanetaryIndustryColoniesQuestion(
        title = Res.string.alerts_create_planetary_industry_colonies_title.toUiText(),
    )

    // Planetary Industry alert before
    val PLANETARY_INDUSTRY_ALERT_BEFORE_NONE = FormChoiceItem(id = id++, text = Res.string.alerts_create_planetary_industry_alert_before_none_name.toUiText())
    val PLANETARY_INDUSTRY_ALERT_BEFORE_5_MINUTES = FormChoiceItem(id = id++, text = Res.string.alerts_create_planetary_industry_alert_before_5_minutes_name.toUiText())
    val PLANETARY_INDUSTRY_ALERT_BEFORE_15_MINUTES = FormChoiceItem(id = id++, text = Res.string.alerts_create_planetary_industry_alert_before_15_minutes_name.toUiText())
    val PLANETARY_INDUSTRY_ALERT_BEFORE_30_MINUTES = FormChoiceItem(id = id++, text = Res.string.alerts_create_planetary_industry_alert_before_30_minutes_name.toUiText())
    val PLANETARY_INDUSTRY_ALERT_BEFORE_1_HOUR = FormChoiceItem(id = id++, text = Res.string.alerts_create_planetary_industry_alert_before_1_hour_name.toUiText())
    val PLANETARY_INDUSTRY_ALERT_BEFORE_2_HOURS = FormChoiceItem(id = id++, text = Res.string.alerts_create_planetary_industry_alert_before_2_hours_name.toUiText())
    val PLANETARY_INDUSTRY_ALERT_BEFORE_4_HOURS = FormChoiceItem(id = id++, text = Res.string.alerts_create_planetary_industry_alert_before_4_hours_name.toUiText())
    val PLANETARY_INDUSTRY_ALERT_BEFORE_8_HOURS = FormChoiceItem(id = id++, text = Res.string.alerts_create_planetary_industry_alert_before_8_hours_name.toUiText())
    val PLANETARY_INDUSTRY_ALERT_BEFORE_12_HOURS = FormChoiceItem(id = id++, text = Res.string.alerts_create_planetary_industry_alert_before_12_hours_name.toUiText())
    val PLANETARY_INDUSTRY_ALERT_BEFORE_24_HOURS = FormChoiceItem(id = id++, text = Res.string.alerts_create_planetary_industry_alert_before_24_hours_name.toUiText())
    val PLANETARY_INDUSTRY_ALERT_BEFORE_QUESTION = SingleChoiceQuestion(
        title = Res.string.alerts_create_planetary_industry_alert_before_title.toUiText(),
        items = listOf(
            PLANETARY_INDUSTRY_ALERT_BEFORE_NONE,
            PLANETARY_INDUSTRY_ALERT_BEFORE_5_MINUTES,
            PLANETARY_INDUSTRY_ALERT_BEFORE_15_MINUTES,
            PLANETARY_INDUSTRY_ALERT_BEFORE_30_MINUTES,
            PLANETARY_INDUSTRY_ALERT_BEFORE_1_HOUR,
            PLANETARY_INDUSTRY_ALERT_BEFORE_2_HOURS,
            PLANETARY_INDUSTRY_ALERT_BEFORE_4_HOURS,
            PLANETARY_INDUSTRY_ALERT_BEFORE_8_HOURS,
            PLANETARY_INDUSTRY_ALERT_BEFORE_12_HOURS,
            PLANETARY_INDUSTRY_ALERT_BEFORE_24_HOURS,
        ),
    )

    // Chat message, channel type
    val CHAT_MESSAGE_CHANNEL_ANY = FormChoiceItem(id = id++, text = Res.string.alerts_create_chat_message_channel_any_name.toUiText())
    val CHAT_MESSAGE_CHANNEL_SPECIFIC = FormChoiceItem(id = id++, text = Res.string.alerts_create_chat_message_channel_specific_name.toUiText())
    val CHAT_MESSAGE_CHANNEL_TYPE_QUESTION = SingleChoiceQuestion(
        title = Res.string.alerts_create_chat_message_channel_type_title.toUiText(),
        items = listOf(
            CHAT_MESSAGE_CHANNEL_ANY,
            CHAT_MESSAGE_CHANNEL_SPECIFIC,
        ),
    )

    // Chat message, specific channel
    val CHAT_MESSAGE_SPECIFIC_CHANNEL_QUESTION = FreeformTextQuestion(
        title = Res.string.alerts_create_chat_message_specific_channel_title.toUiText(),
        placeholder = Res.string.alerts_create_chat_message_specific_channel_placeholder.toUiText(),
        allowEmpty = false,
    )

    // Chat message, sender
    val CHAT_MESSAGE_SENDER_QUESTION = FreeformTextQuestion(
        title = Res.string.alerts_create_chat_message_sender_title.toUiText(),
        placeholder = Res.string.alerts_create_chat_message_sender_placeholder.toUiText(),
        allowEmpty = true,
    )

    // Chat message, exclude self
    val CHAT_MESSAGE_SENDER_EXCLUDE_SELF_NO = FormChoiceItem(id = id++, text = Res.string.alerts_create_chat_message_sender_exclude_self_no_name.toUiText())
    val CHAT_MESSAGE_SENDER_EXCLUDE_SELF_YES = FormChoiceItem(id = id++, text = Res.string.alerts_create_chat_message_sender_exclude_self_yes_name.toUiText())
    val CHAT_MESSAGE_SENDER_EXCLUDE_SELF_QUESTION = SingleChoiceQuestion(
        title = Res.string.alerts_create_chat_message_sender_exclude_self_title.toUiText(),
        items = listOf(
            CHAT_MESSAGE_SENDER_EXCLUDE_SELF_NO,
            CHAT_MESSAGE_SENDER_EXCLUDE_SELF_YES,
        ),
    )

    // Chat message, message contains
    val CHAT_MESSAGE_MESSAGE_CONTAINING_QUESTION = FreeformTextQuestion(
        title = Res.string.alerts_create_chat_message_message_containing_title.toUiText(),
        placeholder = Res.string.alerts_create_chat_message_message_containing_placeholder.toUiText(),
        allowEmpty = true,
        isRegexAllowed = true,
    )

    // Jabber ping, ping type
    val JABBER_PING_TYPE_FLEET = FormChoiceItem(id = id++, text = Res.string.alerts_create_jabber_ping_type_fleet_name.toUiText())
    val JABBER_PING_TYPE_MESSAGE = FormChoiceItem(id = id++, text = Res.string.alerts_create_jabber_ping_type_message_name.toUiText())
    val JABBER_PING_TYPE_QUESTION = SingleChoiceQuestion(
        title = Res.string.alerts_create_jabber_ping_type_title.toUiText(),
        items = listOf(
            JABBER_PING_TYPE_FLEET,
            JABBER_PING_TYPE_MESSAGE,
        ),
    )

    // Jabber ping, fleet ping, fleet commander
    val JABBER_PING_FLEET_COMMANDER_QUESTION = SpecificCharactersQuestion(
        title = Res.string.alerts_create_jabber_ping_fleet_commander_title.toUiText(),
        allowEmpty = true,
    )

    // Jabber ping, fleet ping, formup system
    val JABBER_PING_FLEET_FORMUP_SYSTEM_QUESTION = SystemQuestion(
        title = Res.string.alerts_create_jabber_ping_fleet_formup_system_title.toUiText(),
        allowEmpty = true,
    )

    // Jabber ping, fleet ping, PAP type
    val JABBER_PING_FLEET_PAP_TYPE_STRATEGIC = FormChoiceItem(id = id++, text = Res.string.alerts_create_jabber_ping_fleet_pap_type_strategic_name.toUiText())
    val JABBER_PING_FLEET_PAP_TYPE_PEACETIME = FormChoiceItem(id = id++, text = Res.string.alerts_create_jabber_ping_fleet_pap_type_peacetime_name.toUiText())
    val JABBER_PING_FLEET_PAP_TYPE_ANY = FormChoiceItem(id = id++, text = Res.string.alerts_create_jabber_ping_fleet_pap_type_any_name.toUiText())
    val JABBER_PING_FLEET_PAP_TYPE_QUESTION = SingleChoiceQuestion(
        title = Res.string.alerts_create_jabber_ping_fleet_pap_type_title.toUiText(),
        items = listOf(
            JABBER_PING_FLEET_PAP_TYPE_STRATEGIC,
            JABBER_PING_FLEET_PAP_TYPE_PEACETIME,
            JABBER_PING_FLEET_PAP_TYPE_ANY,
        ),
    )

    // Jabber ping, fleet ping, doctrine
    val JABBER_PING_FLEET_DOCTRINE_QUESTION = FreeformTextQuestion(
        title = Res.string.alerts_create_jabber_ping_fleet_doctrine_title.toUiText(),
        placeholder = Res.string.alerts_create_jabber_ping_fleet_doctrine_placeholder.toUiText(),
        allowEmpty = true,
    )

    // Jabber ping, target
    val JABBER_PING_TARGET_QUESTION = FreeformTextQuestion(
        title = Res.string.alerts_create_jabber_ping_target_title.toUiText(),
        placeholder = Res.string.alerts_create_jabber_ping_target_placeholder.toUiText(),
        allowEmpty = true,
    )

    // Jabber message, channel type
    val JABBER_MESSAGE_CHANNEL_ANY = FormChoiceItem(id = id++, text = Res.string.alerts_create_jabber_message_channel_any_name.toUiText())
    val JABBER_MESSAGE_CHANNEL_SPECIFIC = FormChoiceItem(id = id++, text = Res.string.alerts_create_jabber_message_channel_specific_name.toUiText())
    val JABBER_MESSAGE_CHANNEL_DIRECT_MESSAGE = FormChoiceItem(id = id++, text = Res.string.alerts_create_jabber_message_channel_direct_message_name.toUiText())
    val JABBER_MESSAGE_CHANNEL_TYPE_QUESTION = SingleChoiceQuestion(
        title = Res.string.alerts_create_jabber_message_channel_type_title.toUiText(),
        items = listOf(
            JABBER_MESSAGE_CHANNEL_ANY,
            JABBER_MESSAGE_CHANNEL_SPECIFIC,
            JABBER_MESSAGE_CHANNEL_DIRECT_MESSAGE,
        ),
    )

    // Jabber message, specific channel
    val JABBER_MESSAGE_SPECIFIC_CHANNEL_QUESTION = FreeformTextQuestion(
        title = Res.string.alerts_create_jabber_message_specific_channel_title.toUiText(),
        placeholder = Res.string.alerts_create_jabber_message_specific_channel_placeholder.toUiText(),
        allowEmpty = false,
    )

    // Jabber message, sender
    val JABBER_MESSAGE_SENDER_QUESTION = FreeformTextQuestion(
        title = Res.string.alerts_create_jabber_message_sender_title.toUiText(),
        placeholder = Res.string.alerts_create_jabber_message_sender_placeholder.toUiText(),
        allowEmpty = true,
    )

    // Jabber message, message contains
    val JABBER_MESSAGE_MESSAGE_CONTAINING_QUESTION = FreeformTextQuestion(
        title = Res.string.alerts_create_jabber_message_message_containing_title.toUiText(),
        placeholder = Res.string.alerts_create_jabber_message_message_containing_placeholder.toUiText(),
        allowEmpty = true,
        isRegexAllowed = true,
    )

    // Alert action
    val ALERT_ACTION_RIFT_NOTIFICATION = FormChoiceItem(id = id++, text = Res.string.alerts_create_action_rift_notification_name.toUiText(), description = Res.string.alerts_create_action_rift_notification_description.toUiText())
    val ALERT_ACTION_SYSTEM_NOTIFICATION = FormChoiceItem(id = id++, text = Res.string.alerts_create_action_system_notification_name.toUiText(), description = Res.string.alerts_create_action_system_notification_description.toUiText())
    val ALERT_ACTION_PUSH_NOTIFICATION = FormChoiceItem(id = id++, text = Res.string.alerts_create_action_push_notification_name.toUiText(), description = Res.string.alerts_create_action_push_notification_description.toUiText())
    val ALERT_ACTION_PLAY_SOUND = FormChoiceItem(id = id++, text = Res.string.alerts_create_action_play_sound_name.toUiText())
    val ALERT_ACTION_SHOW_COLONIES = FormChoiceItem(id = id++, text = Res.string.alerts_create_action_show_colonies_name.toUiText(), description = Res.string.alerts_create_action_show_colonies_description.toUiText())
    val ALERT_ACTION_SHOW_PING = FormChoiceItem(id = id++, text = Res.string.alerts_create_action_show_ping_name.toUiText(), description = Res.string.alerts_create_action_show_ping_description.toUiText())
    val ALERT_ACTION_SHOW_JABBER_MESSAGE = FormChoiceItem(id = id++, text = Res.string.alerts_create_action_show_jabber_message_name.toUiText(), description = Res.string.alerts_create_action_show_jabber_message_description.toUiText())
    val ALERT_ACTION_QUESTION = MultipleChoiceQuestion(
        title = Res.string.alerts_create_action_title.toUiText(),
        items = listOf(
            ALERT_ACTION_RIFT_NOTIFICATION,
            ALERT_ACTION_SYSTEM_NOTIFICATION,
            ALERT_ACTION_PUSH_NOTIFICATION,
            ALERT_ACTION_PLAY_SOUND,
        ),
    )

    // Alert action (Planetary Industry ping version)
    val ALERT_ACTION_PLANETARY_INDUSTRY_QUESTION = MultipleChoiceQuestion(
        title = Res.string.alerts_create_action_planetary_industry_title.toUiText(),
        items = listOf(
            ALERT_ACTION_SHOW_COLONIES,
            ALERT_ACTION_RIFT_NOTIFICATION,
            ALERT_ACTION_SYSTEM_NOTIFICATION,
            ALERT_ACTION_PUSH_NOTIFICATION,
            ALERT_ACTION_PLAY_SOUND,
        ),
    )

    // Alert action (Jabber ping version)
    val ALERT_ACTION_JABBER_PING_QUESTION = MultipleChoiceQuestion(
        title = Res.string.alerts_create_action_jabber_ping_title.toUiText(),
        items = listOf(
            ALERT_ACTION_SHOW_PING,
            ALERT_ACTION_PUSH_NOTIFICATION,
            ALERT_ACTION_PLAY_SOUND,
        ),
    )

    // Alert action (Jabber message version)
    val ALERT_ACTION_JABBER_MESSAGE_QUESTION = MultipleChoiceQuestion(
        title = Res.string.alerts_create_action_jabber_message_title.toUiText(),
        items = listOf(
            ALERT_ACTION_SHOW_JABBER_MESSAGE,
            ALERT_ACTION_RIFT_NOTIFICATION,
            ALERT_ACTION_SYSTEM_NOTIFICATION,
            ALERT_ACTION_PUSH_NOTIFICATION,
            ALERT_ACTION_PLAY_SOUND,
        ),
    )

    // Alert action, sound
    val ALERT_ACTION_SOUND_QUESTION = SoundQuestion(
        title = Res.string.alerts_create_action_sound_title.toUiText(),
    )

    // Alert cooldown
    val ALERT_COOLDOWN_NONE = FormChoiceItem(id = id++, text = Res.string.alerts_create_cooldown_none_name.toUiText())
    val ALERT_COOLDOWN_30_SECONDS = FormChoiceItem(id = id++, text = Res.string.alerts_create_cooldown_30_seconds_name.toUiText())
    val ALERT_COOLDOWN_1_MINUTE = FormChoiceItem(id = id++, text = Res.string.alerts_create_cooldown_1_minute_name.toUiText())
    val ALERT_COOLDOWN_2_MINUTES = FormChoiceItem(id = id++, text = Res.string.alerts_create_cooldown_2_minutes_name.toUiText())
    val ALERT_COOLDOWN_5_MINUTES = FormChoiceItem(id = id++, text = Res.string.alerts_create_cooldown_5_minutes_name.toUiText())
    val ALERT_COOLDOWN_10_MINUTES = FormChoiceItem(id = id++, text = Res.string.alerts_create_cooldown_10_minutes_name.toUiText())
    val ALERT_COOLDOWN_QUESTION = SingleChoiceQuestion(
        title = Res.string.alerts_create_cooldown_title.toUiText(),
        items = listOf(
            ALERT_COOLDOWN_NONE,
            ALERT_COOLDOWN_30_SECONDS,
            ALERT_COOLDOWN_1_MINUTE,
            ALERT_COOLDOWN_2_MINUTES,
            ALERT_COOLDOWN_5_MINUTES,
            ALERT_COOLDOWN_10_MINUTES,
        ),
    )
}
