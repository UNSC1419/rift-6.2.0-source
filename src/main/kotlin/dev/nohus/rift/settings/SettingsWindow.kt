package dev.nohus.rift.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.onClick
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberWindowState
import dev.nohus.rift.BuildConfig
import dev.nohus.rift.characters.repositories.LocalCharactersRepository
import dev.nohus.rift.clipboard.Clipboard
import dev.nohus.rift.compose.AsyncTypeIcon
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.FlagIcon
import dev.nohus.rift.compose.LinkText
import dev.nohus.rift.compose.MulticolorIconType
import dev.nohus.rift.compose.PointerInteractionStateHolder
import dev.nohus.rift.compose.RequirementIcon
import dev.nohus.rift.compose.RiftAutocompleteTextField
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftCheckbox
import dev.nohus.rift.compose.RiftCheckboxWithLabel
import dev.nohus.rift.compose.RiftDialog
import dev.nohus.rift.compose.RiftDropdown
import dev.nohus.rift.compose.RiftDropdownWithLabel
import dev.nohus.rift.compose.RiftFileChooserButton
import dev.nohus.rift.compose.RiftImageButton
import dev.nohus.rift.compose.RiftMessageDialog
import dev.nohus.rift.compose.RiftMulticolorIcon
import dev.nohus.rift.compose.RiftPill
import dev.nohus.rift.compose.RiftRadioButtonWithLabel
import dev.nohus.rift.compose.RiftSliderWithLabel
import dev.nohus.rift.compose.RiftSolarSystemChip
import dev.nohus.rift.compose.RiftTabBar
import dev.nohus.rift.compose.RiftTextField
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.ScrollbarLazyColumn
import dev.nohus.rift.compose.SectionTitle
import dev.nohus.rift.compose.Tab
import dev.nohus.rift.compose.hoverBackground
import dev.nohus.rift.compose.modifyIf
import dev.nohus.rift.compose.pointerInteraction
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.configurationpack.ConfigurationPackRepository
import dev.nohus.rift.configurationpack.displayName
import dev.nohus.rift.di.koin
import dev.nohus.rift.dynamicportraits.DynamicCharacterPortraitParallax
import dev.nohus.rift.dynamicportraits.DynamicCharacterPortraitStandings
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.common_action_cancel
import dev.nohus.rift.generated.resources.common_action_confirm
import dev.nohus.rift.generated.resources.deleteicon
import dev.nohus.rift.generated.resources.settings_alerts_alert_volume_label
import dev.nohus.rift.generated.resources.settings_alerts_alerts_title
import dev.nohus.rift.generated.resources.settings_alerts_choose_notification_position_text
import dev.nohus.rift.generated.resources.settings_alerts_configure_text
import dev.nohus.rift.generated.resources.settings_alerts_edit_position_text
import dev.nohus.rift.generated.resources.settings_alerts_mobile_push_notifications_text
import dev.nohus.rift.generated.resources.settings_character_portraits_all_text
import dev.nohus.rift.generated.resources.settings_character_portraits_character_portraits_title
import dev.nohus.rift.generated.resources.settings_character_portraits_friendly_text
import dev.nohus.rift.generated.resources.settings_character_portraits_hostile_text
import dev.nohus.rift.generated.resources.settings_character_portraits_non_neutral_text
import dev.nohus.rift.generated.resources.settings_character_portraits_normal_text
import dev.nohus.rift.generated.resources.settings_character_portraits_parallax_effect_label
import dev.nohus.rift.generated.resources.settings_character_portraits_reduced_text
import dev.nohus.rift.generated.resources.settings_character_portraits_shown_characters_characters_outside_intel_tooltip
import dev.nohus.rift.generated.resources.settings_character_portraits_shown_characters_intel_contexts_tooltip
import dev.nohus.rift.generated.resources.settings_character_portraits_standings_background_label
import dev.nohus.rift.generated.resources.settings_character_portraits_standings_background_strength_label
import dev.nohus.rift.generated.resources.settings_character_portraits_turned_off_text
import dev.nohus.rift.generated.resources.settings_clipboard_clipboard_tester_text
import dev.nohus.rift.generated.resources.settings_clipboard_clipboard_title
import dev.nohus.rift.generated.resources.settings_clipboard_rift_can_import_some_data_text
import dev.nohus.rift.generated.resources.settings_clipboard_troubleshoot_import_issues_text
import dev.nohus.rift.generated.resources.settings_dev_development_title
import dev.nohus.rift.generated.resources.settings_dev_mocked_equinox_requests_label
import dev.nohus.rift.generated.resources.settings_display_eve_time_label
import dev.nohus.rift.generated.resources.settings_display_eve_time_tooltip
import dev.nohus.rift.generated.resources.settings_distance_intel_overlay_always_show_hostile_count_nearby_label
import dev.nohus.rift.generated.resources.settings_distance_intel_overlay_nearby_intel_max_jumps_label
import dev.nohus.rift.generated.resources.settings_distance_intel_overlay_nearby_intel_panel_title
import dev.nohus.rift.generated.resources.settings_enable_transparent_windows_label
import dev.nohus.rift.generated.resources.settings_enable_transparent_windows_tooltip
import dev.nohus.rift.generated.resources.settings_eve_installation_chat_logs_directory_types_description
import dev.nohus.rift.generated.resources.settings_eve_installation_detect_text
import dev.nohus.rift.generated.resources.settings_eve_installation_eve_installation_title
import dev.nohus.rift.generated.resources.settings_eve_installation_eve_launcher_logs_directory_text
import dev.nohus.rift.generated.resources.settings_eve_installation_eve_online_character_settings_directory_text
import dev.nohus.rift.generated.resources.settings_eve_installation_eve_online_logs_directory_text
import dev.nohus.rift.generated.resources.settings_eve_installation_eve_online_shared_cache_directory_text
import dev.nohus.rift.generated.resources.settings_eve_installation_eve_shared_cache_directory_types_description
import dev.nohus.rift.generated.resources.settings_eve_installation_game_logs_directory_types_description
import dev.nohus.rift.generated.resources.settings_eve_installation_invalid_launcher_logs_directory_text
import dev.nohus.rift.generated.resources.settings_eve_installation_invalid_logs_directory_text
import dev.nohus.rift.generated.resources.settings_eve_installation_invalid_settings_directory_text
import dev.nohus.rift.generated.resources.settings_eve_installation_invalid_shared_cache_directory_text
import dev.nohus.rift.generated.resources.settings_eve_installation_launcher_logs_directory_types_description
import dev.nohus.rift.generated.resources.settings_eve_installation_launcher_logs_directory_valid_text
import dev.nohus.rift.generated.resources.settings_eve_installation_logs_directory_valid_text
import dev.nohus.rift.generated.resources.settings_eve_installation_no_launcher_logs_directory_text
import dev.nohus.rift.generated.resources.settings_eve_installation_no_logs_directory_text
import dev.nohus.rift.generated.resources.settings_eve_installation_no_settings_directory_text
import dev.nohus.rift.generated.resources.settings_eve_installation_no_shared_cache_directory_text
import dev.nohus.rift.generated.resources.settings_eve_installation_settings_directory_valid_text
import dev.nohus.rift.generated.resources.settings_eve_installation_shared_cache_directory_valid_text
import dev.nohus.rift.generated.resources.settings_import_connections
import dev.nohus.rift.generated.resources.settings_import_systems
import dev.nohus.rift.generated.resources.settings_intel_channels_add_channel_text
import dev.nohus.rift.generated.resources.settings_intel_channels_all_regions_text
import dev.nohus.rift.generated.resources.settings_intel_channels_channel_name_placeholder
import dev.nohus.rift.generated.resources.settings_intel_channels_choose_region_channel_text
import dev.nohus.rift.generated.resources.settings_intel_channels_choose_region_text
import dev.nohus.rift.generated.resources.settings_intel_channels_enter_channel_name_text
import dev.nohus.rift.generated.resources.settings_intel_channels_intel_channels_title
import dev.nohus.rift.generated.resources.settings_intel_channels_intel_reports_will_read_from_text
import dev.nohus.rift.generated.resources.settings_intel_channels_no_intel_channels_configured_text
import dev.nohus.rift.generated.resources.settings_intel_timeout_don_t_expire_text
import dev.nohus.rift.generated.resources.settings_intel_timeout_expire_intel_after_label
import dev.nohus.rift.generated.resources.settings_intel_timeout_fifteen_minutes
import dev.nohus.rift.generated.resources.settings_intel_timeout_five_minutes
import dev.nohus.rift.generated.resources.settings_intel_timeout_intel_timeout_title
import dev.nohus.rift.generated.resources.settings_intel_timeout_one_hour
import dev.nohus.rift.generated.resources.settings_intel_timeout_one_minute
import dev.nohus.rift.generated.resources.settings_intel_timeout_ten_minutes
import dev.nohus.rift.generated.resources.settings_intel_timeout_thirty_minutes
import dev.nohus.rift.generated.resources.settings_intel_timeout_time_after_piece_intel_will_tooltip
import dev.nohus.rift.generated.resources.settings_intel_timeout_two_minutes
import dev.nohus.rift.generated.resources.settings_jump_bridge_available
import dev.nohus.rift.generated.resources.settings_jump_bridge_list
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_any_format_will_work_as_text
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_can_press_ctrl_ctrl_c_text
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_click_use_text
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_connection_opacity_label
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_copied_network_text
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_copy_text
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_forget_text
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_format_info_text
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_import_jump_bridges_by_copying_text
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_import_text
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_jump_bridge_connection_lines_will_tooltip
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_jump_bridge_network_title
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_more_text
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_no_jump_bridges_imported_text
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_search_automatically_text
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_search_text
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_show_network_map_label
import dev.nohus.rift.generated.resources.settings_jump_bridge_network_visibility_jump_bridge_connection_lines_tooltip
import dev.nohus.rift.generated.resources.settings_killmail_monitoring_killmail_monitoring_title
import dev.nohus.rift.generated.resources.settings_killmail_monitoring_monitor_zkillboard_com_label
import dev.nohus.rift.generated.resources.settings_killmail_monitoring_rift_watches_killmails_valuable_intel_text
import dev.nohus.rift.generated.resources.settings_killmail_monitoring_rift_will_subscribe_zkillboard_com_tooltip
import dev.nohus.rift.generated.resources.settings_killmail_posting_automatically_post_killmail_label
import dev.nohus.rift.generated.resources.settings_killmail_posting_do_nothing_label
import dev.nohus.rift.generated.resources.settings_killmail_posting_killmail_posting_title
import dev.nohus.rift.generated.resources.settings_killmail_posting_posting_delay_label
import dev.nohus.rift.generated.resources.settings_killmail_posting_show_dialog_posting_killmail_label
import dev.nohus.rift.generated.resources.settings_killmail_posting_when_copy_killmail_link_text
import dev.nohus.rift.generated.resources.settings_language_label
import dev.nohus.rift.generated.resources.settings_map_autopilot_autopilot_title
import dev.nohus.rift.generated.resources.settings_map_autopilot_eve_calculated_route_label
import dev.nohus.rift.generated.resources.settings_map_autopilot_prefer_insecure_stay_null_sec_label
import dev.nohus.rift.generated.resources.settings_map_autopilot_prefer_less_secure_stay_low_label
import dev.nohus.rift.generated.resources.settings_map_autopilot_prefer_safer_stay_high_sec_label
import dev.nohus.rift.generated.resources.settings_map_autopilot_prefer_shorter_ignore_security_status_label
import dev.nohus.rift.generated.resources.settings_map_autopilot_rift_calculated_route_label
import dev.nohus.rift.generated.resources.settings_map_autopilot_route_as_set_by_eve_tooltip
import dev.nohus.rift.generated.resources.settings_map_autopilot_route_as_shown_rift_map_tooltip
import dev.nohus.rift.generated.resources.settings_map_autopilot_security_preference_text
import dev.nohus.rift.generated.resources.settings_map_autopilot_when_setting_autopilot_destination_use_text
import dev.nohus.rift.generated.resources.settings_map_intel_popups_automatically_show_popups_label
import dev.nohus.rift.generated.resources.settings_map_intel_popups_don_t_show_text
import dev.nohus.rift.generated.resources.settings_map_intel_popups_how_long_will_intel_popups_tooltip
import dev.nohus.rift.generated.resources.settings_map_intel_popups_intel_popups_title
import dev.nohus.rift.generated.resources.settings_map_intel_popups_no_limit_text
import dev.nohus.rift.generated.resources.settings_map_intel_popups_ten_seconds
import dev.nohus.rift.generated.resources.settings_map_intel_popups_thirty_seconds
import dev.nohus.rift.generated.resources.settings_map_user_interface_always_show_system_labels_label
import dev.nohus.rift.generated.resources.settings_map_user_interface_compact_mode_label
import dev.nohus.rift.generated.resources.settings_map_user_interface_invert_scroll_wheel_zoom_label
import dev.nohus.rift.generated.resources.settings_map_user_interface_map_markers_text
import dev.nohus.rift.generated.resources.settings_map_user_interface_map_user_interface_title
import dev.nohus.rift.generated.resources.settings_map_user_interface_move_map_follow_character_label
import dev.nohus.rift.generated.resources.settings_map_user_interface_prefer_showing_systems_region_maps_label
import dev.nohus.rift.generated.resources.settings_map_user_interface_switch_maps_follow_character_label
import dev.nohus.rift.generated.resources.settings_map_user_interface_system_labels_won_t_hide_tooltip
import dev.nohus.rift.generated.resources.settings_map_user_interface_view_edit_map_markers_text
import dev.nohus.rift.generated.resources.settings_map_user_interface_when_clicking_system_somewhere_rift_tooltip
import dev.nohus.rift.generated.resources.settings_map_user_interface_when_jump_another_system_not_tooltip
import dev.nohus.rift.generated.resources.settings_map_user_interface_when_jump_another_system_visible_tooltip
import dev.nohus.rift.generated.resources.settings_map_user_interface_zoom_direction_will_reversed_tooltip
import dev.nohus.rift.generated.resources.settings_map_zoom_tip
import dev.nohus.rift.generated.resources.settings_network_loaded
import dev.nohus.rift.generated.resources.settings_other_settings_configuration_pack_label
import dev.nohus.rift.generated.resources.settings_other_settings_default_text
import dev.nohus.rift.generated.resources.settings_other_settings_did_know_that_aura_wizard_tooltip
import dev.nohus.rift.generated.resources.settings_other_settings_enables_settings_specific_player_group_tooltip
import dev.nohus.rift.generated.resources.settings_other_settings_other_settings_title
import dev.nohus.rift.generated.resources.settings_other_settings_show_setup_wizard_next_start_label
import dev.nohus.rift.generated.resources.settings_overlay_detection_failed
import dev.nohus.rift.generated.resources.settings_overlay_general_drag_handle_shown_eve_window_text
import dev.nohus.rift.generated.resources.settings_overlay_general_finish_moving_text
import dev.nohus.rift.generated.resources.settings_overlay_general_move_overlay_text
import dev.nohus.rift.generated.resources.settings_overlay_general_no_eve_window_currently_showing_text
import dev.nohus.rift.generated.resources.settings_overlay_general_overlay_already_at_its_default_text
import dev.nohus.rift.generated.resources.settings_overlay_general_reset_position_text
import dev.nohus.rift.generated.resources.settings_overlay_info_alliance_or_faction_holding_sovereignty
import dev.nohus.rift.generated.resources.settings_overlay_info_ansiblex_cost_zone_based_distance
import dev.nohus.rift.generated.resources.settings_overlay_info_ansiblex_zone
import dev.nohus.rift.generated.resources.settings_overlay_info_assets
import dev.nohus.rift.generated.resources.settings_overlay_info_asteroid_belts
import dev.nohus.rift.generated.resources.settings_overlay_info_clones
import dev.nohus.rift.generated.resources.settings_overlay_info_colonies
import dev.nohus.rift.generated.resources.settings_overlay_info_constellation
import dev.nohus.rift.generated.resources.settings_overlay_info_constellation_name
import dev.nohus.rift.generated.resources.settings_overlay_info_faction_rats_system
import dev.nohus.rift.generated.resources.settings_overlay_info_ice_fields
import dev.nohus.rift.generated.resources.settings_overlay_info_indicator_asteroid_belts
import dev.nohus.rift.generated.resources.settings_overlay_info_indicator_ice_fields
import dev.nohus.rift.generated.resources.settings_overlay_info_indicator_jove_observatories
import dev.nohus.rift.generated.resources.settings_overlay_info_indicator_jump_clones
import dev.nohus.rift.generated.resources.settings_overlay_info_indicator_pi_colonies
import dev.nohus.rift.generated.resources.settings_overlay_info_indicator_planets
import dev.nohus.rift.generated.resources.settings_overlay_info_indicators_installed_sovereignty_upgrades
import dev.nohus.rift.generated.resources.settings_overlay_info_indicators_raidable_skyhooks
import dev.nohus.rift.generated.resources.settings_overlay_info_jove_observatories
import dev.nohus.rift.generated.resources.settings_overlay_info_jump_distance_system
import dev.nohus.rift.generated.resources.settings_overlay_info_jump_range
import dev.nohus.rift.generated.resources.settings_overlay_info_jumps
import dev.nohus.rift.generated.resources.settings_overlay_info_kills
import dev.nohus.rift.generated.resources.settings_overlay_info_markers_added_system_map
import dev.nohus.rift.generated.resources.settings_overlay_info_npc_kills
import dev.nohus.rift.generated.resources.settings_overlay_info_number_jumps_last_hour
import dev.nohus.rift.generated.resources.settings_overlay_info_number_npcs_killed_last_hour
import dev.nohus.rift.generated.resources.settings_overlay_info_number_owned_assets_located_here
import dev.nohus.rift.generated.resources.settings_overlay_info_number_ship_pod_kills_last
import dev.nohus.rift.generated.resources.settings_overlay_info_number_stations
import dev.nohus.rift.generated.resources.settings_overlay_info_planets
import dev.nohus.rift.generated.resources.settings_overlay_info_raidable_skyhooks
import dev.nohus.rift.generated.resources.settings_overlay_info_rats
import dev.nohus.rift.generated.resources.settings_overlay_info_region
import dev.nohus.rift.generated.resources.settings_overlay_info_region_name
import dev.nohus.rift.generated.resources.settings_overlay_info_sovereignty_upgrades
import dev.nohus.rift.generated.resources.settings_overlay_info_space_type
import dev.nohus.rift.generated.resources.settings_overlay_info_stations
import dev.nohus.rift.generated.resources.settings_overlay_info_system
import dev.nohus.rift.generated.resources.settings_overlay_info_system_name_security_status
import dev.nohus.rift.generated.resources.settings_overlay_info_type_space_standings_towards_sovereignty
import dev.nohus.rift.generated.resources.settings_overlay_status_detecting_eve_windows_text
import dev.nohus.rift.generated.resources.settings_overlay_status_eve_window_detection_needs_accessibility_text
import dev.nohus.rift.generated.resources.settings_overlay_status_eve_windows_title
import dev.nohus.rift.generated.resources.settings_overlay_status_log_game_see_overlays_text
import dev.nohus.rift.generated.resources.settings_overlay_status_no_eve_windows_detected_text
import dev.nohus.rift.generated.resources.settings_overlay_status_open_accessibility_settings_text
import dev.nohus.rift.generated.resources.settings_overlay_unsupported
import dev.nohus.rift.generated.resources.settings_remember_open_windows_label
import dev.nohus.rift.generated.resources.settings_remember_open_windows_tooltip
import dev.nohus.rift.generated.resources.settings_remember_window_placement_label
import dev.nohus.rift.generated.resources.settings_remember_window_placement_tooltip
import dev.nohus.rift.generated.resources.settings_search_connections
import dev.nohus.rift.generated.resources.settings_search_network
import dev.nohus.rift.generated.resources.settings_search_progress
import dev.nohus.rift.generated.resources.settings_settings_window_content_ansiblex_capital_label
import dev.nohus.rift.generated.resources.settings_settings_window_content_jump_range_from_label
import dev.nohus.rift.generated.resources.settings_settings_window_content_jump_range_label
import dev.nohus.rift.generated.resources.settings_settings_window_content_show_icon_only_sovereignty_label
import dev.nohus.rift.generated.resources.settings_settings_window_content_show_icon_only_space_type_label
import dev.nohus.rift.generated.resources.settings_settings_window_feature_not_unique_rift_no_text
import dev.nohus.rift.generated.resources.settings_settings_window_jump_bridge_search_title
import dev.nohus.rift.generated.resources.settings_settings_window_use_at_own_risk_text
import dev.nohus.rift.generated.resources.settings_show_isk_cents_label
import dev.nohus.rift.generated.resources.settings_show_isk_cents_tooltip
import dev.nohus.rift.generated.resources.settings_show_system_distance_label
import dev.nohus.rift.generated.resources.settings_show_system_distance_tooltip
import dev.nohus.rift.generated.resources.settings_smart_always_above_label
import dev.nohus.rift.generated.resources.settings_smart_always_above_tooltip
import dev.nohus.rift.generated.resources.settings_sovereignty_upgrades_alliance_sovereignty_upgrades_list_text
import dev.nohus.rift.generated.resources.settings_sovereignty_upgrades_automatically_import_sovereignty_upgrades_when_tooltip
import dev.nohus.rift.generated.resources.settings_sovereignty_upgrades_can_press_ctrl_ctrl_c_text
import dev.nohus.rift.generated.resources.settings_sovereignty_upgrades_copied_upgrades_text
import dev.nohus.rift.generated.resources.settings_sovereignty_upgrades_import_offline_upgrades_from_hacked_label
import dev.nohus.rift.generated.resources.settings_sovereignty_upgrades_import_upgrades_by_copying_list_text
import dev.nohus.rift.generated.resources.settings_sovereignty_upgrades_import_upgrades_from_hacked_sovereignty_label
import dev.nohus.rift.generated.resources.settings_sovereignty_upgrades_need_system_name_upgrade_names_text
import dev.nohus.rift.generated.resources.settings_sovereignty_upgrades_no_sovereignty_upgrades_imported_text
import dev.nohus.rift.generated.resources.settings_sovereignty_upgrades_sovereignty_upgrades_title
import dev.nohus.rift.generated.resources.settings_sovereignty_upgrades_when_importing_sovereignty_upgrades_from_tooltip
import dev.nohus.rift.generated.resources.settings_storage_all_data_text
import dev.nohus.rift.generated.resources.settings_storage_bytes
import dev.nohus.rift.generated.resources.settings_storage_characters
import dev.nohus.rift.generated.resources.settings_storage_esi_cache_text
import dev.nohus.rift.generated.resources.settings_storage_gigabytes
import dev.nohus.rift.generated.resources.settings_storage_http_cache_text
import dev.nohus.rift.generated.resources.settings_storage_killmail_cache_text
import dev.nohus.rift.generated.resources.settings_storage_kilobytes
import dev.nohus.rift.generated.resources.settings_storage_megabytes
import dev.nohus.rift.generated.resources.settings_storage_open_app_cache_text
import dev.nohus.rift.generated.resources.settings_storage_open_app_data_text
import dev.nohus.rift.generated.resources.settings_storage_other_cache_text
import dev.nohus.rift.generated.resources.settings_storage_portraits_cache_text
import dev.nohus.rift.generated.resources.settings_storage_rift_cache_directory_text
import dev.nohus.rift.generated.resources.settings_storage_rift_data_directory_text
import dev.nohus.rift.generated.resources.settings_storage_storage_title
import dev.nohus.rift.generated.resources.settings_storage_used
import dev.nohus.rift.generated.resources.settings_system_info_overlay_system_info_overlay_shows_information_text
import dev.nohus.rift.generated.resources.settings_system_info_overlay_system_info_panel_title
import dev.nohus.rift.generated.resources.settings_toolbar_row_general_settings_title
import dev.nohus.rift.generated.resources.settings_toolbar_row_intel_alerts_title
import dev.nohus.rift.generated.resources.settings_toolbar_row_map_title
import dev.nohus.rift.generated.resources.settings_toolbar_row_misc_title
import dev.nohus.rift.generated.resources.settings_toolbar_row_overlay_title
import dev.nohus.rift.generated.resources.settings_toolbar_row_sovereignty_title
import dev.nohus.rift.generated.resources.settings_ui_scale_label
import dev.nohus.rift.generated.resources.settings_ui_sound_volume_label
import dev.nohus.rift.generated.resources.settings_upgrades_loaded
import dev.nohus.rift.generated.resources.settings_use_jump_bridges_label
import dev.nohus.rift.generated.resources.settings_use_jump_bridges_tooltip
import dev.nohus.rift.generated.resources.settings_used_space_calculating_text
import dev.nohus.rift.generated.resources.settings_user_interface_title
import dev.nohus.rift.generated.resources.settings_translation_feedback_link
import dev.nohus.rift.generated.resources.settings_translation_feedback_text
import dev.nohus.rift.generated.resources.settings_window_title
import dev.nohus.rift.generated.resources.settings_window_transparency_custom
import dev.nohus.rift.generated.resources.settings_window_transparency_high
import dev.nohus.rift.generated.resources.settings_window_transparency_label
import dev.nohus.rift.generated.resources.settings_window_transparency_low
import dev.nohus.rift.generated.resources.settings_window_transparency_maximal
import dev.nohus.rift.generated.resources.settings_window_transparency_medium
import dev.nohus.rift.generated.resources.settings_window_transparency_minimal
import dev.nohus.rift.generated.resources.window_settings
import dev.nohus.rift.generated.resources.window_warning
import dev.nohus.rift.localization.SupportedLanguage
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.localization.toUiTextArgument
import dev.nohus.rift.location.CharacterLocationRepository
import dev.nohus.rift.map.AnsiblexCapitalSystemField
import dev.nohus.rift.map.JumpRangeDistanceField
import dev.nohus.rift.map.JumpRangeSourceField
import dev.nohus.rift.map.MapAnsiblexZonesController
import dev.nohus.rift.notifications.NotificationEditWindow
import dev.nohus.rift.overlay.OverlayPlacementController
import dev.nohus.rift.overlay.OverlayRuntimeStatusRepository
import dev.nohus.rift.overlay.OverlayStatus
import dev.nohus.rift.overlay.getOverlayStatus
import dev.nohus.rift.postkillmail.KillmailPostingDelay
import dev.nohus.rift.repositories.RoutePreference
import dev.nohus.rift.repositories.SolarSystemChipState
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.settings.SettingsViewModel.JumpBridgeCopyState
import dev.nohus.rift.settings.SettingsViewModel.JumpBridgeSearchState
import dev.nohus.rift.settings.SettingsViewModel.SettingsTab
import dev.nohus.rift.settings.SettingsViewModel.SovereigntyUpgradesCopyState
import dev.nohus.rift.settings.SettingsViewModel.UiState
import dev.nohus.rift.settings.persistence.CharacterPortraitsParallaxStrength
import dev.nohus.rift.settings.persistence.CharacterPortraitsStandingsTargets
import dev.nohus.rift.settings.persistence.ConfigurationPack
import dev.nohus.rift.settings.persistence.KillmailPosting
import dev.nohus.rift.settings.persistence.OverlayInfoType
import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.standings.Standing
import dev.nohus.rift.utils.Links
import dev.nohus.rift.utils.OperatingSystem
import dev.nohus.rift.utils.OperatingSystem.MacOs
import dev.nohus.rift.utils.evewindows.EveWindowsRepository
import dev.nohus.rift.utils.formatDate
import dev.nohus.rift.utils.openBrowser
import dev.nohus.rift.utils.roundSecurity
import dev.nohus.rift.utils.sound.rememberUiSoundPlayer
import dev.nohus.rift.utils.toURIOrNull
import dev.nohus.rift.utils.withColor
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale
import javax.swing.JFileChooser
import kotlin.io.path.absolutePathString
import org.jetbrains.compose.resources.stringResource

private val logger = KotlinLogging.logger {}

@Composable
fun SettingsWindow(
    inputModel: SettingsInputModel,
    windowState: RiftWindowState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: SettingsViewModel = viewModel(inputModel)
    val state by viewModel.state.collectAsState()
    val placementController: OverlayPlacementController = remember { koin.get() }
    if (state.selectedTab == SettingsTab.Overlay) {
        DisposableEffect(placementController) {
            onDispose { placementController.setPositioning(false) }
        }
    }

    RiftWindow(
        title = stringResource(Res.string.settings_window_title),
        icon = Res.drawable.window_settings,
        state = windowState,
        onCloseClick = onCloseRequest,
        titleBarContent = { height ->
            ToolbarRow(
                selectedTab = state.selectedTab,
                fixedHeight = height,
                onTabSelected = viewModel::onTabSelected,
            )
        },
        withContentPadding = false,
        isResizable = false,
    ) {
        SettingsWindowContent(
            inputModel = inputModel,
            state = state,
            viewModel = viewModel,
        )

        state.dialogMessage?.let {
            RiftMessageDialog(
                dialog = it,
                parentWindowState = windowState,
                onDismiss = viewModel::onCloseDialogMessage,
            )
        }

        if (state.isJumpBridgeSearchDialogShown) {
            RiftDialog(
                title = stringResource(Res.string.settings_settings_window_jump_bridge_search_title),
                icon = Res.drawable.window_warning,
                parentState = windowState,
                state = rememberWindowState(width = 350.dp, height = Dp.Unspecified),
                onCloseClick = viewModel::onJumpBridgeDialogDismissed,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                ) {
                    Text(
                        text = stringResource(Res.string.settings_settings_window_feature_not_unique_rift_no_text),
                        style = RiftTheme.typography.bodyPrimary,
                    )
                    Text(
                        text = stringResource(Res.string.settings_settings_window_use_at_own_risk_text),
                        textAlign = TextAlign.Center,
                        style = RiftTheme.typography.headerPrimary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    ) {
                        RiftButton(
                            text = stringResource(Res.string.common_action_cancel),
                            cornerCut = ButtonCornerCut.BottomLeft,
                            type = ButtonType.Secondary,
                            onClick = viewModel::onJumpBridgeDialogDismissed,
                            modifier = Modifier.weight(1f),
                        )
                        RiftButton(
                            text = stringResource(Res.string.common_action_confirm),
                            type = ButtonType.Secondary,
                            onClick = viewModel::onJumpBridgeSearchDialogConfirmClick,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }

    if (state.isEditNotificationWindowOpen) {
        NotificationEditWindow(
            position = state.notificationEditPlacement,
            onCloseRequest = viewModel::onEditNotificationDone,
        )
    }
}

@Composable
private fun SettingsWindowContent(
    inputModel: SettingsInputModel,
    state: UiState,
    viewModel: SettingsViewModel,
) {
    Column {
        val offset = LocalDensity.current.run { 1.dp.toPx() }
        Box(
            modifier = Modifier
                .graphicsLayer(translationY = -offset)
                .fillMaxWidth()
                .height(1.dp)
                .background(RiftTheme.colors.borderGreyLight),
        )

        Layout(
            content = {
                // General Settings
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    modifier = Modifier
                        .padding(Spacing.medium)
                        .height(IntrinsicSize.Max),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                        modifier = Modifier.weight(1f),
                    ) {
                        SectionContainer(inputModel) {
                            UserInterfaceSection(state, viewModel)
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                        modifier = Modifier.weight(1f),
                    ) {
                        SectionContainer(inputModel, SettingsInputModel.EveInstallation) {
                            EveInstallationSection(state, viewModel)
                        }
                        SectionContainer(inputModel) {
                            CharacterPortraitsSection(state, viewModel)
                        }
                    }
                }

                // Intel & Alerts
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    modifier = Modifier
                        .padding(Spacing.medium)
                        .height(IntrinsicSize.Max),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                        modifier = Modifier.weight(1f),
                    ) {
                        SectionContainer(inputModel, SettingsInputModel.IntelChannels) {
                            IntelChannelsSection(state, viewModel)
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                        modifier = Modifier.weight(1f),
                    ) {
                        SectionContainer(inputModel) {
                            IntelTimeoutSection(state, viewModel)
                        }
                        SectionContainer(inputModel) {
                            AlertsSection(state, viewModel)
                        }
                        SectionContainer(inputModel) {
                            KillmailMonitoringSection(state, viewModel)
                        }
                    }
                }

                // Map & Autopilot
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    modifier = Modifier
                        .padding(Spacing.medium)
                        .height(IntrinsicSize.Max),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                        modifier = Modifier.weight(1f),
                    ) {
                        SectionContainer(inputModel) {
                            MapUserInterfaceSection(state, viewModel)
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                        modifier = Modifier.weight(1f),
                    ) {
                        SectionContainer(inputModel) {
                            MapAutopilotSection(state, viewModel)
                        }
                        SectionContainer(inputModel) {
                            MapIntelPopupsSection(state, viewModel)
                        }
                    }
                }

                // Sovereignty
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    modifier = Modifier
                        .padding(Spacing.medium)
                        .height(IntrinsicSize.Max),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                        modifier = Modifier.weight(1f),
                    ) {
                        SectionContainer(inputModel) {
                            JumpBridgeNetworkSection(state, viewModel)
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                        modifier = Modifier.weight(1f),
                    ) {
                        SectionContainer(inputModel) {
                            SovereigntyUpgradesSection(state, viewModel)
                        }
                    }
                }

                // Overlay
                val overlayWindowStatuses = getOverlayWindowStatuses(state.overlayEnabledCharacterIds)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    modifier = Modifier
                        .padding(Spacing.medium),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                        modifier = Modifier.weight(1f),
                    ) {
                        SectionContainer(inputModel) {
                            OverlayGeneralSection(overlayWindowStatuses)
                        }
                        SectionContainer(inputModel) {
                            OverlayStatusSection(overlayWindowStatuses, state, viewModel)
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.small),
                        modifier = Modifier.weight(1f),
                    ) {
                        SectionContainer(inputModel) {
                            SystemInfoOverlaySection(state, viewModel)
                        }
                        SectionContainer(inputModel, isEnabled = OverlayInfoType.SpaceType in state.overlayInfoTypes) {
                            RiftCheckboxWithLabel(
                                label = stringResource(Res.string.settings_settings_window_content_show_icon_only_space_type_label),
                                isChecked = state.isOverlaySpaceTypeIconOnly,
                                onCheckedChange = viewModel::onOverlaySpaceTypeIconOnlyChanged,
                            )
                        }
                        SectionContainer(inputModel, isEnabled = OverlayInfoType.Sovereignty in state.overlayInfoTypes) {
                            RiftCheckboxWithLabel(
                                label = stringResource(Res.string.settings_settings_window_content_show_icon_only_sovereignty_label),
                                isChecked = state.isOverlaySovereigntyIconOnly,
                                onCheckedChange = viewModel::onOverlaySovereigntyIconOnlyChanged,
                            )
                        }
                        SectionContainer(inputModel, isEnabled = OverlayInfoType.AnsiblexZone in state.overlayInfoTypes) {
                            val ansiblexZonesController: MapAnsiblexZonesController = remember { koin.get() }
                            val ansiblexZonesState by ansiblexZonesController.state.collectAsState()
                            AnsiblexCapitalSystemField(
                                state = ansiblexZonesState,
                                label = stringResource(Res.string.settings_settings_window_content_ansiblex_capital_label),
                                onCapitalSystemUpdate = ansiblexZonesController::onCapitalSystemUpdate,
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                            SectionContainer(inputModel, isEnabled = OverlayInfoType.JumpRange in state.overlayInfoTypes) {
                                JumpRangeSourceField(
                                    state = state.mapJumpRangeState,
                                    label = stringResource(Res.string.settings_settings_window_content_jump_range_from_label),
                                    onTargetUpdate = viewModel::onOverlayJumpRangeTargetChanged,
                                )
                            }
                            SectionContainer(inputModel, isEnabled = OverlayInfoType.JumpRange in state.overlayInfoTypes) {
                                JumpRangeDistanceField(
                                    state = state.mapJumpRangeState,
                                    label = stringResource(Res.string.settings_settings_window_content_jump_range_label),
                                    onRangeUpdate = viewModel::onOverlayJumpRangeDistanceChanged,
                                )
                            }
                        }
                        SectionContainer(inputModel) {
                            DistanceIntelOverlaySection(state, viewModel)
                        }
                    }
                }

                // Misc
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    modifier = Modifier
                        .padding(Spacing.medium)
                        .height(IntrinsicSize.Max),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                        modifier = Modifier.weight(1f),
                    ) {
                        SectionContainer(inputModel) {
                            OtherSettingsSection(state, viewModel)
                        }
                        SectionContainer(inputModel) {
                            StorageSection(state, viewModel)
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                        modifier = Modifier.weight(1f),
                    ) {
                        SectionContainer(inputModel) {
                            ClipboardSection(state, viewModel)
                        }
                        SectionContainer(inputModel) {
                            KillmailPostingSection(state, viewModel)
                        }
                        if (BuildConfig.isDevEnvironment) {
                            SectionContainer(inputModel) {
                                DevSection(state, viewModel)
                            }
                        }
                    }
                }
            },
        ) { measurables, constraints ->
            val placeables = measurables.map { measurable ->
                measurable.measure(constraints)
            }
            val height = placeables.maxOf { it.height }
            layout(constraints.maxWidth, height) {
                placeables[state.selectedTab.id].place(0, 0)
            }
        }
    }
}

@Composable
private fun ToolbarRow(
    selectedTab: SettingsTab,
    fixedHeight: Dp,
    onTabSelected: (SettingsTab) -> Unit,
) {
    val tabs = listOf(
        Tab(id = SettingsTab.General.id, title = stringResource(Res.string.settings_toolbar_row_general_settings_title), isCloseable = false, payload = SettingsTab.General),
        Tab(id = SettingsTab.Intel.id, title = stringResource(Res.string.settings_toolbar_row_intel_alerts_title), isCloseable = false, payload = SettingsTab.Intel),
        Tab(id = SettingsTab.Map.id, title = stringResource(Res.string.settings_toolbar_row_map_title), isCloseable = false, payload = SettingsTab.Map),
        Tab(id = SettingsTab.Sovereignty.id, title = stringResource(Res.string.settings_toolbar_row_sovereignty_title), isCloseable = false, payload = SettingsTab.Sovereignty),
        Tab(id = SettingsTab.Overlay.id, title = stringResource(Res.string.settings_toolbar_row_overlay_title), isCloseable = false, payload = SettingsTab.Overlay),
        Tab(id = SettingsTab.Misc.id, title = stringResource(Res.string.settings_toolbar_row_misc_title), isCloseable = false, payload = SettingsTab.Misc),
    )
    RiftTabBar(
        tabs = tabs,
        selectedTab = selectedTab.id,
        onTabSelected = { tab ->
            onTabSelected(tabs.first { it.id == tab }.payload as SettingsTab)
        },
        onTabClosed = {},
        withUnderline = false,
        withWideTabs = true,
        fixedHeight = fixedHeight,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SectionContainer(
    inputModel: SettingsInputModel,
    enabledInputModel: SettingsInputModel? = null,
    isEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val canInteract = isEnabled && (inputModel == SettingsInputModel.Normal || inputModel == enabledInputModel)
    Box(
        modifier = Modifier
            .modifyIf(!canInteract) {
                alpha(0.3f)
            },
    ) {
        Column {
            content()
        }
        if (!canInteract) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .onClick {},
            ) {}
        }
    }
}

@Composable
private fun UserInterfaceSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_user_interface_title), Modifier.padding(bottom = Spacing.medium))
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = Spacing.medium),
    ) {
        RiftDropdownWithLabel(
            label = stringResource(Res.string.settings_language_label),
            items = SupportedLanguage.entries,
            selectedItem = state.language,
            onItemSelected = viewModel::onLanguageChanged,
            getItemName = { it.nativeName },
        )
        val discordText = stringResource(Res.string.settings_translation_feedback_link)
        val feedbackText = stringResource(Res.string.settings_translation_feedback_text, discordText)
        Text(
            text = buildAnnotatedString {
                append(feedbackText)
                val linkStart = feedbackText.indexOf(discordText)
                if (linkStart >= 0) {
                    addLink(
                        LinkAnnotation.Url(
                            url = Links.discordInvite,
                            styles = TextLinkStyles(
                                style = SpanStyle(fontWeight = FontWeight.Bold, color = RiftTheme.colors.textLink),
                                hoveredStyle = SpanStyle(textDecoration = TextDecoration.Underline),
                            ),
                            linkInteractionListener = { Links.discordInvite.toURIOrNull()?.openBrowser() },
                        ),
                        start = linkStart,
                        end = linkStart + discordText.length,
                    )
                }
            },
            style = RiftTheme.typography.detailSecondary,
            modifier = Modifier.weight(1f),
        )
    }
    RiftCheckboxWithLabel(
        label = stringResource(Res.string.settings_remember_open_windows_label),
        tooltip = stringResource(Res.string.settings_remember_open_windows_tooltip),
        isChecked = state.isRememberOpenWindows,
        onCheckedChange = viewModel::onRememberOpenWindowsChanged,
        modifier = Modifier.padding(bottom = Spacing.small),
    )
    RiftCheckboxWithLabel(
        label = stringResource(Res.string.settings_remember_window_placement_label),
        tooltip = stringResource(Res.string.settings_remember_window_placement_tooltip),
        isChecked = state.isRememberWindowPlacement,
        onCheckedChange = viewModel::onRememberWindowPlacementChanged,
        modifier = Modifier.padding(bottom = Spacing.small),
    )
    RiftCheckboxWithLabel(
        label = stringResource(Res.string.settings_display_eve_time_label),
        tooltip = stringResource(Res.string.settings_display_eve_time_tooltip),
        isChecked = state.isDisplayEveTime,
        onCheckedChange = viewModel::onIsDisplayEveTimeChanged,
        modifier = Modifier.padding(bottom = Spacing.small),
    )
    RiftCheckboxWithLabel(
        label = stringResource(Res.string.settings_show_isk_cents_label),
        tooltip = stringResource(Res.string.settings_show_isk_cents_tooltip),
        isChecked = state.isShowIskCents,
        onCheckedChange = viewModel::onIsShowIskCentsChanged,
        modifier = Modifier.padding(bottom = Spacing.small),
    )
    val operatingSystem: OperatingSystem = remember { koin.get() }
    if (operatingSystem != MacOs) {
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.settings_smart_always_above_label),
            tooltip = stringResource(Res.string.settings_smart_always_above_tooltip),
            isChecked = state.isSmartAlwaysAbove,
            onCheckedChange = viewModel::onIsSmartAlwaysAboveChanged,
            modifier = Modifier.padding(bottom = Spacing.small),
        )
    }
    RiftCheckboxWithLabel(
        label = stringResource(Res.string.settings_show_system_distance_label),
        tooltip = stringResource(Res.string.settings_show_system_distance_tooltip),
        isChecked = state.isShowingSystemDistance,
        onCheckedChange = viewModel::onIsShowingSystemDistanceChange,
        modifier = Modifier.padding(bottom = Spacing.small),
    )
    RiftCheckboxWithLabel(
        label = stringResource(Res.string.settings_use_jump_bridges_label),
        tooltip = stringResource(Res.string.settings_use_jump_bridges_tooltip),
        isChecked = state.isUsingJumpBridgesForDistance,
        onCheckedChange = viewModel::onIsUsingJumpBridgesForDistance,
        modifier = Modifier.padding(bottom = Spacing.small),
    )
    RiftCheckboxWithLabel(
        label = stringResource(Res.string.settings_enable_transparent_windows_label),
        tooltip = stringResource(Res.string.settings_enable_transparent_windows_tooltip),
        isChecked = state.isWindowTransparencyEnabled,
        onCheckedChange = viewModel::onIsWindowTransparencyChanged,
    )
    Spacer(Modifier.height(Spacing.small))
    val transparencyNames = mapOf(
        0f to stringResource(Res.string.settings_window_transparency_maximal),
        0.25f to stringResource(Res.string.settings_window_transparency_high),
        0.5f to stringResource(Res.string.settings_window_transparency_medium),
        0.75f to stringResource(Res.string.settings_window_transparency_low),
        1f to stringResource(Res.string.settings_window_transparency_minimal),
    )
    val customTransparencyName = stringResource(Res.string.settings_window_transparency_custom)
    RiftDropdownWithLabel(
        label = stringResource(Res.string.settings_window_transparency_label),
        items = listOf(0f, 0.25f, 0.5f, 0.75f, 1f),
        selectedItem = state.windowTransparencyModifier,
        onItemSelected = viewModel::onWindowTransparencyModifierChanged,
        getItemName = { transparencyNames[it] ?: customTransparencyName },
    )
    RiftDropdownWithLabel(
        label = stringResource(Res.string.settings_ui_scale_label),
        items = listOf(0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 2.0f),
        selectedItem = state.uiScale,
        onItemSelected = viewModel::onUiScaleChanged,
        getItemName = { formatPercentage((it * 100).toInt()) },
    )
    Spacer(Modifier.height(Spacing.small))
    RiftSliderWithLabel(
        label = stringResource(Res.string.settings_ui_sound_volume_label),
        width = 100.dp,
        range = 0..100,
        currentValue = state.uiSoundsVolume,
        onValueChange = viewModel::onUiSoundsVolumeChange,
        getValueName = { formatPercentage(it) },
    )
}

@Composable
private fun AlertsSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_alerts_alerts_title), Modifier.padding(bottom = Spacing.medium))
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.settings_alerts_choose_notification_position_text),
            style = RiftTheme.typography.bodyPrimary,
            modifier = Modifier.weight(1f),
        )
        RiftButton(
            text = stringResource(Res.string.settings_alerts_edit_position_text),
            onClick = viewModel::onEditNotificationClick,
        )
    }
    RiftSliderWithLabel(
        label = stringResource(Res.string.settings_alerts_alert_volume_label),
        width = 100.dp,
        range = 0..100,
        currentValue = state.soundsVolume,
        onValueChange = viewModel::onSoundsVolumeChange,
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.settings_alerts_mobile_push_notifications_text),
            style = RiftTheme.typography.bodyPrimary,
            modifier = Modifier.weight(1f),
        )
        RiftButton(
            text = stringResource(Res.string.settings_alerts_configure_text),
            onClick = viewModel::onConfigurePushoverClick,
        )
    }
}

@Composable
private fun KillmailMonitoringSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_killmail_monitoring_killmail_monitoring_title), Modifier.padding(bottom = Spacing.medium))
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
        Text(
            text = stringResource(Res.string.settings_killmail_monitoring_rift_watches_killmails_valuable_intel_text),
            style = RiftTheme.typography.bodySecondary,
        )
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.settings_killmail_monitoring_monitor_zkillboard_com_label),
            tooltip = stringResource(Res.string.settings_killmail_monitoring_rift_will_subscribe_zkillboard_com_tooltip),
            isChecked = state.isZkillboardMonitoringEnabled,
            onCheckedChange = viewModel::onIsZkillboardMonitoringChanged,
            modifier = Modifier.padding(bottom = Spacing.small),
        )
    }
}

@Composable
private fun KillmailPostingSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_killmail_posting_killmail_posting_title), Modifier.padding(bottom = Spacing.medium))
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
        Text(
            text = stringResource(Res.string.settings_killmail_posting_when_copy_killmail_link_text),
            style = RiftTheme.typography.bodySecondary,
        )
        RiftRadioButtonWithLabel(
            label = stringResource(Res.string.settings_killmail_posting_do_nothing_label),
            isChecked = state.killmailPosting == KillmailPosting.DoNothing,
            onChecked = { viewModel.onKillmailPostingChange(KillmailPosting.DoNothing) },
        )
        RiftRadioButtonWithLabel(
            label = stringResource(Res.string.settings_killmail_posting_show_dialog_posting_killmail_label),
            isChecked = state.killmailPosting == KillmailPosting.Dialog,
            onChecked = { viewModel.onKillmailPostingChange(KillmailPosting.Dialog) },
        )
        RiftRadioButtonWithLabel(
            label = stringResource(Res.string.settings_killmail_posting_automatically_post_killmail_label),
            isChecked = state.killmailPosting == KillmailPosting.Automatic,
            onChecked = { viewModel.onKillmailPostingChange(KillmailPosting.Automatic) },
        )
        val delayNames = KillmailPostingDelay.entries.associateWith { it.displayText.resolve() }
        RiftDropdownWithLabel(
            label = stringResource(Res.string.settings_killmail_posting_posting_delay_label),
            items = KillmailPostingDelay.entries,
            selectedItem = state.killmailPostingDelay,
            onItemSelected = viewModel::onKillmailPostingDelayChange,
            getItemName = { delayNames.getValue(it) },
            maxItems = KillmailPostingDelay.entries.size,
            modifier = Modifier.padding(top = Spacing.small),
        )
    }
}

@Composable
private fun OtherSettingsSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_other_settings_other_settings_title), Modifier.padding(bottom = Spacing.medium))
    val defaultPackName = stringResource(Res.string.settings_other_settings_default_text)
    RiftDropdownWithLabel(
        label = stringResource(Res.string.settings_other_settings_configuration_pack_label),
        items = listOf(null) + ConfigurationPack.entries,
        selectedItem = state.configurationPack,
        onItemSelected = viewModel::onConfigurationPackChange,
        getItemName = { it?.displayName ?: defaultPackName },
        tooltip = stringResource(Res.string.settings_other_settings_enables_settings_specific_player_group_tooltip),
        modifier = Modifier.padding(bottom = Spacing.small),
    )
    RiftCheckboxWithLabel(
        label = stringResource(Res.string.settings_other_settings_show_setup_wizard_next_start_label),
        tooltip = stringResource(Res.string.settings_other_settings_did_know_that_aura_wizard_tooltip),
        isChecked = state.isShowSetupWizardOnNextStartEnabled,
        onCheckedChange = viewModel::onShowSetupWizardOnNextStartChanged,
    )
}

@Composable
private fun StorageSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_storage_storage_title), Modifier.padding(bottom = Spacing.medium))
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.verySmall),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(Res.string.settings_storage_rift_data_directory_text),
                    style = RiftTheme.typography.bodyPrimary,
                )
                Text(
                    text = state.storageStats?.dataDirectory?.toString() ?: "…",
                    style = RiftTheme.typography.detailSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis,
                )
            }
            RiftButton(
                text = stringResource(Res.string.settings_storage_open_app_data_text),
                type = ButtonType.Primary,
                onClick = viewModel::onOpenAppData,
            )
        }
        UsedSpace(stringResource(Res.string.settings_storage_all_data_text), state.storageStats?.dataSize)

        Divider(color = RiftTheme.colors.divider, modifier = Modifier.padding(vertical = Spacing.small))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.verySmall),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(Res.string.settings_storage_rift_cache_directory_text),
                    style = RiftTheme.typography.bodyPrimary,
                )
                Text(
                    text = state.storageStats?.cacheDirectory?.toString() ?: "…",
                    style = RiftTheme.typography.detailSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis,
                )
            }
            RiftButton(
                text = stringResource(Res.string.settings_storage_open_app_cache_text),
                type = ButtonType.Primary,
                onClick = viewModel::onOpenAppCache,
            )
        }
        UsedSpace(stringResource(Res.string.settings_storage_esi_cache_text), state.storageStats?.esiCacheSize)
        UsedSpace(stringResource(Res.string.settings_storage_killmail_cache_text), state.storageStats?.zkillCacheSize)
        UsedSpace(stringResource(Res.string.settings_storage_http_cache_text), state.storageStats?.httpCacheSize)
        UsedSpace(
            stringResource(Res.string.settings_storage_portraits_cache_text), state.storageStats?.portraitsSize, state.storageStats?.portraitsCount?.let {
                Res.plurals.settings_storage_characters.toUiText(it, it.toUiTextArgument(useGrouping = true)).resolve()
            })
        UsedSpace(stringResource(Res.string.settings_storage_other_cache_text), state.storageStats?.otherCacheSize)
    }
}

@Composable
private fun UsedSpace(text: String, bytes: Long?, secondaryText: String? = null) {
    Row {
        Text(
            text = text,
            style = RiftTheme.typography.bodyPrimary,
            modifier = Modifier.weight(1f),
        )
        if (secondaryText != null) {
            Text(
                text = secondaryText,
                style = RiftTheme.typography.bodyPrimary,
                modifier = Modifier.padding(end = Spacing.small),
            )
        }
        if (bytes != null) {
            Text(
                text = stringResource(Res.string.settings_storage_used, formatBytes(bytes)),
                style = RiftTheme.typography.bodyPrimary,
            )
        } else {
            Text(
                text = stringResource(Res.string.settings_used_space_calculating_text),
                style = RiftTheme.typography.bodySecondary,
            )
        }
    }
}

@Composable
private fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> Res.string.settings_storage_bytes.toUiText(bytes.toUiTextArgument()).resolve()
        bytes < 1024 * 1024 -> Res.string.settings_storage_kilobytes.toUiText((bytes / 1024.0).toUiTextArgument()).resolve()
        bytes < 1024 * 1024 * 1024 -> Res.string.settings_storage_megabytes.toUiText((bytes / (1024.0 * 1024)).toUiTextArgument()).resolve()
        else -> Res.string.settings_storage_gigabytes.toUiText((bytes / (1024.0 * 1024 * 1024)).toUiTextArgument()).resolve()
    }
}

@Composable
private fun ClipboardSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_clipboard_clipboard_title), Modifier.padding(bottom = Spacing.medium))
    Text(
        text = stringResource(Res.string.settings_clipboard_rift_can_import_some_data_text),
        style = RiftTheme.typography.bodySecondary,
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(Res.string.settings_clipboard_troubleshoot_import_issues_text))
        RiftButton(
            text = stringResource(Res.string.settings_clipboard_clipboard_tester_text),
            type = ButtonType.Primary,
            onClick = viewModel::onClipboardTesterClick,
        )
    }
}

@Composable
private fun DevSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_dev_development_title), Modifier.padding(bottom = Spacing.medium))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.settings_dev_mocked_equinox_requests_label),
            isChecked = state.isEquinoxMockingEnabled,
            onCheckedChange = viewModel::onIsEquinoxMockingEnabledChange,
        )
    }
}

@Composable
private fun EveInstallationSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_eve_installation_eve_installation_title))

    Text(
        text = stringResource(Res.string.settings_eve_installation_eve_online_logs_directory_text),
        style = RiftTheme.typography.detailPrimary,
        modifier = Modifier.padding(top = Spacing.medium, bottom = Spacing.verySmall),
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        var text by remember(state.logsDirectory) { mutableStateOf(state.logsDirectory) }
        RiftTextField(
            text = text,
            onTextChanged = {
                text = it
                viewModel.onLogsDirectoryChanged(it)
            },
            modifier = Modifier.weight(1f),
        )
        RequirementIcon(
            isFulfilled = state.isLogsDirectoryValid,
            fulfilledTooltip = stringResource(Res.string.settings_eve_installation_logs_directory_valid_text),
            notFulfilledTooltip = if (state.logsDirectory.isBlank()) stringResource(Res.string.settings_eve_installation_no_logs_directory_text) else stringResource(
                Res.string.settings_eve_installation_invalid_logs_directory_text
            ),
        )
        RiftFileChooserButton(
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY,
            typesDescription = stringResource(Res.string.settings_eve_installation_chat_logs_directory_types_description),
            currentPath = text,
            type = ButtonType.Secondary,
            cornerCut = ButtonCornerCut.None,
            onFileChosen = {
                text = it.absolutePathString()
                viewModel.onLogsDirectoryChanged(it.absolutePathString())
            },
        )
        RiftButton(
            text = stringResource(Res.string.settings_eve_installation_detect_text),
            type = if (state.isLogsDirectoryValid) ButtonType.Secondary else ButtonType.Primary,
            onClick = viewModel::onDetectLogsDirectoryClick,
        )
    }

    Text(
        text = stringResource(Res.string.settings_eve_installation_eve_online_character_settings_directory_text),
        style = RiftTheme.typography.detailPrimary,
        modifier = Modifier.padding(top = Spacing.medium, bottom = Spacing.verySmall),
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        var text by remember(state.settingsDirectory) { mutableStateOf(state.settingsDirectory) }
        RiftTextField(
            text = text,
            onTextChanged = {
                text = it
                viewModel.onSettingsDirectoryChanged(it)
            },
            modifier = Modifier.weight(1f),
        )
        RequirementIcon(
            isFulfilled = state.isSettingsDirectoryValid,
            fulfilledTooltip = stringResource(Res.string.settings_eve_installation_settings_directory_valid_text),
            notFulfilledTooltip = if (state.settingsDirectory.isBlank()) stringResource(Res.string.settings_eve_installation_no_settings_directory_text) else stringResource(
                Res.string.settings_eve_installation_invalid_settings_directory_text
            ),
        )
        RiftFileChooserButton(
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY,
            typesDescription = stringResource(Res.string.settings_eve_installation_game_logs_directory_types_description),
            currentPath = text,
            type = ButtonType.Secondary,
            cornerCut = ButtonCornerCut.None,
            onFileChosen = {
                text = it.absolutePathString()
                viewModel.onSettingsDirectoryChanged(it.absolutePathString())
            },
        )
        RiftButton(
            text = stringResource(Res.string.settings_eve_installation_detect_text),
            type = if (state.isSettingsDirectoryValid) ButtonType.Secondary else ButtonType.Primary,
            onClick = viewModel::onDetectSettingsDirectoryClick,
        )
    }

    Text(
        text = stringResource(Res.string.settings_eve_installation_eve_online_shared_cache_directory_text),
        style = RiftTheme.typography.detailPrimary,
        modifier = Modifier.padding(top = Spacing.medium, bottom = Spacing.verySmall),
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        var text by remember(state.sharedCacheDirectory) { mutableStateOf(state.sharedCacheDirectory) }
        RiftTextField(
            text = text,
            onTextChanged = {
                text = it
                viewModel.onSharedCacheDirectoryChanged(it)
            },
            modifier = Modifier.weight(1f),
        )
        RequirementIcon(
            isFulfilled = state.isSharedCacheDirectoryValid,
            fulfilledTooltip = stringResource(Res.string.settings_eve_installation_shared_cache_directory_valid_text),
            notFulfilledTooltip = if (state.sharedCacheDirectory.isBlank()) stringResource(Res.string.settings_eve_installation_no_shared_cache_directory_text) else stringResource(
                Res.string.settings_eve_installation_invalid_shared_cache_directory_text
            ),
        )
        RiftFileChooserButton(
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY,
            typesDescription = stringResource(Res.string.settings_eve_installation_eve_shared_cache_directory_types_description),
            currentPath = text,
            type = ButtonType.Secondary,
            cornerCut = ButtonCornerCut.None,
            onFileChosen = {
                text = it.absolutePathString()
                viewModel.onSharedCacheDirectoryChanged(it.absolutePathString())
            },
        )
        RiftButton(
            text = stringResource(Res.string.settings_eve_installation_detect_text),
            type = if (state.isSharedCacheDirectoryValid) ButtonType.Secondary else ButtonType.Primary,
            onClick = viewModel::onDetectSharedCacheDirectoryClick,
        )
    }

    Text(
        text = stringResource(Res.string.settings_eve_installation_eve_launcher_logs_directory_text),
        style = RiftTheme.typography.detailPrimary,
        modifier = Modifier.padding(top = Spacing.medium, bottom = Spacing.verySmall),
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        var text by remember(state.launcherLogsDirectory) { mutableStateOf(state.launcherLogsDirectory) }
        RiftTextField(
            text = text,
            onTextChanged = {
                text = it
                viewModel.onLauncherLogsDirectoryChanged(it)
            },
            modifier = Modifier.weight(1f),
        )
        RequirementIcon(
            isFulfilled = state.isLauncherLogsDirectoryValid,
            fulfilledTooltip = stringResource(Res.string.settings_eve_installation_launcher_logs_directory_valid_text),
            notFulfilledTooltip = if (state.launcherLogsDirectory.isBlank()) {
                stringResource(Res.string.settings_eve_installation_no_launcher_logs_directory_text)
            } else {
                stringResource(Res.string.settings_eve_installation_invalid_launcher_logs_directory_text)
            },
        )
        RiftFileChooserButton(
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY,
            typesDescription = stringResource(Res.string.settings_eve_installation_launcher_logs_directory_types_description),
            currentPath = text,
            type = ButtonType.Secondary,
            cornerCut = ButtonCornerCut.None,
            onFileChosen = {
                text = it.absolutePathString()
                viewModel.onLauncherLogsDirectoryChanged(it.absolutePathString())
            },
        )
        RiftButton(
            text = stringResource(Res.string.settings_eve_installation_detect_text),
            type = if (state.isLauncherLogsDirectoryValid) ButtonType.Secondary else ButtonType.Primary,
            onClick = viewModel::onDetectLauncherLogsDirectoryClick,
        )
    }
}

@Composable
private fun CharacterPortraitsSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_character_portraits_character_portraits_title))
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            modifier = Modifier.padding(top = Spacing.small),
        ) {
            listOf(91217127, 2123140346, 2119893075, 2118421377).forEach {
                DynamicCharacterPortraitParallax(
                    characterId = it,
                    size = 48.dp,
                    enterTimestamp = null,
                    pointerInteractionStateHolder = null,
                )
            }
        }
        val parallaxNames = CharacterPortraitsParallaxStrength.entries.associateWith {
            when (it) {
                CharacterPortraitsParallaxStrength.None -> stringResource(Res.string.settings_character_portraits_turned_off_text)
                CharacterPortraitsParallaxStrength.Reduced -> stringResource(Res.string.settings_character_portraits_reduced_text)
                CharacterPortraitsParallaxStrength.Normal -> stringResource(Res.string.settings_character_portraits_normal_text)
            }
        }
        RiftDropdownWithLabel(
            label = stringResource(Res.string.settings_character_portraits_parallax_effect_label),
            items = CharacterPortraitsParallaxStrength.entries,
            selectedItem = state.characterPortraits.parallaxStrength,
            onItemSelected = { viewModel.onCharacterPortraitsParallaxStrengthChanged(it) },
            getItemName = { parallaxNames.getValue(it) },
            tooltip = stringResource(Res.string.settings_character_portraits_shown_characters_characters_outside_intel_tooltip),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            Standing.entries.forEach { standing ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                ) {
                    FlagIcon(standing, disableFlashing = true)
                    DynamicCharacterPortraitStandings(
                        characterId = 324677773,
                        size = 32.dp,
                        standingLevel = standing,
                        isAnimated = true,
                    )
                }
            }
        }
        val standingsNames = CharacterPortraitsStandingsTargets.entries.associateWith {
            when (it) {
                CharacterPortraitsStandingsTargets.All -> stringResource(Res.string.settings_character_portraits_all_text)
                CharacterPortraitsStandingsTargets.OnlyFriendly -> stringResource(Res.string.settings_character_portraits_friendly_text)
                CharacterPortraitsStandingsTargets.OnlyHostile -> stringResource(Res.string.settings_character_portraits_hostile_text)
                CharacterPortraitsStandingsTargets.OnlyNonNeutral -> stringResource(Res.string.settings_character_portraits_non_neutral_text)
                CharacterPortraitsStandingsTargets.None -> stringResource(Res.string.settings_character_portraits_turned_off_text)
            }
        }
        RiftDropdownWithLabel(
            label = stringResource(Res.string.settings_character_portraits_standings_background_label),
            items = CharacterPortraitsStandingsTargets.entries,
            selectedItem = state.characterPortraits.standingsTargets,
            onItemSelected = { viewModel.onCharacterPortraitsStandingsTargetsChanged(it) },
            getItemName = { standingsNames.getValue(it) },
            tooltip = stringResource(Res.string.settings_character_portraits_shown_characters_intel_contexts_tooltip),
        )
        RiftSliderWithLabel(
            label = stringResource(Res.string.settings_character_portraits_standings_background_strength_label),
            width = 100.dp,
            range = 30..100,
            currentValue = (state.characterPortraits.standingsEffectStrength * 100).toInt().coerceIn(0..100),
            onValueChange = { viewModel.onCharacterPortraitsStandingsEffectStrengthChanged(it / 100f) },
            getValueName = { formatPercentage(it) },
        )
    }
}

@Composable
private fun IntelChannelsSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    val allRegionsName = stringResource(Res.string.settings_intel_channels_all_regions_text)
    SectionTitle(stringResource(Res.string.settings_intel_channels_intel_channels_title))
    Text(
        text = stringResource(Res.string.settings_intel_channels_intel_reports_will_read_from_text),
        style = RiftTheme.typography.bodyPrimary,
        modifier = Modifier.padding(vertical = Spacing.medium),
    )
    ScrollbarColumn(
        modifier = Modifier
            .height(300.dp)
            .border(1.dp, RiftTheme.colors.borderGrey),
        scrollbarModifier = Modifier.padding(vertical = Spacing.small),
    ) {
        for (channel in state.intelChannels) {
            key(channel) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .hoverBackground()
                        .padding(Spacing.small),
                ) {
                    val text = buildAnnotatedString {
                        append(channel.name)
                        withStyle(SpanStyle(color = RiftTheme.colors.textSecondary)) {
                            append(" – ${channel.region ?: allRegionsName}")
                        }
                    }
                    Text(
                        text = text,
                        style = RiftTheme.typography.bodyPrimary,
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                    )
                    RiftImageButton(
                        resource = Res.drawable.deleteicon,
                        size = 20.dp,
                        onClick = { viewModel.onIntelChannelDelete(channel) },
                    )
                }
            }
        }
        if (state.intelChannels.isEmpty()) {
            Text(
                text = stringResource(Res.string.settings_intel_channels_no_intel_channels_configured_text),
                style = RiftTheme.typography.headerPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.large)
                    .padding(horizontal = Spacing.large),
            )
            if (state.suggestedIntelChannels != null) {
                Text(
                    text = state.suggestedIntelChannels.promptTitleText.resolve(),
                    style = RiftTheme.typography.bodyPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.medium)
                        .padding(horizontal = Spacing.large),
                )
                RiftButton(
                    text = state.suggestedIntelChannels.promptButtonText.resolve(),
                    type = ButtonType.Primary,
                    cornerCut = ButtonCornerCut.Both,
                    onClick = viewModel::onSuggestedIntelChannelsClick,
                    modifier = Modifier
                        .padding(top = Spacing.medium)
                        .align(Alignment.CenterHorizontally),
                )
            }
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = Modifier.padding(top = Spacing.medium),
    ) {
        var addChannelText by remember { mutableStateOf("") }
        RiftAutocompleteTextField(
            text = addChannelText,
            suggestions = state.autocompleteIntelChannels.filter { it.lowercase().startsWith(addChannelText.lowercase()) }.take(5),
            placeholder = stringResource(Res.string.settings_intel_channels_channel_name_placeholder),
            onTextChanged = {
                addChannelText = it
            },
            modifier = Modifier.weight(1f),
        )
        val regionPlaceholder = stringResource(Res.string.settings_intel_channels_choose_region_text)
        var selectedRegion by remember { mutableStateOf<String?>(null) }
        var isRegionSelected by remember { mutableStateOf(false) }
        RiftDropdown(
            items = listOf(null) + state.regions,
            selectedItem = if (isRegionSelected) selectedRegion else regionPlaceholder,
            onItemSelected = { selectedRegion = it
                isRegionSelected = true
            },
            getItemName = { it ?: allRegionsName },
            maxItems = 5,
        )

        val isNameSelected = addChannelText.isNotEmpty()
        RiftTooltipArea(
            text = if (!isNameSelected) {
                stringResource(Res.string.settings_intel_channels_enter_channel_name_text)
            } else if (!isRegionSelected) {
                stringResource(Res.string.settings_intel_channels_choose_region_channel_text)
            } else {
                null
            },
        ) {
            RiftButton(
                text = stringResource(Res.string.settings_intel_channels_add_channel_text),
                isEnabled = isNameSelected && isRegionSelected,
                onClick = {
                    if (addChannelText.isNotEmpty() && isRegionSelected) {
                        viewModel.onIntelChannelAdded(addChannelText.trim(), selectedRegion)
                        addChannelText = ""
                        selectedRegion = null
                        isRegionSelected = false
                    }
                },
            )
        }
    }
}

@Composable
private fun IntelTimeoutSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_intel_timeout_intel_timeout_title), Modifier.padding(bottom = Spacing.medium))
    val expiryItems = mapOf(
        60 to stringResource(Res.string.settings_intel_timeout_one_minute),
        60 * 2 to stringResource(Res.string.settings_intel_timeout_two_minutes),
        60 * 5 to stringResource(Res.string.settings_intel_timeout_five_minutes),
        60 * 10 to stringResource(Res.string.settings_intel_timeout_ten_minutes),
        60 * 15 to stringResource(Res.string.settings_intel_timeout_fifteen_minutes),
        60 * 30 to stringResource(Res.string.settings_intel_timeout_thirty_minutes),
        60 * 60 to stringResource(Res.string.settings_intel_timeout_one_hour),
        Int.MAX_VALUE to stringResource(Res.string.settings_intel_timeout_don_t_expire_text),
    )
    RiftDropdownWithLabel(
        label = stringResource(Res.string.settings_intel_timeout_expire_intel_after_label),
        items = expiryItems.keys.toList(),
        selectedItem = state.intelExpireSeconds,
        onItemSelected = viewModel::onIntelExpireSecondsChange,
        getItemName = { item -> expiryItems[item] ?: item.toString() },
        tooltip = stringResource(Res.string.settings_intel_timeout_time_after_piece_intel_will_tooltip),
    )
}

@Composable
private fun MapUserInterfaceSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_map_user_interface_map_user_interface_title), Modifier.padding(bottom = Spacing.medium))
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.settings_map_user_interface_compact_mode_label),
            isChecked = state.intelMap.isUsingCompactMode,
            onCheckedChange = viewModel::onIsUsingCompactModeChange,
        )
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.settings_map_user_interface_move_map_follow_character_label),
            tooltip = stringResource(Res.string.settings_map_user_interface_when_jump_another_system_visible_tooltip),
            isChecked = state.intelMap.isFollowingCharacterWithinLayouts,
            onCheckedChange = { viewModel.onIsFollowingCharacterWithinLayoutsChange(it) },
        )
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.settings_map_user_interface_switch_maps_follow_character_label),
            tooltip = stringResource(Res.string.settings_map_user_interface_when_jump_another_system_not_tooltip),
            isChecked = state.intelMap.isFollowingCharacterAcrossLayouts,
            onCheckedChange = { viewModel.onIsFollowingCharacterAcrossLayoutsChange(it) },
        )
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.settings_map_user_interface_invert_scroll_wheel_zoom_label),
            tooltip = stringResource(Res.string.settings_map_user_interface_zoom_direction_will_reversed_tooltip),
            isChecked = state.intelMap.isInvertZoom,
            onCheckedChange = { viewModel.onIsScrollZoomInvertedChange(it) },
        )
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.settings_map_user_interface_always_show_system_labels_label),
            tooltip = stringResource(Res.string.settings_map_user_interface_system_labels_won_t_hide_tooltip),
            isChecked = state.intelMap.isAlwaysShowingSystems,
            onCheckedChange = { viewModel.onIsAlwaysShowingSystemsChange(it) },
        )
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.settings_map_user_interface_prefer_showing_systems_region_maps_label),
            tooltip = stringResource(Res.string.settings_map_user_interface_when_clicking_system_somewhere_rift_tooltip),
            isChecked = state.intelMap.isPreferringRegionMaps,
            onCheckedChange = { viewModel.onIsPreferringRegionMapsChange(it) },
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(end = Spacing.medium).fillMaxWidth(),
        ) {
            Text(stringResource(Res.string.settings_map_user_interface_view_edit_map_markers_text))
            RiftButton(
                text = stringResource(Res.string.settings_map_user_interface_map_markers_text),
                type = ButtonType.Primary,
                onClick = viewModel::onMapNotesClick,
            )
        }
        Text(
            text = stringResource(Res.string.settings_map_zoom_tip),
            style = RiftTheme.typography.bodySecondary,
        )
    }
}

@Composable
private fun MapAutopilotSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_map_autopilot_autopilot_title), Modifier.padding(bottom = Spacing.medium))
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
        Text(
            text = stringResource(Res.string.settings_map_autopilot_when_setting_autopilot_destination_use_text),
            style = RiftTheme.typography.bodySecondary,
        )
        RiftRadioButtonWithLabel(
            label = stringResource(Res.string.settings_map_autopilot_rift_calculated_route_label),
            tooltip = stringResource(Res.string.settings_map_autopilot_route_as_shown_rift_map_tooltip),
            isChecked = state.isUsingRiftAutopilotRoute,
            onChecked = { viewModel.onIsUsingRiftAutopilotRouteChange(true) },
        )
        RiftRadioButtonWithLabel(
            label = stringResource(Res.string.settings_map_autopilot_eve_calculated_route_label),
            tooltip = stringResource(Res.string.settings_map_autopilot_route_as_set_by_eve_tooltip),
            isChecked = !state.isUsingRiftAutopilotRoute,
            onChecked = { viewModel.onIsUsingRiftAutopilotRouteChange(false) },
        )
        Text(
            text = stringResource(Res.string.settings_map_autopilot_security_preference_text),
            style = RiftTheme.typography.bodySecondary,
            modifier = Modifier.padding(top = Spacing.small),
        )
        RiftRadioButtonWithLabel(
            label = stringResource(Res.string.settings_map_autopilot_prefer_shorter_ignore_security_status_label),
            isChecked = state.routePreference == RoutePreference.Shortest,
            onChecked = { viewModel.onRoutePreferenceChange(RoutePreference.Shortest) },
        )
        RiftRadioButtonWithLabel(
            label = stringResource(Res.string.settings_map_autopilot_prefer_safer_stay_high_sec_label),
            isChecked = state.routePreference == RoutePreference.Safer,
            onChecked = { viewModel.onRoutePreferenceChange(RoutePreference.Safer) },
        )
        RiftRadioButtonWithLabel(
            label = stringResource(Res.string.settings_map_autopilot_prefer_less_secure_stay_low_label),
            isChecked = state.routePreference == RoutePreference.LessSecure,
            onChecked = { viewModel.onRoutePreferenceChange(RoutePreference.LessSecure) },
        )
        RiftRadioButtonWithLabel(
            label = stringResource(Res.string.settings_map_autopilot_prefer_insecure_stay_null_sec_label),
            isChecked = state.routePreference == RoutePreference.Insecure,
            onChecked = { viewModel.onRoutePreferenceChange(RoutePreference.Insecure) },
        )
    }
}

@Composable
private fun MapIntelPopupsSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_map_intel_popups_intel_popups_title), Modifier.padding(bottom = Spacing.medium))
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
        val timeoutItems = mapOf(
            0 to stringResource(Res.string.settings_map_intel_popups_don_t_show_text),
            10 to stringResource(Res.string.settings_map_intel_popups_ten_seconds),
            30 to stringResource(Res.string.settings_map_intel_popups_thirty_seconds),
            60 to stringResource(Res.string.settings_intel_timeout_one_minute),
            60 * 2 to stringResource(Res.string.settings_intel_timeout_two_minutes),
            60 * 5 to stringResource(Res.string.settings_intel_timeout_five_minutes),
            60 * 15 to stringResource(Res.string.settings_intel_timeout_fifteen_minutes),
            Int.MAX_VALUE to stringResource(Res.string.settings_map_intel_popups_no_limit_text),
        )
        RiftDropdownWithLabel(
            label = stringResource(Res.string.settings_map_intel_popups_automatically_show_popups_label),
            items = timeoutItems.keys.toList(),
            selectedItem = state.intelMap.intelPopupTimeoutSeconds,
            onItemSelected = viewModel::onIntelPopupTimeoutSecondsChange,
            getItemName = { item -> timeoutItems[item] ?: item.toString() },
            tooltip = stringResource(Res.string.settings_map_intel_popups_how_long_will_intel_popups_tooltip),
        )
    }
}

@Composable
private fun JumpBridgeNetworkSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_jump_bridge_network_jump_bridge_network_title), Modifier.padding())
    Column {
        val solarSystemsRepository: SolarSystemsRepository = remember { koin.get() }
        ScrollbarLazyColumn(
            modifier = Modifier
                .height(250.dp)
                .border(1.dp, RiftTheme.colors.borderGrey),
            scrollbarModifier = Modifier.padding(vertical = Spacing.small),
            contentPadding = PaddingValues(vertical = Spacing.verySmall),
        ) {
            if (state.jumpBridgeNetwork.isNotEmpty()) {
                val connections = state.jumpBridgeNetwork.sortedBy { it.from.name }
                for (connection in connections) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .hoverBackground()
                                .padding(horizontal = Spacing.small, vertical = Spacing.verySmall),
                        ) {
                            RiftSolarSystemChip(
                                state = SolarSystemChipState(
                                    locationsText = null,
                                    jumpsText = null,
                                    name = connection.from.name,
                                    security = connection.from.security.roundSecurity(),
                                    region = solarSystemsRepository.getRegionBySystem(connection.from.name)?.name,
                                ),
                                hasBackground = false,
                            )
                            Text(
                                text = "→",
                                style = RiftTheme.typography.bodyPrimary,
                            )
                            RiftSolarSystemChip(
                                state = SolarSystemChipState(
                                    locationsText = null,
                                    jumpsText = null,
                                    name = connection.to.name,
                                    security = connection.to.security.roundSecurity(),
                                    region = solarSystemsRepository.getRegionBySystem(connection.to.name)?.name,
                                ),
                                hasBackground = false,
                            )
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = stringResource(Res.string.settings_jump_bridge_network_no_jump_bridges_imported_text),
                        style = RiftTheme.typography.headerPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.large)
                            .padding(horizontal = Spacing.large),
                    )
                    AnimatedContent(state.jumpBridgeCopyState) { copyState ->
                        when (copyState) {
                            JumpBridgeCopyState.NotCopied -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = Spacing.medium),
                                ) {
                                    Text(stringResource(Res.string.settings_jump_bridge_network_import_jump_bridges_by_copying_text))
                                    when (state.jumpBridgesReference) {
                                        is ConfigurationPackRepository.JumpBridgesReference.Url -> {
                                            Text(stringResource(Res.string.settings_jump_bridge_network_can_press_ctrl_ctrl_c_text))
                                            LinkText(
                                                text = stringResource(Res.string.settings_jump_bridge_list, state.jumpBridgesReference.packName),
                                                onClick = { state.jumpBridgesReference.url.toURIOrNull()?.openBrowser() },
                                            )
                                        }
                                        is ConfigurationPackRepository.JumpBridgesReference.Text -> {
                                            Text(
                                                text = stringResource(Res.string.settings_jump_bridge_available, state.jumpBridgesReference.packName, formatDate(state.jumpBridgesReference.date)),
                                                textAlign = TextAlign.Center,
                                            )
                                            LinkText(
                                                text = stringResource(Res.string.settings_jump_bridge_network_click_use_text),
                                                onClick = { Clipboard.copy(state.jumpBridgesReference.text) },
                                            )
                                        }
                                        null -> {
                                            val pointerInteractionStateHolder = remember { PointerInteractionStateHolder() }
                                            RiftTooltipArea(
                                                text = buildAnnotatedString {
                                                    appendLine(stringResource(Res.string.settings_jump_bridge_network_any_format_will_work_as_text))
                                                    appendLine()
                                                    withColor(RiftTheme.colors.textHighlighted) {
                                                        appendLine("Jita -> Perimeter")
                                                        appendLine("New Caldari -> Alikara")
                                                        append("Hirtamon -> Ikuchi")
                                                    }
                                                },
                                            ) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                                                    modifier = Modifier
                                                        .pointerInteraction(pointerInteractionStateHolder)
                                                        .padding(vertical = Spacing.small),
                                                ) {
                                                    Text(
                                                        text = stringResource(Res.string.settings_jump_bridge_network_format_info_text),
                                                        style = RiftTheme.typography.bodySecondary,
                                                    )
                                                    RiftMulticolorIcon(
                                                        type = MulticolorIconType.Info,
                                                        parentPointerInteractionStateHolder = pointerInteractionStateHolder,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            is JumpBridgeCopyState.Copied -> {
                                val tooltip = buildString {
                                    val connections = copyState.network.take(5).joinToString("\n") {
                                        "${it.from.name} → ${it.to.name}"
                                    }
                                    append(connections)
                                    if (copyState.network.size > 5) {
                                        appendLine()
                                        append(stringResource(Res.string.settings_jump_bridge_network_more_text))
                                    }
                                }
                                RiftTooltipArea(
                                    text = tooltip,
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(Spacing.small),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = Spacing.medium),
                                    ) {
                                        Text(stringResource(Res.string.settings_jump_bridge_network_copied_network_text))
                                        RiftButton(
                                            text = Res.plurals.settings_import_connections.toUiText(copyState.network.size, copyState.network.size.toUiTextArgument()).resolve(),
                                            onClick = viewModel::onJumpBridgeImportClick,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        AnimatedContent(state.jumpBridgeNetwork) { network ->
            if (network.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = Spacing.medium).fillMaxWidth(),
                ) {
                    Text(Res.plurals.settings_network_loaded.toUiText(network.size, network.size.toUiTextArgument()).resolve())
                    Spacer(Modifier.weight(1f))
                    RiftButton(
                        text = stringResource(Res.string.settings_jump_bridge_network_copy_text),
                        type = ButtonType.Primary,
                        cornerCut = ButtonCornerCut.None,
                        onClick = viewModel::onJumpBridgeCopyClick,
                        modifier = Modifier.padding(end = Spacing.medium),
                    )
                    RiftButton(
                        text = stringResource(Res.string.settings_jump_bridge_network_forget_text),
                        type = ButtonType.Negative,
                        onClick = viewModel::onJumpBridgeForgetClick,
                    )
                }
            }
        }
        AnimatedContent(state.jumpBridgeSearchState, contentKey = { it::class }) { searchState ->
            when (searchState) {
                JumpBridgeSearchState.NotSearched -> {
                    if (state.jumpBridgeNetwork.isEmpty()) {
                        Column(
                            modifier = Modifier.padding(top = Spacing.medium),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(stringResource(Res.string.settings_jump_bridge_network_search_automatically_text))
                                RiftButton(
                                    text = stringResource(Res.string.settings_jump_bridge_network_search_text),
                                    onClick = viewModel::onJumpBridgeSearchClick,
                                )
                            }
                        }
                    }
                }
                is JumpBridgeSearchState.Searching -> {
                    Column(
                        modifier = Modifier.padding(top = Spacing.medium),
                    ) {
                        Text(Res.string.settings_search_progress.toUiText((searchState.progress * 100).toDouble().toUiTextArgument(fractionDigits = 1)).resolve())
                        Text(
                            text = Res.plurals.settings_search_connections.toUiText(searchState.connectionsCount, searchState.connectionsCount.toUiTextArgument()).resolve(),
                            style = RiftTheme.typography.bodySecondary,
                        )
                    }
                }
                is JumpBridgeSearchState.SearchFailed -> {
                    Column(
                        modifier = Modifier.padding(top = Spacing.medium),
                    ) {
                        Text(searchState.message.resolve())
                    }
                }
                is JumpBridgeSearchState.SearchDone -> {
                    Column(
                        modifier = Modifier.padding(top = Spacing.medium),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(Res.plurals.settings_search_network.toUiText(searchState.network.size, searchState.network.size.toUiTextArgument()).resolve())
                            RiftButton(
                                text = stringResource(Res.string.settings_jump_bridge_network_import_text),
                                onClick = viewModel::onJumpBridgeSearchImportClick,
                            )
                        }
                    }
                }
            }
        }
        AnimatedVisibility(state.jumpBridgeNetwork.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
                modifier = Modifier.padding(top = Spacing.medium),
            ) {
                RiftCheckboxWithLabel(
                    label = stringResource(Res.string.settings_jump_bridge_network_show_network_map_label),
                    tooltip = stringResource(Res.string.settings_jump_bridge_network_jump_bridge_connection_lines_will_tooltip),
                    isChecked = state.intelMap.isJumpBridgeNetworkShown,
                    onCheckedChange = viewModel::onIsJumpBridgeNetworkShownChange,
                )
                RiftSliderWithLabel(
                    label = stringResource(Res.string.settings_jump_bridge_network_connection_opacity_label),
                    width = 100.dp,
                    range = 10..100,
                    currentValue = state.intelMap.jumpBridgeNetworkOpacity,
                    onValueChange = viewModel::onJumpBridgeNetworkOpacityChange,
                    getValueName = { formatPercentage(it) },
                    tooltip = stringResource(Res.string.settings_jump_bridge_network_visibility_jump_bridge_connection_lines_tooltip),
                )
            }
        }
    }
}

@Composable
private fun SovereigntyUpgradesSection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_sovereignty_upgrades_sovereignty_upgrades_title), Modifier.padding())
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        val solarSystemsRepository: SolarSystemsRepository = remember { koin.get() }
        ScrollbarLazyColumn(
            modifier = Modifier
                .height(250.dp)
                .border(1.dp, RiftTheme.colors.borderGrey),
            scrollbarModifier = Modifier.padding(vertical = Spacing.small),
            contentPadding = PaddingValues(vertical = Spacing.verySmall),
        ) {
            if (state.sovereigntyUpgrades.isNotEmpty()) {
                for ((system, upgrades) in state.sovereigntyUpgrades) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .hoverBackground()
                                .padding(horizontal = Spacing.small, vertical = Spacing.verySmall),
                        ) {
                            RiftSolarSystemChip(
                                state = SolarSystemChipState(
                                    locationsText = null,
                                    jumpsText = null,
                                    name = system.name,
                                    security = system.security,
                                    region = solarSystemsRepository.getRegionBySystem(system.name)?.name,
                                ),
                                hasBackground = false,
                            )
                            for (type in upgrades) {
                                RiftTooltipArea(
                                    text = type.name,
                                    modifier = Modifier.size(32.dp),
                                ) {
                                    AsyncTypeIcon(
                                        type = type,
                                        modifier = Modifier.size(32.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = stringResource(Res.string.settings_sovereignty_upgrades_no_sovereignty_upgrades_imported_text),
                        style = RiftTheme.typography.headerPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.large)
                            .padding(horizontal = Spacing.large),
                    )
                    AnimatedContent(state.sovereigntyUpgradesCopyState) { copyState ->
                        when (copyState) {
                            SovereigntyUpgradesCopyState.NotCopied -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = Spacing.medium),
                                ) {
                                    Text(stringResource(Res.string.settings_sovereignty_upgrades_import_upgrades_by_copying_list_text))
                                    if (state.sovereigntyUpgradesUrl != null) {
                                        Text(
                                            text = stringResource(Res.string.settings_sovereignty_upgrades_can_press_ctrl_ctrl_c_text),
                                            textAlign = TextAlign.Center,
                                        )
                                        LinkText(
                                            text = stringResource(Res.string.settings_sovereignty_upgrades_alliance_sovereignty_upgrades_list_text),
                                            onClick = { state.sovereigntyUpgradesUrl.toURIOrNull()?.openBrowser() },
                                        )
                                    } else {
                                        Text(stringResource(Res.string.settings_sovereignty_upgrades_need_system_name_upgrade_names_text))
                                    }
                                }
                            }
                            is SovereigntyUpgradesCopyState.Copied -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = Spacing.medium),
                                ) {
                                    Text(stringResource(Res.string.settings_sovereignty_upgrades_copied_upgrades_text))
                                    RiftButton(
                                        text = Res.plurals.settings_import_systems.toUiText(copyState.upgrades.size,copyState.upgrades.size.toUiTextArgument()).resolve(),
                                        onClick = viewModel::onSovereigntyUpgradesImportClick,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedContent(state.sovereigntyUpgrades) { upgrades ->
            if (upgrades.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = Spacing.medium).fillMaxWidth(),
                ) {
                    Text(Res.plurals.settings_upgrades_loaded.toUiText(upgrades.size, upgrades.size.toUiTextArgument()).resolve())
                    Spacer(Modifier.weight(1f))
                    RiftButton(
                        text = stringResource(Res.string.settings_jump_bridge_network_copy_text),
                        type = ButtonType.Primary,
                        cornerCut = ButtonCornerCut.None,
                        onClick = viewModel::onSovereigntyUpgradesCopyClick,
                        modifier = Modifier.padding(end = Spacing.medium),
                    )
                    RiftButton(
                        text = stringResource(Res.string.settings_jump_bridge_network_forget_text),
                        type = ButtonType.Negative,
                        onClick = viewModel::onSovereigntyUpgradesForgetClick,
                    )
                }
            }
        }

        RiftCheckboxWithLabel(
            label = stringResource(Res.string.settings_sovereignty_upgrades_import_upgrades_from_hacked_sovereignty_label),
            tooltip = stringResource(Res.string.settings_sovereignty_upgrades_automatically_import_sovereignty_upgrades_when_tooltip),
            isChecked = state.isSovereigntyUpgradesHackImportingEnabled,
            onCheckedChange = viewModel::onIsSovereigntyUpgradesHackImportingEnabledClick,
        )
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.settings_sovereignty_upgrades_import_offline_upgrades_from_hacked_label),
            tooltip = stringResource(Res.string.settings_sovereignty_upgrades_when_importing_sovereignty_upgrades_from_tooltip),
            isChecked = state.isSovereigntyUpgradesHackImportingOfflineEnabled,
            onCheckedChange = viewModel::onIsSovereigntyUpgradesHackImportingOfflineEnabledClick,
        )
    }
}

@Composable
private fun SystemInfoOverlaySection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_system_info_overlay_system_info_panel_title), Modifier.padding())
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier.padding(top = Spacing.medium)
    ) {
        Text(
            text = stringResource(Res.string.settings_system_info_overlay_system_info_overlay_shows_information_text),
            style = RiftTheme.typography.bodySecondary,
        )
        val player = rememberUiSoundPlayer()
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier.padding(top = Spacing.small, bottom = Spacing.small),
        ) {
            OverlayInfoType.entries.forEach { type ->
                val [label, description] = getOverlayInfoTypeName(type)
                RiftTooltipArea(text = description) {
                    val isSelected = type in state.overlayInfoTypes
                    RiftPill(
                        text = label,
                        isSelected = isSelected,
                        onClick = {
                            player.playSelect(isSelected)
                            viewModel.onOverlayInfoTypeChanged(type, !isSelected)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun OverlayGeneralSection(
    overlayWindowStatuses: OverlayWindowStatuses,
) {
    val placementController: OverlayPlacementController = remember { koin.get() }
    val isPositioning by placementController.isPositioning.collectAsState()
    val settings: Settings = remember { koin.get() }
    val settingsModel by settings.updateFlow.collectAsState()
    val hasReadyOverlay = overlayWindowStatuses.windows.any { it.status == OverlayStatus.Ready }
    LaunchedEffect(hasReadyOverlay) {
        if (!hasReadyOverlay) placementController.setPositioning(false)
    }
    val unavailableTooltip = stringResource(Res.string.settings_overlay_general_no_eve_window_currently_showing_text)
    SectionTitle(stringResource(Res.string.settings_toolbar_row_overlay_title), Modifier.padding())
    Column(
        modifier = Modifier.padding(top = Spacing.medium),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            RiftTooltipArea(text = unavailableTooltip.takeUnless { hasReadyOverlay }) {
                RiftButton(
                    text = if (isPositioning) stringResource(Res.string.settings_overlay_general_finish_moving_text) else stringResource(Res.string.settings_overlay_general_move_overlay_text),
                    isEnabled = hasReadyOverlay,
                    onClick = { placementController.setPositioning(!isPositioning) },
                )
            }
            RiftTooltipArea(
                text = when {
                    !hasReadyOverlay -> unavailableTooltip
                    settingsModel.overlayPosition == null -> stringResource(Res.string.settings_overlay_general_overlay_already_at_its_default_text)
                    else -> null
                },
            ) {
                RiftButton(
                    text = stringResource(Res.string.settings_overlay_general_reset_position_text),
                    isEnabled = hasReadyOverlay && settingsModel.overlayPosition != null,
                    onClick = { settings.overlayPosition = null },
                )
            }
        }
        AnimatedVisibility(isPositioning) {
            Text(
                text = stringResource(Res.string.settings_overlay_general_drag_handle_shown_eve_window_text),
                style = RiftTheme.typography.bodySecondary,
                modifier = Modifier.padding(top = Spacing.medium)
            )
        }
    }
}

private data class OverlayWindowStatuses(
    val repositoryStatus: EveWindowsRepository.Status,
    val windows: List<OverlayWindowStatus>,
)

private data class OverlayWindowStatus(
    val characterId: Int?,
    val characterName: String,
    val hasWindow: Boolean,
    val status: OverlayStatus,
)

@Composable
private fun getOverlayWindowStatuses(enabledCharacterIds: List<Int>): OverlayWindowStatuses {
    val windowsRepository: EveWindowsRepository = remember { koin.get() }
    val windowsState by windowsRepository.state.collectAsState()
    val charactersRepository: LocalCharactersRepository = remember { koin.get() }
    val characters by charactersRepository.allCharacters.collectAsState()
    val locationRepository: CharacterLocationRepository = remember { koin.get() }
    val locations by locationRepository.locations.collectAsState()
    val systemsRepository: SolarSystemsRepository = remember { koin.get() }
    val runtimeStatus: OverlayRuntimeStatusRepository = remember { koin.get() }
    val failures by runtimeStatus.failures.collectAsState()

    val windows = if (windowsState.status == EveWindowsRepository.Status.Available) windowsState.windows else emptyList()
    val statuses = windows.map { window ->
        val character = characters.firstOrNull { it.info?.name == window.characterName }
        val systemId = character?.characterId?.let { locations[it]?.solarSystemId }
        OverlayWindowStatus(
            characterId = character?.characterId,
            characterName = window.characterName,
            hasWindow = true,
            status = getOverlayStatus(
                window = window,
                isOverlayEnabled = character == null || character.characterId in enabledCharacterIds,
                character = character,
                systemId = systemId,
                isSystemAvailable = systemId?.let { systemsRepository.getSystem(it) != null } == true,
                runtimeFailure = failures[window.id],
            ),
        )
    }
    val windowCharacterNames = windows.map { it.characterName }.toSet()
    val charactersWithoutWindows = characters
        .filterNot { it.isHidden }
        .filterNot { it.info?.name in windowCharacterNames }
        .map { character ->
            OverlayWindowStatus(
                characterId = character.characterId,
                characterName = character.info?.name ?: character.characterId.toString(),
                hasWindow = false,
                status = if (character.characterId in enabledCharacterIds) OverlayStatus.NoEveWindow else OverlayStatus.Disabled,
            )
        }
    return OverlayWindowStatuses(windowsState.status, statuses + charactersWithoutWindows)
}

@Composable
private fun DistanceIntelOverlaySection(
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_distance_intel_overlay_nearby_intel_panel_title))
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier.padding(top = Spacing.medium)
    ) {
        RiftCheckboxWithLabel(
            label = stringResource(Res.string.settings_distance_intel_overlay_always_show_hostile_count_nearby_label),
            isChecked = state.isOverlayDistanceIntelCountAlwaysShown,
            onCheckedChange = viewModel::onOverlayDistanceIntelCountAlwaysShownChanged,
        )
        RiftSliderWithLabel(
            label = stringResource(Res.string.settings_distance_intel_overlay_nearby_intel_max_jumps_label),
            width = 150.dp,
            range = 0..10,
            currentValue = state.overlayDistanceIntelMaxJumps,
            onValueChange = viewModel::onOverlayDistanceIntelMaxJumpsChanged,
            isShowingValue = true,
        )
    }
}

@Composable
private fun OverlayStatusSection(
    overlayWindowStatuses: OverlayWindowStatuses,
    state: UiState,
    viewModel: SettingsViewModel,
) {
    SectionTitle(stringResource(Res.string.settings_overlay_status_eve_windows_title))
    ScrollbarLazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .border(1.dp, RiftTheme.colors.borderGrey),
        scrollbarModifier = Modifier.padding(vertical = Spacing.small),
        contentPadding = PaddingValues(vertical = Spacing.verySmall),
    ) {
        when (val status = overlayWindowStatuses.repositoryStatus) {
            EveWindowsRepository.Status.Available -> {
                if (overlayWindowStatuses.windows.isEmpty()) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(Spacing.large),
                        ) {
                            Text(
                                text = stringResource(Res.string.settings_overlay_status_no_eve_windows_detected_text),
                                style = RiftTheme.typography.headerPrimary,
                            )
                            Text(
                                text = stringResource(Res.string.settings_overlay_status_log_game_see_overlays_text),
                                style = RiftTheme.typography.bodySecondary,
                            )
                        }
                    }
                } else {
                    items(overlayWindowStatuses.windows.size) { index ->
                        val windowStatus = overlayWindowStatuses.windows[index]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                            modifier = Modifier
                                .padding(vertical = Spacing.verySmall)
                                .fillMaxWidth()
                                .hoverBackground()
                                .padding(horizontal = Spacing.medium, vertical = Spacing.verySmall),
                        ) {
                            val style = if (windowStatus.hasWindow) {
                                RiftTheme.typography.bodyPrimary
                            } else {
                                RiftTheme.typography.bodySecondary
                            }
                            Text(
                                text = windowStatus.characterName,
                                style = style,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            val statusStyle = if (windowStatus.status == OverlayStatus.Ready) {
                                RiftTheme.typography.bodyPrimary
                            } else {
                                RiftTheme.typography.bodySecondary
                            }
                            Text(
                                text = windowStatus.status.displayText.resolve(),
                                style = statusStyle
                            )
                            RiftCheckbox(
                                isChecked = windowStatus.characterId in state.overlayEnabledCharacterIds,
                                isEnabled = windowStatus.characterId != null,
                                onCheckedChange = { enabled ->
                                    windowStatus.characterId?.let {
                                        viewModel.onCharacterOverlayEnabledChanged(it, enabled)
                                    }
                                },
                            )
                        }
                    }
                }
            }
            else -> item {
                val message = when (status) {
                    EveWindowsRepository.Status.Initializing -> stringResource(Res.string.settings_overlay_status_detecting_eve_windows_text)
                    EveWindowsRepository.Status.PermissionRequired -> stringResource(Res.string.settings_overlay_status_eve_window_detection_needs_accessibility_text)
                    is EveWindowsRepository.Status.Unsupported -> stringResource(Res.string.settings_overlay_unsupported, status.reason)
                    is EveWindowsRepository.Status.Failed -> stringResource(Res.string.settings_overlay_detection_failed, status.reason)
                    EveWindowsRepository.Status.Available -> error("Handled above")
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(Spacing.large),
                ) {
                    Text(
                        text = message,
                        style = RiftTheme.typography.bodySecondary,
                        textAlign = TextAlign.Center,
                    )
                    if (status == EveWindowsRepository.Status.PermissionRequired) {
                        RiftButton(
                            text = stringResource(Res.string.settings_overlay_status_open_accessibility_settings_text),
                            onClick = {
                                try {
                                    ProcessBuilder(
                                        "/usr/bin/open",
                                        "-b", "com.apple.systempreferences",
                                        "-u", "x-apple.systempreferences:com.apple.preference.security?Privacy_Accessibility",
                                    ).start()
                                } catch (e: IOException) {
                                    logger.warn(e) { "Could not open macOS Accessibility settings" }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getOverlayInfoTypeName(type: OverlayInfoType): Pair<String, String> = when (type) {
    OverlayInfoType.System -> stringResource(Res.string.settings_overlay_info_system) to stringResource(Res.string.settings_overlay_info_system_name_security_status)
    OverlayInfoType.Constellation -> stringResource(Res.string.settings_overlay_info_constellation) to stringResource(Res.string.settings_overlay_info_constellation_name)
    OverlayInfoType.Region -> stringResource(Res.string.settings_overlay_info_region) to stringResource(Res.string.settings_overlay_info_region_name)
    OverlayInfoType.LocalRats -> stringResource(Res.string.settings_overlay_info_rats) to stringResource(Res.string.settings_overlay_info_faction_rats_system)
    OverlayInfoType.SpaceType -> stringResource(Res.string.settings_overlay_info_space_type) to stringResource(Res.string.settings_overlay_info_type_space_standings_towards_sovereignty)
    OverlayInfoType.Sovereignty -> stringResource(Res.string.settings_toolbar_row_sovereignty_title) to stringResource(Res.string.settings_overlay_info_alliance_or_faction_holding_sovereignty)
    OverlayInfoType.MapMarkers -> stringResource(Res.string.settings_map_user_interface_map_markers_text) to stringResource(Res.string.settings_overlay_info_markers_added_system_map)
    OverlayInfoType.Jumps -> stringResource(Res.string.settings_overlay_info_jumps) to stringResource(Res.string.settings_overlay_info_number_jumps_last_hour)
    OverlayInfoType.Kills -> stringResource(Res.string.settings_overlay_info_kills) to stringResource(Res.string.settings_overlay_info_number_ship_pod_kills_last)
    OverlayInfoType.NpcKills -> stringResource(Res.string.settings_overlay_info_npc_kills) to stringResource(Res.string.settings_overlay_info_number_npcs_killed_last_hour)
    OverlayInfoType.Assets -> stringResource(Res.string.settings_overlay_info_assets) to stringResource(Res.string.settings_overlay_info_number_owned_assets_located_here)
    OverlayInfoType.Stations -> stringResource(Res.string.settings_overlay_info_stations) to stringResource(Res.string.settings_overlay_info_number_stations)
    OverlayInfoType.AsteroidBelts -> stringResource(Res.string.settings_overlay_info_asteroid_belts) to stringResource(Res.string.settings_overlay_info_indicator_asteroid_belts)
    OverlayInfoType.IceFields -> stringResource(Res.string.settings_overlay_info_ice_fields) to stringResource(Res.string.settings_overlay_info_indicator_ice_fields)
    OverlayInfoType.Colonies -> stringResource(Res.string.settings_overlay_info_colonies) to stringResource(Res.string.settings_overlay_info_indicator_pi_colonies)
    OverlayInfoType.JoveObservatories -> stringResource(Res.string.settings_overlay_info_jove_observatories) to stringResource(Res.string.settings_overlay_info_indicator_jove_observatories)
    OverlayInfoType.AnsiblexZone -> stringResource(Res.string.settings_overlay_info_ansiblex_zone) to stringResource(Res.string.settings_overlay_info_ansiblex_cost_zone_based_distance)
    OverlayInfoType.JumpRange -> stringResource(Res.string.settings_overlay_info_jump_range) to stringResource(Res.string.settings_overlay_info_jump_distance_system)
    OverlayInfoType.Clones -> stringResource(Res.string.settings_overlay_info_clones) to stringResource(Res.string.settings_overlay_info_indicator_jump_clones)
    OverlayInfoType.Planets -> stringResource(Res.string.settings_overlay_info_planets) to stringResource(Res.string.settings_overlay_info_indicator_planets)
    OverlayInfoType.SovereigntyUpgrades -> stringResource(Res.string.settings_overlay_info_sovereignty_upgrades) to stringResource(Res.string.settings_overlay_info_indicators_installed_sovereignty_upgrades)
    OverlayInfoType.RaidableSkyhooks -> stringResource(Res.string.settings_overlay_info_raidable_skyhooks) to stringResource(Res.string.settings_overlay_info_indicators_raidable_skyhooks)
}

private fun formatPercentage(value: Int): String {
    return NumberFormat.getPercentInstance(Locale.getDefault(Locale.Category.FORMAT)).format(value / 100.0)
}
