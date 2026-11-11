package dev.nohus.rift.alerts.list

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.nohus.rift.alerts.Alert
import dev.nohus.rift.alerts.AlertAction
import dev.nohus.rift.alerts.AlertTrigger
import dev.nohus.rift.alerts.ChatMessageChannel
import dev.nohus.rift.alerts.GameActionType
import dev.nohus.rift.alerts.EveNotificationCharacterStatus
import dev.nohus.rift.alerts.IntelReportLocation
import dev.nohus.rift.alerts.IntelReportType
import dev.nohus.rift.alerts.JabberMessageChannel
import dev.nohus.rift.alerts.JabberPingType
import dev.nohus.rift.alerts.JumpRange
import dev.nohus.rift.alerts.PapType
import dev.nohus.rift.alerts.PiEventType
import dev.nohus.rift.alerts.create.CreateAlertDialog
import dev.nohus.rift.alerts.creategroup.CreateGroupDialog
import dev.nohus.rift.alerts.list.AlertsViewModel.UiState
import dev.nohus.rift.characters.repositories.LocalCharactersRepository.LocalCharacter
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.ExpandChevron
import dev.nohus.rift.compose.PointerInteractionStateHolder
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftCheckbox
import dev.nohus.rift.compose.RiftDropdownWithLabel
import dev.nohus.rift.compose.RiftImageButton
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.ScrollbarLazyColumn
import dev.nohus.rift.compose.hoverBackground
import dev.nohus.rift.compose.pointerInteraction
import dev.nohus.rift.compose.theme.Cursors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.contacts.ContactsRepository.Label
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.alerts_description_action_builtin_sound
import dev.nohus.rift.generated.resources.alerts_description_action_colonies
import dev.nohus.rift.generated.resources.alerts_description_action_custom_sound
import dev.nohus.rift.generated.resources.alerts_description_action_message
import dev.nohus.rift.generated.resources.alerts_description_action_ping
import dev.nohus.rift.generated.resources.alerts_description_action_push
import dev.nohus.rift.generated.resources.alerts_description_action_rift
import dev.nohus.rift.generated.resources.alerts_description_action_system
import dev.nohus.rift.generated.resources.alerts_description_actions_prefix
import dev.nohus.rift.generated.resources.alerts_description_any_channel
import dev.nohus.rift.generated.resources.alerts_description_any_character
import dev.nohus.rift.generated.resources.alerts_description_any_colony
import dev.nohus.rift.generated.resources.alerts_description_any_jabber_chat
import dev.nohus.rift.generated.resources.alerts_description_any_offline_character
import dev.nohus.rift.generated.resources.alerts_description_any_online_character
import dev.nohus.rift.generated.resources.alerts_description_asteroid_prefix
import dev.nohus.rift.generated.resources.alerts_description_attack_target_prefix
import dev.nohus.rift.generated.resources.alerts_description_chat_message_prefix
import dev.nohus.rift.generated.resources.alerts_description_colony_count
import dev.nohus.rift.generated.resources.alerts_description_colony_prefix
import dev.nohus.rift.generated.resources.alerts_description_combat_target_prefix
import dev.nohus.rift.generated.resources.alerts_description_condition_separator
import dev.nohus.rift.generated.resources.alerts_description_contact_label_count
import dev.nohus.rift.generated.resources.alerts_description_cooldown_prefix
import dev.nohus.rift.generated.resources.alerts_description_custom_game_prefix
import dev.nohus.rift.generated.resources.alerts_description_decloak_exceptions_suffix
import dev.nohus.rift.generated.resources.alerts_description_direct_message
import dev.nohus.rift.generated.resources.alerts_description_doctrine_prefix
import dev.nohus.rift.generated.resources.alerts_description_duration_prefix
import dev.nohus.rift.generated.resources.alerts_description_eve_notification_prefix
import dev.nohus.rift.generated.resources.alerts_description_exclude_self_suffix
import dev.nohus.rift.generated.resources.alerts_description_fleet_commander_count
import dev.nohus.rift.generated.resources.alerts_description_fleet_commander_suffix
import dev.nohus.rift.generated.resources.alerts_description_fleet_filter_prefix
import dev.nohus.rift.generated.resources.alerts_description_fleet_ping_prefix
import dev.nohus.rift.generated.resources.alerts_description_formup_prefix
import dev.nohus.rift.generated.resources.alerts_description_game_asteroid_depleted
import dev.nohus.rift.generated.resources.alerts_description_game_attacking
import dev.nohus.rift.generated.resources.alerts_description_game_character_prefix
import dev.nohus.rift.generated.resources.alerts_description_game_charges_depleted
import dev.nohus.rift.generated.resources.alerts_description_game_combat_stopped
import dev.nohus.rift.generated.resources.alerts_description_game_conversation
import dev.nohus.rift.generated.resources.alerts_description_game_decloaked
import dev.nohus.rift.generated.resources.alerts_description_game_in_combat
import dev.nohus.rift.generated.resources.alerts_description_game_under_attack
import dev.nohus.rift.generated.resources.alerts_description_game_warp_scrambled
import dev.nohus.rift.generated.resources.alerts_description_hours
import dev.nohus.rift.generated.resources.alerts_description_jabber_message_prefix
import dev.nohus.rift.generated.resources.alerts_description_labeled_characters
import dev.nohus.rift.generated.resources.alerts_description_location_any_online
import dev.nohus.rift.generated.resources.alerts_description_location_any_undocked
import dev.nohus.rift.generated.resources.alerts_description_location_character
import dev.nohus.rift.generated.resources.alerts_description_location_character_undocked
import dev.nohus.rift.generated.resources.alerts_description_location_prefix
import dev.nohus.rift.generated.resources.alerts_description_location_system
import dev.nohus.rift.generated.resources.alerts_description_message_filter_prefix
import dev.nohus.rift.generated.resources.alerts_description_message_ping_prefix
import dev.nohus.rift.generated.resources.alerts_description_message_sent_suffix
import dev.nohus.rift.generated.resources.alerts_description_minutes
import dev.nohus.rift.generated.resources.alerts_description_module_prefix
import dev.nohus.rift.generated.resources.alerts_description_offline_suffix
import dev.nohus.rift.generated.resources.alerts_description_online_suffix
import dev.nohus.rift.generated.resources.alerts_description_pap_any
import dev.nohus.rift.generated.resources.alerts_description_pap_peacetime
import dev.nohus.rift.generated.resources.alerts_description_pap_strategic
import dev.nohus.rift.generated.resources.alerts_description_pap_suffix
import dev.nohus.rift.generated.resources.alerts_description_pi_extractor
import dev.nohus.rift.generated.resources.alerts_description_pi_idle
import dev.nohus.rift.generated.resources.alerts_description_pi_minutes
import dev.nohus.rift.generated.resources.alerts_description_pi_setup
import dev.nohus.rift.generated.resources.alerts_description_pi_storage
import dev.nohus.rift.generated.resources.alerts_description_ping_received_suffix
import dev.nohus.rift.generated.resources.alerts_description_ping_target_prefix
import dev.nohus.rift.generated.resources.alerts_description_range_between
import dev.nohus.rift.generated.resources.alerts_description_range_exactly
import dev.nohus.rift.generated.resources.alerts_description_range_same_system
import dev.nohus.rift.generated.resources.alerts_description_range_up_to
import dev.nohus.rift.generated.resources.alerts_description_regex_prefix
import dev.nohus.rift.generated.resources.alerts_description_report_alternatives_prefix
import dev.nohus.rift.generated.resources.alerts_description_report_bubbles
import dev.nohus.rift.generated.resources.alerts_description_report_characters
import dev.nohus.rift.generated.resources.alerts_description_report_ess
import dev.nohus.rift.generated.resources.alerts_description_report_gate_camps
import dev.nohus.rift.generated.resources.alerts_description_report_location_prefix
import dev.nohus.rift.generated.resources.alerts_description_report_ships
import dev.nohus.rift.generated.resources.alerts_description_report_skyhooks
import dev.nohus.rift.generated.resources.alerts_description_report_wormholes
import dev.nohus.rift.generated.resources.alerts_description_seconds
import dev.nohus.rift.generated.resources.alerts_description_sender_prefix
import dev.nohus.rift.generated.resources.alerts_description_ship_class
import dev.nohus.rift.generated.resources.alerts_description_ship_class_count
import dev.nohus.rift.generated.resources.alerts_description_specific_character_count
import dev.nohus.rift.generated.resources.alerts_description_specific_colony
import dev.nohus.rift.generated.resources.alerts_description_trigger_prefix
import dev.nohus.rift.generated.resources.alerts_description_unknown_label
import dev.nohus.rift.generated.resources.alerts_list_action_create_alert
import dev.nohus.rift.generated.resources.alerts_list_action_create_group
import dev.nohus.rift.generated.resources.alerts_list_action_delete_alert
import dev.nohus.rift.generated.resources.alerts_list_action_delete_group
import dev.nohus.rift.generated.resources.alerts_list_action_delete_group_keep_alerts
import dev.nohus.rift.generated.resources.alerts_list_action_disable_group
import dev.nohus.rift.generated.resources.alerts_list_action_edit_action
import dev.nohus.rift.generated.resources.alerts_list_action_enable_group
import dev.nohus.rift.generated.resources.alerts_list_action_rename_group
import dev.nohus.rift.generated.resources.alerts_list_action_test_sound
import dev.nohus.rift.generated.resources.alerts_list_character_loading
import dev.nohus.rift.generated.resources.alerts_list_contact_labels_prefix
import dev.nohus.rift.generated.resources.alerts_list_decloak_exceptions_prefix
import dev.nohus.rift.generated.resources.alerts_list_default_group
import dev.nohus.rift.generated.resources.alerts_list_empty
import dev.nohus.rift.generated.resources.alerts_list_fleet_commanders_prefix
import dev.nohus.rift.generated.resources.alerts_list_group_count
import dev.nohus.rift.generated.resources.alerts_list_group_empty
import dev.nohus.rift.generated.resources.alerts_list_group_enabled_count
import dev.nohus.rift.generated.resources.alerts_list_group_enabled_summary
import dev.nohus.rift.generated.resources.alerts_list_group_label
import dev.nohus.rift.generated.resources.alerts_list_groups_description
import dev.nohus.rift.generated.resources.alerts_list_label_owner_prefix
import dev.nohus.rift.generated.resources.alerts_list_monitored_characters_prefix
import dev.nohus.rift.generated.resources.alerts_list_monitored_classes_prefix
import dev.nohus.rift.generated.resources.alerts_list_target_colonies_heading
import dev.nohus.rift.generated.resources.alerts_list_window_title
import dev.nohus.rift.generated.resources.delete
import dev.nohus.rift.generated.resources.editplanicon
import dev.nohus.rift.generated.resources.toggle_off_18
import dev.nohus.rift.generated.resources.toggle_on_18
import dev.nohus.rift.generated.resources.window_loudspeaker_icon
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.planetaryindustry.PlanetaryIndustryRepository.ColonyItem
import dev.nohus.rift.utils.sound.Sound
import dev.nohus.rift.utils.sound.UiSound
import dev.nohus.rift.utils.sound.rememberUiSoundPlayer
import dev.nohus.rift.utils.withColor
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import java.nio.file.Path
import java.time.Duration
import kotlin.io.path.nameWithoutExtension

@Composable
fun AlertsWindow(
    windowState: RiftWindowState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: AlertsViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    RiftWindow(
        title = stringResource(Res.string.alerts_list_window_title),
        icon = Res.drawable.window_loudspeaker_icon,
        state = windowState,
        onCloseClick = onCloseRequest,
    ) {
        AlertsWindowContent(
            state = state,
            onAlertClick = viewModel::onAlertClick,
            onGroupClick = viewModel::onGroupClick,
            onToggleAlert = viewModel::onToggleAlert,
            onGroupChange = viewModel::onGroupChange,
            onTestAlertSound = viewModel::onTestAlertSound,
            onEditAlertAction = viewModel::onEditAlertAction,
            onDeleteAlert = viewModel::onDeleteAlert,
            onCreateAlertClick = viewModel::onCreateAlertClick,
            onCreateGroupClick = viewModel::onCreateGroupClick,
            onGroupRenameClick = viewModel::onGroupRenameClick,
            onGroupDeleteClick = viewModel::onGroupDeleteClick,
            onGroupToggleAlerts = viewModel::onGroupToggleAlerts,
        )

        val isCreateAlertDialogOpen = state.isCreateAlertDialogOpen
        if (isCreateAlertDialogOpen != null) {
            CreateAlertDialog(
                inputModel = isCreateAlertDialogOpen,
                parentWindowState = windowState,
                onDismiss = viewModel::onCloseCreateAlert,
            )
        }
        val isCreateGroupDialogOpen = state.isCreateGroupDialogOpen
        if (isCreateGroupDialogOpen != null) {
            CreateGroupDialog(
                inputModel = isCreateGroupDialogOpen,
                parentWindowState = windowState,
                description = stringResource(Res.string.alerts_list_groups_description),
                onDismiss = viewModel::onCloseCreateGroup,
                onConfirmClick = viewModel::onCreateGroupConfirm,
            )
        }
    }
}

@Composable
private fun AlertsWindowContent(
    state: UiState,
    onAlertClick: (id: String) -> Unit,
    onGroupClick: (name: String?) -> Unit,
    onToggleAlert: (id: String, isEnabled: Boolean) -> Unit,
    onGroupChange: (id: String, group: String?) -> Unit,
    onTestAlertSound: (id: String) -> Unit,
    onEditAlertAction: (id: String) -> Unit,
    onDeleteAlert: (id: String) -> Unit,
    onCreateAlertClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    onGroupRenameClick: (group: String) -> Unit,
    onGroupDeleteClick: (group: String) -> Unit,
    onGroupToggleAlerts: (group: String?) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        if (state.alerts.isNotEmpty()) {
            ScrollbarLazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                val nonEmptyGroups = state.alerts.mapNotNull { it.group }.toSet()
                val emptyGroups = state.groups - nonEmptyGroups
                state.alerts
                    .groupBy { it.group }
                    .let { it + emptyGroups.associateWith { emptyList() } }
                    .entries
                    .sortedWith(compareBy({ it.key == null }, { it.key }))
                    .forEach { (group, alertsInGroup) ->
                        val isExpanded = group !in state.collapsedGroups
                        stickyHeader {
                            val groupName = group ?: stringResource(Res.string.alerts_list_default_group)
                            val total = alertsInGroup.size
                            val enabled = alertsInGroup.count { it.isEnabled }
                            val countText = if (enabled < total) {
                                val enabledText = pluralStringResource(Res.plurals.alerts_list_group_enabled_count, enabled, enabled)
                                pluralStringResource(Res.plurals.alerts_list_group_enabled_summary, total, total, enabledText)
                            } else {
                                pluralStringResource(Res.plurals.alerts_list_group_count, total, total)
                            }
                            val text = buildAnnotatedString {
                                withColor(RiftTheme.colors.textPrimary) {
                                    append(groupName)
                                }
                                append(" - ")
                                append(countText)
                            }
                            AlertGroupHeader(
                                name = text,
                                isEmpty = alertsInGroup.isEmpty(),
                                isExpanded = isExpanded,
                                isDefault = group == null,
                                hasEnabledAlerts = alertsInGroup.any { it.isEnabled },
                                onClick = { onGroupClick(group) },
                                onGroupToggleAlerts = { onGroupToggleAlerts(group) },
                                onGroupRenameClick = { onGroupRenameClick(group!!) },
                                onGroupDeleteClick = { onGroupDeleteClick(group!!) },
                            )
                        }
                        if (isExpanded) {
                            if (group in emptyGroups) {
                                item {
                                    EmptyGroup()
                                }
                            }
                            items(alertsInGroup, key = { it.id }) { alert ->
                                val isExpanded = alert.id == state.expandedAlert
                                AlertItem(
                                    onAlertClick = onAlertClick,
                                    alert = alert,
                                    onToggleAlert = onToggleAlert,
                                    state = state,
                                    isExpanded = isExpanded,
                                    groups = state.groups,
                                    onGroupChange = { onGroupChange(alert.id, it) },
                                    onTestAlertSound = onTestAlertSound,
                                    onEditAlertAction = onEditAlertAction,
                                    onDeleteAlert = onDeleteAlert,
                                )
                            }
                        }
                    }
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(Res.string.alerts_list_empty),
                    style = RiftTheme.typography.headerPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.large),
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier.align(Alignment.End),
        ) {
            if (state.alerts.isNotEmpty()) {
                RiftButton(
                    text = stringResource(Res.string.alerts_list_action_create_group),
                    type = ButtonType.Secondary,
                    cornerCut = ButtonCornerCut.None,
                    onClick = onCreateGroupClick,
                )
            }
            RiftButton(
                text = stringResource(Res.string.alerts_list_action_create_alert),
                onClick = onCreateAlertClick,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.AlertGroupHeader(
    name: AnnotatedString,
    isEmpty: Boolean,
    isExpanded: Boolean,
    isDefault: Boolean,
    hasEnabledAlerts: Boolean,
    onClick: () -> Unit,
    onGroupToggleAlerts: () -> Unit,
    onGroupRenameClick: () -> Unit,
    onGroupDeleteClick: () -> Unit,
) {
    val pointerState = remember { PointerInteractionStateHolder() }
    val player = rememberUiSoundPlayer()
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .pointerInteraction(pointerState, hoverSound = UiSound.EntryHover)
            .background(RiftTheme.colors.backgroundPrimary)
            .fillMaxWidth()
            .animateItem()
            .animateContentSize()
            .pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
            .onClick {
                player.playAccordion(isExpanded)
                onClick()
            },
    ) {
        ExpandChevron(isExpanded = isExpanded)
        Text(
            text = name,
            style = RiftTheme.typography.headerSecondary,
            modifier = Modifier.padding(vertical = Spacing.small),
        )
        Spacer(Modifier.weight(1f))

        val buttonsAlpha by animateFloatAsState(if (pointerState.isHovered) 1f else 0f)
        if (!isEmpty) {
            RiftTooltipArea(
                text = if (hasEnabledAlerts) stringResource(Res.string.alerts_list_action_disable_group) else stringResource(Res.string.alerts_list_action_enable_group),
            ) {
                RiftImageButton(
                    resource = if (hasEnabledAlerts) Res.drawable.toggle_on_18 else Res.drawable.toggle_off_18,
                    size = 18.dp,
                    onClick = onGroupToggleAlerts,
                    modifier = Modifier.alpha(buttonsAlpha),
                )
            }
        }
        if (!isDefault) {
            RiftTooltipArea(
                text = stringResource(Res.string.alerts_list_action_rename_group),
            ) {
                RiftImageButton(
                    resource = Res.drawable.editplanicon,
                    size = 20.dp,
                    onClick = onGroupRenameClick,
                    modifier = Modifier.alpha(buttonsAlpha),
                )
            }
            RiftTooltipArea(
                text = if (isEmpty) stringResource(Res.string.alerts_list_action_delete_group) else stringResource(Res.string.alerts_list_action_delete_group_keep_alerts),
            ) {
                RiftImageButton(
                    resource = Res.drawable.delete,
                    size = 20.dp,
                    onClick = onGroupDeleteClick,
                    modifier = Modifier.alpha(buttonsAlpha),
                )
            }
        }
        Spacer(Modifier.width(Spacing.small))
    }
}

@Composable
private fun LazyItemScope.EmptyGroup() {
    Row(
        modifier = Modifier
            .padding(vertical = Spacing.medium)
            .fillMaxWidth()
            .animateItem()
            .animateContentSize(),
    ) {
        Text(
            text = stringResource(Res.string.alerts_list_group_empty),
            style = RiftTheme.typography.bodySecondary,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.AlertItem(
    onAlertClick: (id: String) -> Unit,
    alert: Alert,
    onToggleAlert: (id: String, isEnabled: Boolean) -> Unit,
    state: UiState,
    isExpanded: Boolean,
    groups: Set<String>,
    onGroupChange: (group: String?) -> Unit,
    onTestAlertSound: (id: String) -> Unit,
    onEditAlertAction: (id: String) -> Unit,
    onDeleteAlert: (id: String) -> Unit,
) {
    val player = rememberUiSoundPlayer()
    val defaultGroup = stringResource(Res.string.alerts_list_default_group)
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
        modifier = Modifier
            .hoverBackground()
            .pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
            .padding(vertical = Spacing.medium)
            .fillMaxWidth()
            .animateItem()
            .animateContentSize()
            .onClick {
                player.playAccordion(isExpanded)
                onAlertClick(alert.id)
            },
    ) {
        val alpha = if (alert.isEnabled) 1f else 0.5f
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = Spacing.small),
        ) {
            RiftCheckbox(
                isChecked = alert.isEnabled,
                onCheckedChange = { onToggleAlert(alert.id, it) },
            )
            val text = getAlertDescription(alert, state.characters, state.sounds, state.labels)

            Text(
                text = text,
                modifier = Modifier
                    .weight(1f)
                    .alpha(alpha)
                    .padding(horizontal = Spacing.medium),
            )
        }
        if (isExpanded) {
            listOfNotNull(
                getSpecificCharactersDetailText(alert),
                getSpecificShipClassesDetailText(alert),
                getSpecificFleetCommandersDetailText(alert),
                getDecloakIgnoredKeywordsDetailText(alert),
                getSpecificColoniesDetailText(alert, state.colonies),
                getLabeledContactsDetailText(alert, state.labels),
            ).forEach {
                Row(
                    modifier = Modifier
                        .padding(horizontal = Spacing.small)
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = it,
                        modifier = Modifier
                            .weight(1f)
                            .alpha(alpha),
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(horizontal = Spacing.medium)
                    .fillMaxWidth(),
            ) {
                RiftDropdownWithLabel(
                    label = stringResource(Res.string.alerts_list_group_label),
                    items = (groups.sorted() + listOf(null)).toList(),
                    selectedItem = alert.group,
                    onItemSelected = onGroupChange,
                    getItemName = { it ?: defaultGroup },
                    maxItems = 3,
                    modifier = Modifier
                        .widthIn(max = 170.dp)
                        .padding(end = Spacing.medium),
                )
                if (alert.actions.any { it is AlertAction.Sound || it is AlertAction.CustomSound }) {
                    RiftButton(
                        text = stringResource(Res.string.alerts_list_action_test_sound),
                        type = ButtonType.Secondary,
                        cornerCut = ButtonCornerCut.None,
                        onClick = { onTestAlertSound(alert.id) },
                        modifier = Modifier.padding(end = Spacing.medium),
                    )
                }
                RiftButton(
                    text = stringResource(Res.string.alerts_list_action_edit_action),
                    type = ButtonType.Secondary,
                    cornerCut = ButtonCornerCut.None,
                    onClick = { onEditAlertAction(alert.id) },
                    modifier = Modifier.padding(end = Spacing.medium),
                )
                RiftButton(
                    text = stringResource(Res.string.alerts_list_action_delete_alert),
                    type = ButtonType.Negative,
                    onClick = { onDeleteAlert(alert.id) },
                )
            }
        }
    }
}

@Composable
private fun getSpecificCharactersDetailText(alert: Alert): AnnotatedString? {
    return if (alert.trigger is AlertTrigger.IntelReported) {
        val specificCharacters = alert.trigger.reportTypes
            .firstOrNull { it is IntelReportType.SpecificCharacters }
        if (specificCharacters != null) {
            val secondary = SpanStyle(color = RiftTheme.colors.textSecondary)
            val primary = SpanStyle(color = RiftTheme.colors.textPrimary)
            val characters = (specificCharacters as IntelReportType.SpecificCharacters).characters
            buildAnnotatedString {
                withStyle(secondary) {
                    append(stringResource(Res.string.alerts_list_monitored_characters_prefix))
                    characters.forEach { character ->
                        withStyle(primary) {
                            append(character)
                        }
                        if (character != characters.last()) {
                            append(", ")
                        }
                    }
                }
            }
        } else {
            null
        }
    } else {
        null
    }
}

@Composable
private fun getSpecificFleetCommandersDetailText(alert: Alert): AnnotatedString? {
    return if (alert.trigger is AlertTrigger.JabberPing && alert.trigger.pingType is JabberPingType.Fleet) {
        val fleetCommanders = alert.trigger.pingType.fleetCommanders
        if (fleetCommanders.isNotEmpty()) {
            val secondary = SpanStyle(color = RiftTheme.colors.textSecondary)
            val primary = SpanStyle(color = RiftTheme.colors.textPrimary)
            buildAnnotatedString {
                withStyle(secondary) {
                    append(stringResource(Res.string.alerts_list_fleet_commanders_prefix))
                    fleetCommanders.forEach { character ->
                        withStyle(primary) {
                            append(character)
                        }
                        if (character != fleetCommanders.last()) {
                            append(", ")
                        }
                    }
                }
            }
        } else {
            null
        }
    } else {
        null
    }
}

@Composable
private fun getDecloakIgnoredKeywordsDetailText(alert: Alert): AnnotatedString? {
    val decloakedTrigger = (alert.trigger as? AlertTrigger.GameAction)?.actionTypes
        ?.filterIsInstance<GameActionType.Decloaked>()?.firstOrNull() ?: return null
    return if (decloakedTrigger.ignoredKeywords.isNotEmpty()) {
        val secondary = SpanStyle(color = RiftTheme.colors.textSecondary)
        val primary = SpanStyle(color = RiftTheme.colors.textPrimary)
        buildAnnotatedString {
            withStyle(secondary) {
                append(stringResource(Res.string.alerts_list_decloak_exceptions_prefix))
                decloakedTrigger.ignoredKeywords.forEach { keyword ->
                    withStyle(primary) {
                        append(keyword)
                    }
                    if (keyword != decloakedTrigger.ignoredKeywords.last()) {
                        append(", ")
                    }
                }
            }
        }
    } else {
        null
    }
}

@Composable
private fun getSpecificColoniesDetailText(alert: Alert, colonies: List<ColonyItem>): AnnotatedString? {
    val colonyIds = (alert.trigger as? AlertTrigger.PlanetaryIndustry)?.coloniesFilter ?: return null
    val secondary = SpanStyle(color = RiftTheme.colors.textSecondary)
    val primary = SpanStyle(color = RiftTheme.colors.textPrimary)
    return buildAnnotatedString {
        withStyle(secondary) {
            append(stringResource(Res.string.alerts_list_target_colonies_heading))
            colonyIds
                .mapNotNull { id ->
                    val colony = colonies.find { it.colony.id == id } ?: return@mapNotNull null
                    colony.colony.characterId to colony
                }.groupBy {
                    it.first
                }.forEach { (_, entries) ->
                    val items = entries.map { it.second }
                    val characterName = items.first().characterName ?: stringResource(Res.string.alerts_list_character_loading)
                    append("\n")
                    withStyle(primary) {
                        append(characterName)
                    }
                    append(": ")
                    items.forEachIndexed { index, colony ->
                        if (index != 0) append(", ")
                        withStyle(primary) {
                            append(colony.colony.planet.name)
                        }
                    }
                }
        }
    }
}

@Composable
private fun getSpecificShipClassesDetailText(alert: Alert): AnnotatedString? {
    return if (alert.trigger is AlertTrigger.IntelReported) {
        val specificClasses = alert.trigger.reportTypes
            .firstOrNull { it is IntelReportType.SpecificShipClasses }
        if (specificClasses != null) {
            val secondary = SpanStyle(color = RiftTheme.colors.textSecondary)
            val primary = SpanStyle(color = RiftTheme.colors.textPrimary)
            val characters = (specificClasses as IntelReportType.SpecificShipClasses).classes
            buildAnnotatedString {
                withStyle(secondary) {
                    append(stringResource(Res.string.alerts_list_monitored_classes_prefix))
                    characters.forEach { character ->
                        withStyle(primary) {
                            append(character)
                        }
                        if (character != characters.last()) {
                            append(", ")
                        }
                    }
                }
            }
        } else {
            null
        }
    } else {
        null
    }
}

@Composable
private fun getLabeledContactsDetailText(alert: Alert, labels: List<Label>): AnnotatedString? {
    return if (alert.trigger is AlertTrigger.IntelReported) {
        val labeledContacts = alert.trigger.reportTypes
            .firstOrNull { it is IntelReportType.LabeledContacts }
        if (labeledContacts != null) {
            val labels = (labeledContacts as IntelReportType.LabeledContacts).labels.mapNotNull { label ->
                labels.firstOrNull { it.owner.id == label.ownerId && it.id == label.id }
            }
            val secondary = SpanStyle(color = RiftTheme.colors.textSecondary)
            val primary = SpanStyle(color = RiftTheme.colors.textPrimary)
            buildAnnotatedString {
                withStyle(secondary) {
                    append(stringResource(Res.string.alerts_list_contact_labels_prefix))
                    labels.forEachIndexed { index, label ->
                        withStyle(primary) {
                            append(label.name)
                        }
                        append(stringResource(Res.string.alerts_list_label_owner_prefix))
                        withStyle(primary) {
                            append(label.owner.name)
                        }
                        if (index != labels.lastIndex) {
                            appendLine()
                        }
                    }
                }
            }
        } else {
            null
        }
    } else {
        null
    }
}

@Composable
internal fun getAlertDescription(
    alert: Alert,
    characters: List<LocalCharacter>,
    sounds: List<Sound>,
    labels: List<Label>,
): AnnotatedString {
    val secondary = SpanStyle(color = RiftTheme.colors.textSecondary)
    val primary = SpanStyle(color = RiftTheme.colors.textPrimary)
    val soundNames = sounds.map { it.id to it.name.resolve() }.toMap()
    return buildAnnotatedString {
        withStyle(secondary) {
            append(stringResource(Res.string.alerts_description_trigger_prefix))
            @Suppress("DEPRECATION")
            when (val trigger = alert.trigger) {
                is AlertTrigger.IntelReported -> {
                    if (trigger.reportTypes.size != 1) {
                        append(stringResource(Res.string.alerts_description_report_alternatives_prefix))
                    }
                    val types = trigger.reportTypes.map { type ->
                        when (type) {
                            IntelReportType.AnyCharacter -> stringResource(Res.string.alerts_description_report_characters)
                            is IntelReportType.SpecificCharacters -> {
                                if (type.characters.size == 1) {
                                    type.characters.single()
                                } else {
                                    pluralStringResource(Res.plurals.alerts_description_specific_character_count, type.characters.size, type.characters.size)
                                }
                            }
                            IntelReportType.AnyShip -> stringResource(Res.string.alerts_description_report_ships)
                            is IntelReportType.SpecificShipClasses -> {
                                if (type.classes.size == 1) {
                                    stringResource(Res.string.alerts_description_ship_class, type.classes.single())
                                } else {
                                    pluralStringResource(Res.plurals.alerts_description_ship_class_count, type.classes.size, type.classes.size)
                                }
                            }
                            is IntelReportType.LabeledContacts -> {
                                if (type.labels.size == 1) {
                                    val label = type.labels.single()
                                    val name = labels.firstOrNull { it.owner.id == label.ownerId && it.id == label.id }?.name ?: stringResource(Res.string.alerts_description_unknown_label)
                                    stringResource(Res.string.alerts_description_labeled_characters, name)
                                } else {
                                    pluralStringResource(Res.plurals.alerts_description_contact_label_count, type.labels.size, type.labels.size)
                                }
                            }
                            IntelReportType.Bubbles -> stringResource(Res.string.alerts_description_report_bubbles)
                            IntelReportType.GateCamp -> stringResource(Res.string.alerts_description_report_gate_camps)
                            IntelReportType.Wormhole -> stringResource(Res.string.alerts_description_report_wormholes)
                            IntelReportType.Ess -> stringResource(Res.string.alerts_description_report_ess)
                            IntelReportType.Skyhook -> stringResource(Res.string.alerts_description_report_skyhooks)
                        }
                    }.joinToString()
                    withStyle(primary) {
                        append(types)
                    }
                    append(stringResource(Res.string.alerts_description_report_location_prefix))
                    val location = when (val location = trigger.reportLocation) {
                        is IntelReportLocation.System -> stringResource(Res.string.alerts_description_location_system, getRangePrefixText(location.jumpsRange), location.systemName)
                        is IntelReportLocation.AnyOwnedCharacter if location.onlyUndocked -> stringResource(Res.string.alerts_description_location_any_undocked, getRangePrefixText(location.jumpsRange))
                        is IntelReportLocation.AnyOwnedCharacter -> stringResource(Res.string.alerts_description_location_any_online, getRangePrefixText(location.jumpsRange))
                        is IntelReportLocation.OwnedCharacter if location.onlyUndocked -> {
                            val character = characters.firstOrNull { it.characterId == location.characterId }?.info?.name ?: location.characterId.toString()
                            stringResource(Res.string.alerts_description_location_character_undocked, getRangePrefixText(location.jumpsRange), character)
                        }
                        is IntelReportLocation.OwnedCharacter -> {
                            val character = characters.firstOrNull { it.characterId == location.characterId }?.info?.name ?: location.characterId.toString()
                            stringResource(Res.string.alerts_description_location_character, getRangePrefixText(location.jumpsRange), character)
                        }
                    }
                    withStyle(primary) {
                        append(location)
                    }
                }
                is AlertTrigger.GameAction -> {
                    trigger.actionTypes.forEachIndexed { index, type ->
                        if (index != 0) append(stringResource(Res.string.alerts_description_condition_separator))
                        when (type) {
                            is GameActionType.InCombat -> {
                                append(stringResource(Res.string.alerts_description_game_character_prefix))
                                withStyle(primary) { append(stringResource(Res.string.alerts_description_game_in_combat)) }
                                if (type.nameContaining != null) {
                                    append(stringResource(Res.string.alerts_description_combat_target_prefix))
                                    withStyle(primary) { append(type.nameContaining) }
                                }
                            }
                            is GameActionType.UnderAttack -> {
                                append(stringResource(Res.string.alerts_description_game_character_prefix))
                                withStyle(primary) { append(stringResource(Res.string.alerts_description_game_under_attack)) }
                                if (type.nameContaining != null) {
                                    append(stringResource(Res.string.alerts_description_sender_prefix))
                                    withStyle(primary) { append(type.nameContaining) }
                                }
                            }
                            is GameActionType.Attacking -> {
                                append(stringResource(Res.string.alerts_description_game_character_prefix))
                                withStyle(primary) { append(stringResource(Res.string.alerts_description_game_attacking)) }
                                if (type.nameContaining != null) {
                                    append(stringResource(Res.string.alerts_description_attack_target_prefix))
                                    withStyle(primary) { append(type.nameContaining) }
                                }
                            }
                            GameActionType.BeingWarpScrambled -> {
                                append(stringResource(Res.string.alerts_description_game_character_prefix))
                                withStyle(primary) { append(stringResource(Res.string.alerts_description_game_warp_scrambled)) }
                            }
                            is GameActionType.Decloaked -> {
                                append(stringResource(Res.string.alerts_description_game_character_prefix))
                                withStyle(primary) { append(stringResource(Res.string.alerts_description_game_decloaked)) }
                                if (type.ignoredKeywords.isNotEmpty()) {
                                    append(stringResource(Res.string.alerts_description_decloak_exceptions_suffix))
                                }
                            }
                            is GameActionType.CombatStopped -> {
                                append(stringResource(Res.string.alerts_description_game_character_prefix))
                                withStyle(primary) { append(stringResource(Res.string.alerts_description_game_combat_stopped)) }
                                if (type.nameContaining != null) {
                                    append(stringResource(Res.string.alerts_description_combat_target_prefix))
                                    withStyle(primary) { append(type.nameContaining) }
                                }
                                append(stringResource(Res.string.alerts_description_duration_prefix))
                                val minutes = type.durationSeconds / 60
                                withStyle(primary) {
                                    if (minutes == 1) {
                                        append(pluralStringResource(Res.plurals.alerts_description_minutes, minutes, minutes))
                                    } else if (minutes > 1) {
                                        append(pluralStringResource(Res.plurals.alerts_description_minutes, minutes, minutes))
                                    } else {
                                        append(pluralStringResource(Res.plurals.alerts_description_seconds, type.durationSeconds, type.durationSeconds))
                                    }
                                }
                            }
                            GameActionType.RanOutOfCharges -> {
                                append(stringResource(Res.string.alerts_description_module_prefix))
                                withStyle(primary) { append(stringResource(Res.string.alerts_description_game_charges_depleted)) }
                            }
                            GameActionType.InvitedToConversation -> {
                                append(stringResource(Res.string.alerts_description_game_character_prefix))
                                withStyle(primary) { append(stringResource(Res.string.alerts_description_game_conversation)) }
                            }
                            GameActionType.AsteroidDepleted -> {
                                append(stringResource(Res.string.alerts_description_asteroid_prefix))
                                withStyle(primary) { append(stringResource(Res.string.alerts_description_game_asteroid_depleted)) }
                            }

                            is GameActionType.Custom -> {
                                append(stringResource(Res.string.alerts_description_custom_game_prefix))
                                if (type.isRegex) append(stringResource(Res.string.alerts_description_regex_prefix))
                                withStyle(primary) { append(type.messageContaining) }
                            }
                        }
                    }
                }
                is AlertTrigger.PlanetaryIndustry -> {
                    append(stringResource(Res.string.alerts_description_colony_prefix))
                    val coloniesFilter = when {
                        trigger.coloniesFilter == null -> stringResource(Res.string.alerts_description_any_colony)
                        trigger.coloniesFilter.size == 1 -> stringResource(Res.string.alerts_description_specific_colony)
                        else -> pluralStringResource(Res.plurals.alerts_description_colony_count, trigger.coloniesFilter.size, trigger.coloniesFilter.size)
                    }
                    withStyle(primary) {
                        append(coloniesFilter)
                    }
                    append(" ")
                    trigger.eventTypes.forEachIndexed { index, type ->
                        if (index != 0) append(stringResource(Res.string.alerts_description_condition_separator))
                        val text = when (type) {
                            PiEventType.ExtractorInactive -> stringResource(Res.string.alerts_description_pi_extractor)
                            PiEventType.Idle -> stringResource(Res.string.alerts_description_pi_idle)
                            PiEventType.NotSetup -> stringResource(Res.string.alerts_description_pi_setup)
                            PiEventType.StorageFull -> stringResource(Res.string.alerts_description_pi_storage)
                        }
                        withStyle(primary) {
                            append(text)
                        }
                    }
                    if (trigger.alertBeforeSeconds > 0) {
                        val duration = Duration.ofSeconds(trigger.alertBeforeSeconds.toLong())
                        val text = when {
                            duration.toHours() >= 1 -> pluralStringResource(Res.plurals.alerts_description_hours, duration.toHours().toInt(), duration.toHours())
                            else -> pluralStringResource(Res.plurals.alerts_description_pi_minutes, duration.toMinutes().toInt(), duration.toMinutes())
                        }
                        append(stringResource(Res.string.alerts_description_location_prefix))
                        withStyle(primary) {
                            append(text)
                        }
                    }
                }
                is AlertTrigger.EveNotification -> {
                    append(stringResource(Res.string.alerts_description_eve_notification_prefix))
                    if (trigger.characterId != null) {
                        val characterName = characters.firstOrNull { it.characterId == trigger.characterId }
                            ?.info?.name ?: trigger.characterId.toString()
                        withStyle(primary) {
                            append(characterName)
                        }
                        when (trigger.characterStatus) {
                            EveNotificationCharacterStatus.Online -> append(stringResource(Res.string.alerts_description_online_suffix))
                            EveNotificationCharacterStatus.Offline -> append(stringResource(Res.string.alerts_description_offline_suffix))
                            EveNotificationCharacterStatus.Any -> {}
                        }
                    } else {
                        val character = when (trigger.characterStatus) {
                            EveNotificationCharacterStatus.Online -> stringResource(Res.string.alerts_description_any_online_character)
                            EveNotificationCharacterStatus.Offline -> stringResource(Res.string.alerts_description_any_offline_character)
                            EveNotificationCharacterStatus.Any -> stringResource(Res.string.alerts_description_any_character)
                        }
                        withStyle(primary) {
                            append(character)
                        }
                    }
                }
                is AlertTrigger.ChatMessage -> {
                    append(stringResource(Res.string.alerts_description_chat_message_prefix))
                    if (trigger.messageContaining != null) {
                        append(stringResource(Res.string.alerts_description_message_filter_prefix))
                        if (trigger.isRegex) {
                            append(stringResource(Res.string.alerts_description_regex_prefix))
                        }
                        withStyle(primary) {
                            append(trigger.messageContaining)
                        }
                    }
                    append(stringResource(Res.string.alerts_description_message_sent_suffix))
                    if (trigger.sender != null) {
                        append(stringResource(Res.string.alerts_description_sender_prefix))
                        withStyle(primary) {
                            append(trigger.sender)
                        }
                    }
                    if (trigger.isExcludingSelf) {
                        append(stringResource(Res.string.alerts_description_exclude_self_suffix))
                    }
                    append(stringResource(Res.string.alerts_description_location_prefix))
                    val channel = when (val channel = trigger.channel) {
                        ChatMessageChannel.Any -> stringResource(Res.string.alerts_description_any_channel)
                        is ChatMessageChannel.Channel -> channel.name
                    }
                    withStyle(primary) {
                        append(channel)
                    }
                }
                is AlertTrigger.JabberPing -> {
                    @Suppress("DEPRECATION")
                    when (trigger.pingType) {
                        JabberPingType.Message -> {}
                        is JabberPingType.Message2 -> {
                            append(stringResource(Res.string.alerts_description_message_ping_prefix))
                            if (trigger.pingType.target != null) {
                                append(stringResource(Res.string.alerts_description_ping_target_prefix))
                                withStyle(primary) {
                                    append(trigger.pingType.target)
                                }
                                append(" ")
                            }
                            append(stringResource(Res.string.alerts_description_ping_received_suffix))
                        }
                        is JabberPingType.Fleet -> {
                            append(stringResource(Res.string.alerts_description_fleet_ping_prefix))
                            if (trigger.pingType.target != null) {
                                append(stringResource(Res.string.alerts_description_ping_target_prefix))
                                withStyle(primary) {
                                    append(trigger.pingType.target)
                                }
                                append(" ")
                            }
                            append(stringResource(Res.string.alerts_description_ping_received_suffix))
                            if (trigger.pingType.fleetCommanders.isNotEmpty()) {
                                append(stringResource(Res.string.alerts_description_fleet_filter_prefix))
                                if (trigger.pingType.fleetCommanders.size == 1) {
                                    withStyle(primary) {
                                        append(trigger.pingType.fleetCommanders.single())
                                        append(stringResource(Res.string.alerts_description_fleet_commander_suffix))
                                    }
                                } else {
                                    withStyle(primary) {
                                        append(pluralStringResource(Res.plurals.alerts_description_fleet_commander_count, trigger.pingType.fleetCommanders.size, trigger.pingType.fleetCommanders.size))
                                    }
                                }
                            }
                            if (trigger.pingType.formupSystem != null) {
                                append(stringResource(Res.string.alerts_description_formup_prefix))
                                withStyle(primary) {
                                    append(trigger.pingType.formupSystem)
                                }
                            }
                            if (trigger.pingType.papType != PapType.Any) {
                                append(stringResource(Res.string.alerts_description_fleet_filter_prefix))
                                withStyle(primary) {
                                    val type = when (trigger.pingType.papType) {
                                        PapType.Any -> stringResource(Res.string.alerts_description_pap_any)
                                        PapType.Peacetime -> stringResource(Res.string.alerts_description_pap_peacetime)
                                        PapType.Strategic -> stringResource(Res.string.alerts_description_pap_strategic)
                                    }
                                    append(type)
                                }
                                append(stringResource(Res.string.alerts_description_pap_suffix))
                            }
                            if (trigger.pingType.doctrineContaining != null) {
                                append(stringResource(Res.string.alerts_description_doctrine_prefix))
                                withStyle(primary) {
                                    append(trigger.pingType.doctrineContaining)
                                }
                            }
                        }
                    }
                }
                is AlertTrigger.JabberMessage -> {
                    append(stringResource(Res.string.alerts_description_jabber_message_prefix))
                    if (trigger.messageContaining != null) {
                        append(stringResource(Res.string.alerts_description_message_filter_prefix))
                        if (trigger.isRegex) {
                            append(stringResource(Res.string.alerts_description_regex_prefix))
                        }
                        withStyle(primary) {
                            append(trigger.messageContaining)
                        }
                    }
                    append(stringResource(Res.string.alerts_description_message_sent_suffix))
                    if (trigger.sender != null) {
                        append(stringResource(Res.string.alerts_description_sender_prefix))
                        withStyle(primary) {
                            append(trigger.sender)
                        }
                    }
                    append(stringResource(Res.string.alerts_description_location_prefix))
                    val channel = when (val channel = trigger.channel) {
                        JabberMessageChannel.Any -> stringResource(Res.string.alerts_description_any_jabber_chat)
                        is JabberMessageChannel.Channel -> channel.name
                        JabberMessageChannel.DirectMessage -> stringResource(Res.string.alerts_description_direct_message)
                    }
                    withStyle(primary) {
                        append(channel)
                    }
                }
                is AlertTrigger.NoChannelActivity -> {}
            }
            append(stringResource(Res.string.alerts_description_actions_prefix))
            val actions = alert.actions.map { action ->
                when (action) {
                    AlertAction.RiftNotification -> stringResource(Res.string.alerts_description_action_rift)
                    AlertAction.SystemNotification -> stringResource(Res.string.alerts_description_action_system)
                    AlertAction.PushNotification -> stringResource(Res.string.alerts_description_action_push)
                    is AlertAction.Sound -> stringResource(Res.string.alerts_description_action_builtin_sound, soundNames[action.id] ?: "?")
                    is AlertAction.CustomSound -> stringResource(Res.string.alerts_description_action_custom_sound, Path.of(action.path).nameWithoutExtension)
                    AlertAction.ShowPing -> stringResource(Res.string.alerts_description_action_ping)
                    AlertAction.ShowJabberMessage -> stringResource(Res.string.alerts_description_action_message)
                    AlertAction.ShowColonies -> stringResource(Res.string.alerts_description_action_colonies)
                }
            }.joinToString()
            withStyle(primary) {
                append(actions)
            }
            if (alert.cooldownSeconds != 0) {
                append(stringResource(Res.string.alerts_description_cooldown_prefix))
                val text = when (val minutes = alert.cooldownSeconds / 60) {
                    0 -> pluralStringResource(Res.plurals.alerts_description_seconds, alert.cooldownSeconds, alert.cooldownSeconds)
                    1 -> pluralStringResource(Res.plurals.alerts_description_minutes, 1, 1)
                    else -> pluralStringResource(Res.plurals.alerts_description_minutes, minutes, minutes)
                }
                withStyle(primary) {
                    append(text)
                }
            }
        }
    }
}

@Composable
private fun getRangePrefixText(range: JumpRange): String {
    val [min, max] = range.min to range.max
    return if (min == 0 && max == 0) {
        stringResource(Res.string.alerts_description_range_same_system)
    } else if (min == 0) {
        pluralStringResource(Res.plurals.alerts_description_range_up_to, if (max > 1) max else 1, max)
    } else if (min == max) {
        pluralStringResource(Res.plurals.alerts_description_range_exactly, if (max > 1) max else 1, max)
    } else {
        pluralStringResource(Res.plurals.alerts_description_range_between, if (max > 1) max else 1, min, max)
    }
}
