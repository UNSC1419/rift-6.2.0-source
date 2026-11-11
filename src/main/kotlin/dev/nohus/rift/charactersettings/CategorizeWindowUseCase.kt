package dev.nohus.rift.charactersettings

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.character_settings_eve_window_achievements_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_active_ship_cargo_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_activity_tracker_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_additional_overview_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_agency_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_agent_conversation_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_alliance_chat_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_apply_skill_points_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_ask_link_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_asset_safety_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_assets_locations_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_assets_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_aura_guidance_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_autopilot_settings_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_bookmark_location_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_bookmark_subfolder_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_broadcast_settings_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_bug_report_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_buy_all_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_calculator_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_calendar_event_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_calendar_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_capsuleer_deliveries_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_career_portal_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_cargo_scanner_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_channel_chat_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_channel_settings_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_character_attributes_remap_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_character_preview_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_character_sheet_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_chat_channels_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_chat_filter_settings_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_communications_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_compare_types_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_comparison_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_compression_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_contact_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_contacts_management_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_contacts_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_container_contents_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_contract_details_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_contract_endpoint_search_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_contract_search_finish_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_contracts_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_conversation_invite_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_corp_chat_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_corporation_deliveries_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_corporation_goal_deliveries_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_corporation_hangar_division_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_corporation_hangar_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_corporation_hangars_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_corporation_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_crate_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_create_contract_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_create_transfer_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_deliver_to_structure_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_directional_scanner_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_disconnect_notice_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_drones_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_drop_box_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_dynamic_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_edit_corp_details_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_edit_member_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_edit_theme_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_engine_tools_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_entity_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_expert_system_fanfare_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_faction_chat_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_factional_warfare_enlistment_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_factional_warfare_warzone_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_filter_creation_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_fitting_management_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_fitting_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_fleet_composition_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_fleet_join_request_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_fleet_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_fleet_watchlist_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_floating_cargo_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_fps_monitor_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_give_shares_dialog_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_group_info_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_groups_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_hacking_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_help_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_hide_message_settings_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_industry_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_info_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_insurance_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_insurance_terms_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_insurgency_dashboard_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_inventory_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_item_hangar_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_item_trader_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_item_trader_popup_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_job_board_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_journal_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_kill_report_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_ledger_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_linked_bookmark_folder_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_local_chat_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_locations_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_log_and_messages_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_log_viewer_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_loyalty_point_store_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_mail_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_manage_contact_labels_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_map_browser_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_map_commands_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_map_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_market_buy_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_market_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_market_orders_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_memory_monitor_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_mercenary_dens_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_message_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_mining_scan_results_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_missing_skillbooks_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_modify_market_order_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_monitor_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_moon_mining_schedule_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_moon_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_moon_scanner_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_more_parameter_values_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_multi_buy_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_multi_fit_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_multiple_login_blocked_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_neocom_group_name_popup_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_network_data_monitor_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_new_calendar_event_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_new_feature_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_new_message_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_notepad_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_notify_settings_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_outstanding_calls_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_overview_column_configuration_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_overview_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_overview_settings_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_owner_picker_dialog_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_pi_building_details_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_pi_colony_template_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_pi_export_confirmation_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_pi_template_preview_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_planet_survey_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_planetary_import_export_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_planetary_industry_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_plex_vault_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_pointer_tool_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_portrait_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_preview_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_preview_subsystems_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_probe_scanner_filter_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_probe_scanner_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_project_discovery_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_project_discovery_popup_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_pvp_filament_event_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_raffle_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_random_jump_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_redeem_action_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_redeem_crate_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_redeem_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_register_fleet_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_repair_shop_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_reprocessing_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_rewards_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_save_fitting_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_screenshot_editor_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_search_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_select_job_claim_location_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_selected_object_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_sell_items_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_set_new_name_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_set_quantity_popup_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_ship_cargo_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_ship_colony_resources_hold_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_ship_drone_bay_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_ship_hangar_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_ship_info_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_ship_mining_hold_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_ship_name_dialog_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_ship_password_dialog_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_ship_planetary_commodities_hold_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_ship_restrictions_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_ship_tree_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_skill_plan_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_skill_planner_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_skill_requirement_dialog_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_skin_name_dialog_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_skinr_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_solar_system_map_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_sov_hub_hacking_result_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_space_component_inventory_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_stack_count_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_stack_lines
import dev.nohus.rift.generated.resources.character_settings_eve_window_stack_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_station_container_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_station_inventory_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_station_services_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_structure_browser_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_structure_inventory_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_structure_ship_hangar_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_survey_scanner_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_system_bookmarks_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_task_conversation_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_theaters_of_war_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_trade_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_transfer_money_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_type_list_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_type_selection_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_unknown_chat_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_view_fitting_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_void_space_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_wallet_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_war_declaration_name
import dev.nohus.rift.generated.resources.character_settings_eve_window_wreck_name
import dev.nohus.rift.generated.resources.layout_preview_icon_assets
import dev.nohus.rift.generated.resources.layout_preview_icon_charactersheet
import dev.nohus.rift.generated.resources.layout_preview_icon_chatchannels
import dev.nohus.rift.generated.resources.layout_preview_icon_contacts
import dev.nohus.rift.generated.resources.layout_preview_icon_directional_scanner_64px
import dev.nohus.rift.generated.resources.layout_preview_icon_drones
import dev.nohus.rift.generated.resources.layout_preview_icon_evemail
import dev.nohus.rift.generated.resources.layout_preview_icon_fleet
import dev.nohus.rift.generated.resources.layout_preview_icon_grouplist
import dev.nohus.rift.generated.resources.layout_preview_icon_items
import dev.nohus.rift.generated.resources.layout_preview_icon_journal
import dev.nohus.rift.generated.resources.layout_preview_icon_locations
import dev.nohus.rift.generated.resources.layout_preview_icon_log
import dev.nohus.rift.generated.resources.layout_preview_icon_map
import dev.nohus.rift.generated.resources.layout_preview_icon_market
import dev.nohus.rift.generated.resources.layout_preview_icon_moondrill
import dev.nohus.rift.generated.resources.layout_preview_icon_notepad
import dev.nohus.rift.generated.resources.layout_preview_icon_overview
import dev.nohus.rift.generated.resources.layout_preview_icon_probe_scan
import dev.nohus.rift.generated.resources.layout_preview_icon_selected_object
import dev.nohus.rift.generated.resources.layout_preview_icon_station
import dev.nohus.rift.generated.resources.layout_preview_icon_wallet
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import org.jetbrains.compose.resources.DrawableResource
import org.koin.core.annotation.Single

@Single
class CategorizeWindowUseCase {

    data class EveWindow(
        val name: String,
        val displayName: UiText = name.toUiText(),
        val inSpace: Boolean = true,
        val inStation: Boolean = true,
        val inStructure: Boolean = true,
        val isUnknown: Boolean = false,
        /**
         * Windows that EVE recreates on startup or session change, either from openWindows or another system.
         */
        val isPersistent: Boolean = false,
        /**
         * Whether EVE uses the openWindows setting to decide if this window should be opened.
         */
        val isOpenStateControlled: Boolean = true,
        /**
         * Whether the window can be closed. Some windows are always created by EVE for the current context.
         */
        val isCloseable: Boolean = true,
        /**
         * Whether RIFT can offer to add the window to openWindows.
         */
        val isAddable: Boolean = true,
        /**
         * EVE only restores the saved minimized state for chat windows and chat window stacks.
         */
        val isMinimizedStatePersistent: Boolean = false,
        val icon: DrawableResource? = null,
    )

    val persistentWindows = mapOf(
        "bytes:overview" to EveWindow("Overview", Res.string.character_settings_eve_window_overview_name.toUiText(), inStation = false, inStructure = false, isPersistent = true, isOpenStateControlled = false, isCloseable = false, icon = Res.drawable.layout_preview_icon_overview),
        "bytes:selecteditemview" to EveWindow("Selected Object", Res.string.character_settings_eve_window_selected_object_name.toUiText(), inStation = false, inStructure = false, isPersistent = true, isOpenStateControlled = false, isCloseable = false, icon = Res.drawable.layout_preview_icon_selected_object),
        "bytes:lobbyWnd" to EveWindow("Station Services", Res.string.character_settings_eve_window_station_services_name.toUiText(), inSpace = false, isPersistent = true, isOpenStateControlled = false, isCloseable = false, icon = Res.drawable.layout_preview_icon_station),
        "bytes:InventoryStructure" to EveWindow("Structure Inventory", Res.string.character_settings_eve_window_structure_inventory_name.toUiText(), inSpace = false, inStation = false, isPersistent = true, icon = Res.drawable.layout_preview_icon_items),
        "bytes:InventoryStation" to EveWindow("Station Inventory", Res.string.character_settings_eve_window_station_inventory_name.toUiText(), inSpace = false, inStructure = false, isPersistent = true, icon = Res.drawable.layout_preview_icon_items),
        "bytes:InventorySpace" to EveWindow("Inventory", Res.string.character_settings_eve_window_inventory_name.toUiText(), inStation = false, inStructure = false, isPersistent = true, icon = Res.drawable.layout_preview_icon_items),
        "bytes:StationItems" to EveWindow("Item Hangar", Res.string.character_settings_eve_window_item_hangar_name.toUiText(), inSpace = false, isPersistent = true, icon = Res.drawable.layout_preview_icon_items),
        "bytes:StationShips" to EveWindow("Ship Hangar", Res.string.character_settings_eve_window_ship_hangar_name.toUiText(), inSpace = false, isPersistent = true, icon = Res.drawable.layout_preview_icon_items),
        "bytes:StationCorpDeliveries" to EveWindow("Corporation Deliveries", Res.string.character_settings_eve_window_corporation_deliveries_name.toUiText(), inSpace = false, isPersistent = true, isAddable = false, icon = Res.drawable.layout_preview_icon_items),
        "bytes:CorporationItemDeliveries" to EveWindow("Capsuleer Deliveries", Res.string.character_settings_eve_window_capsuleer_deliveries_name.toUiText(), inSpace = false, isPersistent = true, icon = Res.drawable.layout_preview_icon_items),
        "bytes:StationCorpHangars" to EveWindow("Corporation Hangars", Res.string.character_settings_eve_window_corporation_hangars_name.toUiText(), inSpace = false, inStructure = false, isPersistent = true, isAddable = false, icon = Res.drawable.layout_preview_icon_items),
        "bytes:StructureCorpHangars" to EveWindow("Corporation Hangars", Res.string.character_settings_eve_window_corporation_hangars_name.toUiText(), inSpace = false, inStation = false, isPersistent = true, isAddable = false, icon = Res.drawable.layout_preview_icon_items),
        "bytes:fleetwindow" to EveWindow("Fleet", Res.string.character_settings_eve_window_fleet_name.toUiText(), isPersistent = true, isOpenStateControlled = false, isCloseable = false, icon = Res.drawable.layout_preview_icon_fleet),
        "bytes:watchlistpanel" to EveWindow("Fleet Watchlist", Res.string.character_settings_eve_window_fleet_watchlist_name.toUiText(), isPersistent = true, isOpenStateControlled = false, isCloseable = false, icon = Res.drawable.layout_preview_icon_grouplist),
        "bytes:chatchannel_alliance" to EveWindow("Alliance Chat", Res.string.character_settings_eve_window_alliance_chat_name.toUiText(), isPersistent = true, isMinimizedStatePersistent = true, icon = Res.drawable.layout_preview_icon_chatchannels),
        "bytes:chatchannel_corp" to EveWindow("Corp Chat", Res.string.character_settings_eve_window_corp_chat_name.toUiText(), isPersistent = true, isMinimizedStatePersistent = true, icon = Res.drawable.layout_preview_icon_chatchannels),
        "bytes:chatchannel_faction" to EveWindow("Faction Chat", Res.string.character_settings_eve_window_faction_chat_name.toUiText(), isPersistent = true, isMinimizedStatePersistent = true, icon = Res.drawable.layout_preview_icon_chatchannels),
        "bytes:chatchannel_local" to EveWindow("Local Chat", Res.string.character_settings_eve_window_local_chat_name.toUiText(), isPersistent = true, isMinimizedStatePersistent = true, icon = Res.drawable.layout_preview_icon_chatchannels),
        "bytes:XmppChatChannels" to EveWindow("Chat Channels", Res.string.character_settings_eve_window_chat_channels_name.toUiText(), isPersistent = true, icon = Res.drawable.layout_preview_icon_chatchannels),
        "bytes:droneview" to EveWindow("Drones", Res.string.character_settings_eve_window_drones_name.toUiText(), inStation = false, inStructure = false, isPersistent = true, isOpenStateControlled = false, isCloseable = false, icon = Res.drawable.layout_preview_icon_drones),
        "bytes:MoonScanner" to EveWindow("Moon Scanner", Res.string.character_settings_eve_window_moon_scanner_name.toUiText(), inStation = false, inStructure = false, isPersistent = true, icon = Res.drawable.layout_preview_icon_moondrill),
        "bytes:addressbook" to EveWindow("Contacts", Res.string.character_settings_eve_window_contacts_name.toUiText(), isPersistent = true, icon = Res.drawable.layout_preview_icon_contacts),
        "bytes:standaloneBookmarkWnd" to EveWindow("System Bookmarks", Res.string.character_settings_eve_window_system_bookmarks_name.toUiText(), inStation = false, inStructure = false, isPersistent = true, icon = Res.drawable.layout_preview_icon_locations),
        "bytes:probeScannerWindow" to EveWindow("Probe Scanner", Res.string.character_settings_eve_window_probe_scanner_name.toUiText(), inStation = false, inStructure = false, isPersistent = true, icon = Res.drawable.layout_preview_icon_probe_scan),
        "bytes:directionalScannerWindow" to EveWindow("Directional Scanner", Res.string.character_settings_eve_window_directional_scanner_name.toUiText(), inStation = false, inStructure = false, isPersistent = true, icon = Res.drawable.layout_preview_icon_directional_scanner_64px),
        "bytes:solar_system_map_panel" to EveWindow("Solar System Map", Res.string.character_settings_eve_window_solar_system_map_name.toUiText(), inStation = false, inStructure = false, isPersistent = true, icon = Res.drawable.layout_preview_icon_map),
        "bytes:market" to EveWindow("Market", Res.string.character_settings_eve_window_market_name.toUiText(), isPersistent = true, icon = Res.drawable.layout_preview_icon_market),
        "bytes:locations" to EveWindow("Locations", Res.string.character_settings_eve_window_locations_name.toUiText(), isPersistent = true, icon = Res.drawable.layout_preview_icon_locations),
        "bytes:assets" to EveWindow("Assets", Res.string.character_settings_eve_window_assets_name.toUiText(), isPersistent = true, icon = Res.drawable.layout_preview_icon_assets),
        "bytes:notepad" to EveWindow("Notepad", Res.string.character_settings_eve_window_notepad_name.toUiText(), isPersistent = true, icon = Res.drawable.layout_preview_icon_notepad),
        "bytes:charactersheet" to EveWindow("Character Sheet", Res.string.character_settings_eve_window_character_sheet_name.toUiText(), isPersistent = true, icon = Res.drawable.layout_preview_icon_charactersheet),
        "bytes:walletWindow" to EveWindow("Wallet", Res.string.character_settings_eve_window_wallet_name.toUiText(), isPersistent = true, icon = Res.drawable.layout_preview_icon_wallet),
        "bytes:mail" to EveWindow("Mail", Res.string.character_settings_eve_window_mail_name.toUiText(), isPersistent = true, icon = Res.drawable.layout_preview_icon_evemail),
        "bytes:journal" to EveWindow("Journal", Res.string.character_settings_eve_window_journal_name.toUiText(), isPersistent = true, icon = Res.drawable.layout_preview_icon_journal),
        "bytes:logger" to EveWindow("Log and Messages", Res.string.character_settings_eve_window_log_and_messages_name.toUiText(), isPersistent = true, icon = Res.drawable.layout_preview_icon_log),
    ) + corporationHangarDivisions.associate { division ->
        "bytes:StationCorpHangar_$division" to EveWindow(
            name = division.corporationHangarName,
            displayName = division.getCorporationHangarText(),
            inSpace = false,
            inStructure = false,
            isPersistent = true,
            isAddable = false,
            icon = Res.drawable.layout_preview_icon_items,
        )
    } + corporationHangarDivisions.associate { division ->
        "bytes:StructureCorpHangar_$division" to EveWindow(
            name = division.corporationHangarName,
            displayName = division.getCorporationHangarText(),
            inSpace = false,
            inStation = false,
            isPersistent = true,
            isAddable = false,
            icon = Res.drawable.layout_preview_icon_items,
        )
    }

    operator fun invoke(window: String, children: List<String>, joinedChatChannels: Map<String, String>): EveWindow {
        persistentWindows[window]?.let { return it }
        return when (window) {
            "bytes:tradeWnd" -> EveWindow("Trade", Res.string.character_settings_eve_window_trade_name.toUiText())
            "bytes:StructureBrowser" -> EveWindow("Structure Browser", Res.string.character_settings_eve_window_structure_browser_name.toUiText())
            "bytes:TaskConversationWindow" -> EveWindow("Task Conversation", Res.string.character_settings_eve_window_task_conversation_name.toUiText())
            "bytes:CovidWindow" -> EveWindow("Project Discovery", Res.string.character_settings_eve_window_project_discovery_name.toUiText())
            "bytes:MultiBuy" -> EveWindow("Multi Buy", Res.string.character_settings_eve_window_multi_buy_name.toUiText())
            "bytes:SurveyScanView" -> EveWindow("Survey Scanner", Res.string.character_settings_eve_window_survey_scanner_name.toUiText())
            "bytes:HackingWindow" -> EveWindow("Hacking", Res.string.character_settings_eve_window_hacking_name.toUiText())
            "bytes:help" -> EveWindow("Help", Res.string.character_settings_eve_window_help_name.toUiText())
            "bytes:outstandingcalls" -> EveWindow("Outstanding Calls", Res.string.character_settings_eve_window_outstanding_calls_name.toUiText())
            "bytes:BugReportingWindow" -> EveWindow("Bug Report", Res.string.character_settings_eve_window_bug_report_name.toUiText())
            "bytes:bookmarkLocationWindow" -> EveWindow("Bookmark Location", Res.string.character_settings_eve_window_bookmark_location_name.toUiText())
            "bytes:CrateRedeemedWindow" -> EveWindow("Redeem Crate", Res.string.character_settings_eve_window_redeem_crate_name.toUiText())
            "bytes:myMercDensWnd" -> EveWindow("Mercenary Dens", Res.string.character_settings_eve_window_mercenary_dens_name.toUiText())
            "bytes:marketmodifyaction" -> EveWindow("Modify Market Order", Res.string.character_settings_eve_window_modify_market_order_name.toUiText())
            "bytes:SellItemsWindow" -> EveWindow("Sell Items", Res.string.character_settings_eve_window_sell_items_name.toUiText())
            "bytes:PreviewSubSystems" -> EveWindow("Preview Subsystems", Res.string.character_settings_eve_window_preview_subsystems_name.toUiText())
            "bytes:sovHubHackingResultWnd" -> EveWindow("Sov Hub Hacking Result", Res.string.character_settings_eve_window_sov_hub_hacking_result_name.toUiText())
            "utf8:TemplatePreviewWindow" -> EveWindow("PI Template Preview", Res.string.character_settings_eve_window_pi_template_preview_name.toUiText())
            "bytes:missingSkillbooksWnd" -> EveWindow("Missing Skillbooks", Res.string.character_settings_eve_window_missing_skillbooks_name.toUiText())
            "bytes:RedeemActionMessage" -> EveWindow("Redeem Action", Res.string.character_settings_eve_window_redeem_action_name.toUiText())
            "bytes:theme_edit_window" -> EveWindow("Edit Theme", Res.string.character_settings_eve_window_edit_theme_name.toUiText())
            "bytes:ship_name_dialog" -> EveWindow("Ship Name Dialog", Res.string.character_settings_eve_window_ship_name_dialog_name.toUiText())
            "bytes:enterShipPassword" -> EveWindow("Ship Password Dialog", Res.string.character_settings_eve_window_ship_password_dialog_name.toUiText())
            "bytes:PlanetPinWindow" -> EveWindow("PI Building Details", Res.string.character_settings_eve_window_pi_building_details_name.toUiText())
            "bytes:marketbuyaction" -> EveWindow("Market Buy", Res.string.character_settings_eve_window_market_buy_name.toUiText())
            "bytes:PlanetSurvey" -> EveWindow("Planet Survey", Res.string.character_settings_eve_window_planet_survey_name.toUiText())
            "bytes:SkinNameDialogue" -> EveWindow("Skin Name Dialog", Res.string.character_settings_eve_window_skin_name_dialog_name.toUiText())
            "bytes:itemTraderDialogPopup" -> EveWindow("Item Trader Popup", Res.string.character_settings_eve_window_item_trader_popup_name.toUiText())
            "bytes:CrateWindow" -> EveWindow("Crate", Res.string.character_settings_eve_window_crate_name.toUiText())
            "bytes:DisconnectNotice" -> EveWindow("Disconnect Notice", Res.string.character_settings_eve_window_disconnect_notice_name.toUiText())
            "bytes:comfirmPIexport" -> EveWindow("PI Export Confirmation", Res.string.character_settings_eve_window_pi_export_confirmation_name.toUiText())
            "bytes:createcontract" -> EveWindow("Create Contract", Res.string.character_settings_eve_window_create_contract_name.toUiText())
            "bytes:contractEndpointSearch" -> EveWindow("Contract Endpoint Search", Res.string.character_settings_eve_window_contract_endpoint_search_name.toUiText())
            "bytes:skill_requirement_dialog" -> EveWindow("Skill Requirement Dialog", Res.string.character_settings_eve_window_skill_requirement_dialog_name.toUiText())
            "bytes:InsuranceTermsWindow" -> EveWindow("Insurance Terms", Res.string.character_settings_eve_window_insurance_terms_name.toUiText())
            "bytes:setNewName" -> EveWindow("Set New Name", Res.string.character_settings_eve_window_set_new_name_name.toUiText())
            "bytes:DeliverToStructure" -> EveWindow("Deliver to Structure", Res.string.character_settings_eve_window_deliver_to_structure_name.toUiText())
            "bytes:SpaceComponentInventory" -> EveWindow("Space Component Inventory", Res.string.character_settings_eve_window_space_component_inventory_name.toUiText())
            "bytes:contractFinishStepSearch" -> EveWindow("Contract Search Finish", Res.string.character_settings_eve_window_contract_search_finish_name.toUiText())
            "bytes:setQuantityPopup" -> EveWindow("Set Quantity Popup", Res.string.character_settings_eve_window_set_quantity_popup_name.toUiText())
            "bytes:askLink" -> EveWindow("Ask Link", Res.string.character_settings_eve_window_ask_link_name.toUiText())
            "bytes:message" -> EveWindow("Message", Res.string.character_settings_eve_window_message_name.toUiText())
            "bytes:MonitorWnd" -> EveWindow("Monitor", Res.string.character_settings_eve_window_monitor_name.toUiText())
            "bytes:select_jobboard_claim_location" -> EveWindow("Select Job Claim Location", Res.string.character_settings_eve_window_select_job_claim_location_name.toUiText())
            "bytes:SkillPlanPanelWnd" -> EveWindow("Skill Plan", Res.string.character_settings_eve_window_skill_plan_name.toUiText())
            "bytes:PortraitWindow" -> EveWindow("Portrait", Res.string.character_settings_eve_window_portrait_name.toUiText())
            "bytes:AutopilotSettings" -> EveWindow("Autopilot Settings", Res.string.character_settings_eve_window_autopilot_settings_name.toUiText())
            "bytes:containerContentWindow" -> EveWindow("Container Contents", Res.string.character_settings_eve_window_container_contents_name.toUiText())
            "bytes:NewMessageWindow" -> EveWindow("New Message", Res.string.character_settings_eve_window_new_message_name.toUiText())
            "bytes:careerPortal" -> EveWindow("Career Portal", Res.string.character_settings_eve_window_career_portal_name.toUiText())
            "bytes:buyAllMessageBox" -> EveWindow("Buy All", Res.string.character_settings_eve_window_buy_all_name.toUiText())
            "bytes:moreParameterValues" -> EveWindow("More Parameter Values", Res.string.character_settings_eve_window_more_parameter_values_name.toUiText())
            "bytes:ChatFilterSettings" -> EveWindow("Chat Filter Settings", Res.string.character_settings_eve_window_chat_filter_settings_name.toUiText())
            "bytes:AgencyWndNew" -> EveWindow("Agency", Res.string.character_settings_eve_window_agency_name.toUiText())
            "bytes:ApplySkillPointsWindow" -> EveWindow("Apply Skill Points", Res.string.character_settings_eve_window_apply_skill_points_name.toUiText())
            "bytes:mySearch" -> EveWindow("Search", Res.string.character_settings_eve_window_search_name.toUiText())
            "bytes:shipInfoWindow" -> EveWindow("Ship Info", Res.string.character_settings_eve_window_ship_info_name.toUiText())
            "bytes:calendarEventWnd" -> EveWindow("Calendar Event", Res.string.character_settings_eve_window_calendar_event_name.toUiText())
            "bytes:contractdetails" -> EveWindow("Contract Details", Res.string.character_settings_eve_window_contract_details_name.toUiText())
            "bytes:AssetSafetyDeliverWindow" -> EveWindow("Asset Safety", Res.string.character_settings_eve_window_asset_safety_name.toUiText())
            "bytes:AchievementsWindow" -> EveWindow("Achievements", Res.string.character_settings_eve_window_achievements_name.toUiText())
            "bytes:probeScannerFilterEditor" -> EveWindow("Probe Scanner Filter", Res.string.character_settings_eve_window_probe_scanner_filter_name.toUiText())
            "bytes:previewCharacterWnd" -> EveWindow("Character Preview", Res.string.character_settings_eve_window_character_preview_name.toUiText())
            "bytes:ShipDroneBay" -> EveWindow("Ship Drone Bay", Res.string.character_settings_eve_window_ship_drone_bay_name.toUiText())
            "bytes:ledger" -> EveWindow("Ledger", Res.string.character_settings_eve_window_ledger_name.toUiText())
            "bytes:MapCmdWindow" -> EveWindow("Map Commands", Res.string.character_settings_eve_window_map_commands_name.toUiText())
            "bytes:KillReportWnd" -> EveWindow("Kill Report", Res.string.character_settings_eve_window_kill_report_name.toUiText())
            "bytes:telecom" -> EveWindow("Communications", Res.string.character_settings_eve_window_communications_name.toUiText())
            "bytes:typecompare" -> EveWindow("Compare Types", Res.string.character_settings_eve_window_compare_types_name.toUiText())
            "bytes:ManageLabelscontact" -> EveWindow("Manage Contact Labels", Res.string.character_settings_eve_window_manage_contact_labels_name.toUiText())
            "bytes:industryWnd" -> EveWindow("Industry", Res.string.character_settings_eve_window_industry_name.toUiText())
            "bytes:MemoryMonitor" -> EveWindow("Memory Monitor", Res.string.character_settings_eve_window_memory_monitor_name.toUiText())
            "bytes:RegisterFleetWindow" -> EveWindow("Register Fleet", Res.string.character_settings_eve_window_register_fleet_name.toUiText())
            "bytes:dropboxWnd" -> EveWindow("Drop Box", Res.string.character_settings_eve_window_drop_box_name.toUiText())
            "bytes:FleetJoinRequestWindow" -> EveWindow("Fleet Join Request", Res.string.character_settings_eve_window_fleet_join_request_name.toUiText())
            "bytes:HideMessageSettingWindow" -> EveWindow("Hide Message Settings", Res.string.character_settings_eve_window_hide_message_settings_name.toUiText())
            "bytes:EntityWindow" -> EveWindow("Entity", Res.string.character_settings_eve_window_entity_name.toUiText())
            "bytes:ExpertSystemFanfareWindow" -> EveWindow("Expert System Fanfare", Res.string.character_settings_eve_window_expert_system_fanfare_name.toUiText())
            "bytes:warDeclareWnd" -> EveWindow("War Declaration", Res.string.character_settings_eve_window_war_declaration_name.toUiText())
            "bytes:fwEnlistmentWnd" -> EveWindow("Factional Warfare Enlistment", Res.string.character_settings_eve_window_factional_warfare_enlistment_name.toUiText())
            "bytes:lpstore" -> EveWindow("Loyalty Point Store", Res.string.character_settings_eve_window_loyalty_point_store_name.toUiText())
            "bytes:RandomJumpActivationWindow" -> EveWindow("Random Jump", Res.string.character_settings_eve_window_random_jump_name.toUiText())
            "bytes:FleetComposition" -> EveWindow("Fleet Composition", Res.string.character_settings_eve_window_fleet_composition_name.toUiText())
            "bytes:ViewFitting" -> EveWindow("View Fitting", Res.string.character_settings_eve_window_view_fitting_name.toUiText())
            "bytes:PCOwnerPickerDialog" -> EveWindow("Owner Picker Dialog", Res.string.character_settings_eve_window_owner_picker_dialog_name.toUiText())
            "bytes:ship_restrictions" -> EveWindow("Ship Restrictions", Res.string.character_settings_eve_window_ship_restrictions_name.toUiText())
            "bytes:mapbrowser" -> EveWindow("Map Browser", Res.string.character_settings_eve_window_map_browser_name.toUiText())
            "bytes:mooonminingScheduling" -> EveWindow("Moon Mining Schedule", Res.string.character_settings_eve_window_moon_mining_schedule_name.toUiText())
            "bytes:OverviewTabColumnConfig" -> EveWindow("Overview Column Configuration", Res.string.character_settings_eve_window_overview_column_configuration_name.toUiText())
            "bytes:AuraGuidanceWindow" -> EveWindow("Aura Guidance", Res.string.character_settings_eve_window_aura_guidance_name.toUiText())
            "bytes:overviewsettings" -> EveWindow("Overview Settings", Res.string.character_settings_eve_window_overview_settings_name.toUiText())
            "bytes:SkillPlanner" -> EveWindow("Skill Planner", Res.string.character_settings_eve_window_skill_planner_name.toUiText())
            "bytes:contracts" -> EveWindow("Contracts", Res.string.character_settings_eve_window_contracts_name.toUiText())
            "bytes:ShipSKINRWindow" -> EveWindow("SKINR", Res.string.character_settings_eve_window_skinr_name.toUiText())
            "bytes:EditMemberDialog" -> EveWindow("Edit Member", Res.string.character_settings_eve_window_edit_member_name.toUiText())
            "bytes:ProjectDiscoveryDialogPopup" -> EveWindow("Project Discovery Popup", Res.string.character_settings_eve_window_project_discovery_popup_name.toUiText())
            "bytes:calendar" -> EveWindow("Calendar", Res.string.character_settings_eve_window_calendar_name.toUiText())
            "bytes:compression_window" -> EveWindow("Compression", Res.string.character_settings_eve_window_compression_name.toUiText())
            "bytes:previewWnd" -> EveWindow("Preview", Res.string.character_settings_eve_window_preview_name.toUiText())
            "bytes:MultiLoginBlockedWindow" -> EveWindow("Multiple Login Blocked", Res.string.character_settings_eve_window_multiple_login_blocked_name.toUiText())
            "bytes:rewardsWnd" -> EveWindow("Rewards", Res.string.character_settings_eve_window_rewards_name.toUiText())
            "bytes:insurance" -> EveWindow("Insurance", Res.string.character_settings_eve_window_insurance_name.toUiText())
            "bytes:ShipTree" -> EveWindow("Ship Tree", Res.string.character_settings_eve_window_ship_tree_name.toUiText())
            "bytes:primary_map_panel" -> EveWindow("Map", Res.string.character_settings_eve_window_map_name.toUiText())
            "bytes:PVPFilamentEventWindow" -> EveWindow("PVP Filament Event", Res.string.character_settings_eve_window_pvp_filament_event_name.toUiText())
            "bytes:NewFeatureNotifyWnd" -> EveWindow("New Feature", Res.string.character_settings_eve_window_new_feature_name.toUiText())
            "bytes:fwWarzoneDashboard" -> EveWindow("Factional Warfare Warzone", Res.string.character_settings_eve_window_factional_warfare_warzone_name.toUiText())
            "bytes:repairshop" -> EveWindow("Repair Shop", Res.string.character_settings_eve_window_repair_shop_name.toUiText())
            "bytes:mailReadingWnd" -> EveWindow("Mail", Res.string.character_settings_eve_window_mail_name.toUiText())
            "bytes:broadcastsettings" -> EveWindow("Broadcast Settings", Res.string.character_settings_eve_window_broadcast_settings_name.toUiText())
            "bytes:RaffleWindow" -> EveWindow("Raffle", Res.string.character_settings_eve_window_raffle_name.toUiText())
            "bytes:EngineTools" -> EveWindow("Engine Tools", Res.string.character_settings_eve_window_engine_tools_name.toUiText())
            "bytes:theatersOfWarDashboardWnd" -> EveWindow("Theaters of War", Res.string.character_settings_eve_window_theaters_of_war_name.toUiText())
            "bytes:VoidSpaceActivationWindow" -> EveWindow("Void Space", Res.string.character_settings_eve_window_void_space_name.toUiText())
            "bytes:multiFitWnd" -> EveWindow("Multi Fit", Res.string.character_settings_eve_window_multi_fit_name.toUiText())
            "bytes:PlanetaryImportExportUI" -> EveWindow("Planetary Import Export", Res.string.character_settings_eve_window_planetary_import_export_name.toUiText())
            "bytes:redeem" -> EveWindow("Redeem", Res.string.character_settings_eve_window_redeem_name.toUiText())
            "bytes:reprocessingWindow" -> EveWindow("Reprocessing", Res.string.character_settings_eve_window_reprocessing_name.toUiText())
            "bytes:corporation" -> EveWindow("Corporation", Res.string.character_settings_eve_window_corporation_name.toUiText())
            "bytes:GiveSharesDialog" -> EveWindow("Give Shares Dialog", Res.string.character_settings_eve_window_give_shares_dialog_name.toUiText())
            "bytes:marketOrders" -> EveWindow("Market Orders", Res.string.character_settings_eve_window_market_orders_name.toUiText())
            "bytes:type_list_window" -> EveWindow("Type List", Res.string.character_settings_eve_window_type_list_name.toUiText())
            "bytes:networkdatamonitor" -> EveWindow("Network Data Monitor", Res.string.character_settings_eve_window_network_data_monitor_name.toUiText())
            "bytes:FittingMgmt" -> EveWindow("Fitting Management", Res.string.character_settings_eve_window_fitting_management_name.toUiText())
            "bytes:moon" -> EveWindow("Moon", Res.string.character_settings_eve_window_moon_name.toUiText())
            "bytes:StructureShipHangar" -> EveWindow("Structure Ship Hangar", Res.string.character_settings_eve_window_structure_ship_hangar_name.toUiText())
            "bytes:ScreenshotEditingWnd" -> EveWindow("Screenshot Editor", Res.string.character_settings_eve_window_screenshot_editor_name.toUiText())
            "bytes:LogViewer" -> EveWindow("Log Viewer", Res.string.character_settings_eve_window_log_viewer_name.toUiText())
            "bytes:InsurgentsDashboard" -> EveWindow("Insurgency Dashboard", Res.string.character_settings_eve_window_insurgency_dashboard_name.toUiText())
            "bytes:planetWindow" -> EveWindow("Planetary Industry", Res.string.character_settings_eve_window_planetary_industry_name.toUiText())
            "bytes:MiningScanResultsWindow2" -> EveWindow("Mining Scan Results", Res.string.character_settings_eve_window_mining_scan_results_name.toUiText())
            "bytes:pointerToolWnd" -> EveWindow("Pointer Tool", Res.string.character_settings_eve_window_pointer_tool_name.toUiText())
            "bytes:comparison_window" -> EveWindow("Comparison", Res.string.character_settings_eve_window_comparison_name.toUiText())
            "bytes:fittingWnd" -> EveWindow("Fitting", Res.string.character_settings_eve_window_fitting_name.toUiText())
            "bytes:itemTrader" -> EveWindow("Item Trader", Res.string.character_settings_eve_window_item_trader_name.toUiText())
            "bytes:job_board" -> EveWindow("Job Board", Res.string.character_settings_eve_window_job_board_name.toUiText())
            "bytes:ActivityTracker" -> EveWindow("Activity Tracker", Res.string.character_settings_eve_window_activity_tracker_name.toUiText())
            "bytes:FilterCreationWindow" -> EveWindow("Filter Creation", Res.string.character_settings_eve_window_filter_creation_name.toUiText())
            "bytes:createTransfer" -> EveWindow("Create Transfer", Res.string.character_settings_eve_window_create_transfer_name.toUiText())
            "bytes:LinkedBookmarkFolderWindow" -> EveWindow("Linked Bookmark Folder", Res.string.character_settings_eve_window_linked_bookmark_folder_name.toUiText())
            "bytes:TransferMoney" -> EveWindow("Transfer Money", Res.string.character_settings_eve_window_transfer_money_name.toUiText())
            "bytes:editcorpdetails" -> EveWindow("Edit Corp Details", Res.string.character_settings_eve_window_edit_corp_details_name.toUiText())
            "bytes:ActiveShipCargo" -> EveWindow("Active Ship Cargo", Res.string.character_settings_eve_window_active_ship_cargo_name.toUiText())
            "bytes:infowindow" -> EveWindow("Info", Res.string.character_settings_eve_window_info_name.toUiText())
            "bytes:NotifySettingsWindow" -> EveWindow("Notify Settings", Res.string.character_settings_eve_window_notify_settings_name.toUiText())
            "bytes:calculator" -> EveWindow("Calculator", Res.string.character_settings_eve_window_calculator_name.toUiText())
            "bytes:PlexVault" -> EveWindow("Plex Vault", Res.string.character_settings_eve_window_plex_vault_name.toUiText())
            "bytes:ItemWreck" -> EveWindow("Wreck", Res.string.character_settings_eve_window_wreck_name.toUiText())
            "bytes:ItemFloatingCargo" -> EveWindow("Floating Cargo", Res.string.character_settings_eve_window_floating_cargo_name.toUiText())
            "bytes:StationContainer" -> EveWindow("Station Container", Res.string.character_settings_eve_window_station_container_name.toUiText())
            "bytes:ShipGeneralMiningHold" -> EveWindow("Ship Mining Hold", Res.string.character_settings_eve_window_ship_mining_hold_name.toUiText())
            "bytes:fpsMonitor2" -> EveWindow("FPS Monitor", Res.string.character_settings_eve_window_fps_monitor_name.toUiText())
            "bytes:ShipCargo" -> EveWindow("Ship Cargo", Res.string.character_settings_eve_window_ship_cargo_name.toUiText())
            "bytes:ShipPlanetaryCommoditiesHold" -> EveWindow("Ship Planetary Commodities Hold", Res.string.character_settings_eve_window_ship_planetary_commodities_hold_name.toUiText())
            "bytes:ShipColonyResourcesHold" -> EveWindow("Ship Colony Resources Hold", Res.string.character_settings_eve_window_ship_colony_resources_hold_name.toUiText())
            "bytes:GroupsWnd" -> EveWindow("Groups", Res.string.character_settings_eve_window_groups_name.toUiText())
            "bytes:cargoscanner" -> EveWindow("Cargo Scanner", Res.string.character_settings_eve_window_cargo_scanner_name.toUiText())
            "bytes:NeocomGroupNamePopup" -> EveWindow("Neocom Group Name Popup", Res.string.character_settings_eve_window_neocom_group_name_popup_name.toUiText())
            "bytes:attributerespecification" -> EveWindow("Character Attributes Remap", Res.string.character_settings_eve_window_character_attributes_remap_name.toUiText())
            else -> {
                if (window.startsWith("utf8:chatchannel_")) {
                    val id = window.substringAfter("channel_")
                    if (id in joinedChatChannels) {
                        EveWindow(
                            "${joinedChatChannels[id]} Chat",
                            Res.string.character_settings_eve_window_channel_chat_name.toUiText(joinedChatChannels[id].toString().toUiTextArgument()),
                            isPersistent = true,
                            isOpenStateControlled = false,
                            isMinimizedStatePersistent = true,
                            icon = Res.drawable.layout_preview_icon_chatchannels,
                        )
                    } else {
                        EveWindow("Chat $id", Res.string.character_settings_eve_window_unknown_chat_name.toUiText(id.toUiTextArgument()), isPersistent = false)
                    }
                } else if (window.startsWith("bytes:contactmanagement")) {
                    EveWindow("Contacts Management", Res.string.character_settings_eve_window_contacts_management_name.toUiText())
                } else if (window.startsWith("bytes:overview_")) {
                    EveWindow("Additional Overview", Res.string.character_settings_eve_window_additional_overview_name.toUiText(), inStation = false, inStructure = false, isPersistent = true, isOpenStateControlled = false, isCloseable = false, icon = Res.drawable.layout_preview_icon_overview)
                } else if (window.isCorporationHangarDivision("bytes:StationCorpHangar_")) {
                    EveWindow("Corporation Hangar", Res.string.character_settings_eve_window_corporation_hangar_name.toUiText(), inSpace = false, inStructure = false, isPersistent = true, icon = Res.drawable.layout_preview_icon_items)
                } else if (window.isCorporationHangarDivision("bytes:StructureCorpHangar_")) {
                    EveWindow("Corporation Hangar", Res.string.character_settings_eve_window_corporation_hangar_name.toUiText(), inSpace = false, inStation = false, isPersistent = true, icon = Res.drawable.layout_preview_icon_items)
                } else if (window.startsWith("bytes:ChatInvitation_")) {
                    EveWindow("Conversation Invite", Res.string.character_settings_eve_window_conversation_invite_name.toUiText())
                } else if (window.startsWith("bytes:Save_ViewFitting_")) {
                    EveWindow("Save Fitting", Res.string.character_settings_eve_window_save_fitting_name.toUiText())
                } else if (window.startsWith("bytes:bookmarkSubfolderWindow_newSub_")) {
                    EveWindow("Bookmark Subfolder", Res.string.character_settings_eve_window_bookmark_subfolder_name.toUiText())
                } else if (window.startsWith("utf8:('assetslocations_")) {
                    EveWindow("Assets Locations", Res.string.character_settings_eve_window_assets_locations_name.toUiText())
                } else if (window.startsWith("bytes:AgentConversation_")) {
                    EveWindow("Agent Conversation", Res.string.character_settings_eve_window_agent_conversation_name.toUiText())
                } else if (window.startsWith("bytes:mail_readingWnd_")) {
                    EveWindow("Mail", Res.string.character_settings_eve_window_mail_name.toUiText())
                } else if (window.startsWith("bytes:KillReport_")) {
                    EveWindow("Kill Report", Res.string.character_settings_eve_window_kill_report_name.toUiText())
                } else if (window.startsWith("bytes:groupInfoWnd_")) {
                    EveWindow("Group Info", Res.string.character_settings_eve_window_group_info_name.toUiText())
                } else if (window.startsWith("utf8:ChannelSettingsDlg_")) {
                    EveWindow("Channel Settings", Res.string.character_settings_eve_window_channel_settings_name.toUiText())
                } else if (window.startsWith("utf8:('contact',")) {
                    EveWindow("Contact", Res.string.character_settings_eve_window_contact_name.toUiText())
                } else if (window.startsWith("utf8:ColonyTemplateWindow")) {
                    EveWindow("PI Colony Template", Res.string.character_settings_eve_window_pi_colony_template_name.toUiText())
                } else if (window.startsWith("utf8:('TypeSel',")) {
                    EveWindow("Type Selection", Res.string.character_settings_eve_window_type_selection_name.toUiText())
                } else if (window.startsWith("bytes:calendarNewEventWnd_")) {
                    EveWindow("New Calendar Event", Res.string.character_settings_eve_window_new_calendar_event_name.toUiText())
                } else if (children.isNotEmpty()) {
                    categorizeStack(children, joinedChatChannels)
                } else if (window.startsWith("bytes:") && window.substringAfter(":").toIntOrNull() != null) {
                    EveWindow("Dynamic", Res.string.character_settings_eve_window_dynamic_name.toUiText())
                } else {
                    EveWindow(window, isUnknown = true)
                }
            }
        }
    }

    private fun categorizeStack(children: List<String>, joinedChatChannels: Map<String, String>): EveWindow {
        return if (children.size == 1) {
            invoke(children.single(), emptyList(), joinedChatChannels)
        } else {
            val categorizedChildren = children
                .map { invoke(it, emptyList(), joinedChatChannels) }
                .filter { it.isPersistent }
            val isUnknown = categorizedChildren.any { it.isUnknown }
            val icon = categorizedChildren.firstNotNullOfOrNull { it.icon }
            val groupedChildren = categorizedChildren.groupBy { it.name }
            val displayNames = groupedChildren.values.map { windows ->
                val name = windows.first().displayName
                if (windows.size > 1) {
                    Res.string.character_settings_eve_window_stack_count_name.toUiText(windows.size.toString().toUiTextArgument(), name.toUiTextArgument())
                } else {
                    name
                }
            }
            val names = groupedChildren
                .map {
                    if (it.value.size > 1) {
                        "${it.value.size}x ${it.key}"
                    } else {
                        it.key
                    }
                }
            if (names.isNotEmpty()) {
                EveWindow(
                    name = names.joinToString("\n"),
                    displayName = displayNames.reduce { first, second -> Res.string.character_settings_eve_window_stack_lines.toUiText(first.toUiTextArgument(), second.toUiTextArgument()) },
                    isUnknown = isUnknown,
                    isPersistent = true,
                    isOpenStateControlled = categorizedChildren.all { it.isOpenStateControlled },
                    isCloseable = categorizedChildren.all { it.isCloseable },
                    isMinimizedStatePersistent = categorizedChildren.all { it.isMinimizedStatePersistent },
                    icon = icon,
                )
            } else {
                EveWindow("Stack", Res.string.character_settings_eve_window_stack_name.toUiText(), isUnknown = isUnknown)
            }
        }
    }

    private fun String.isCorporationHangarDivision(prefix: String): Boolean {
        return startsWith(prefix) && removePrefix(prefix).toIntOrNull() in corporationHangarDivisions
    }

    private fun Int.getCorporationHangarText(): UiText = if (this == 14) Res.string.character_settings_eve_window_corporation_goal_deliveries_name.toUiText() else Res.string.character_settings_eve_window_corporation_hangar_division_name.toUiText((this + 1).toString().toUiTextArgument())

    private val Int.corporationHangarName: String
        get() = if (this == 14) "Corporation Goal Deliveries" else "Corporation Hangar ${this + 1}"

    private companion object {
        val corporationHangarDivisions = (0..6) + 14
    }
}
