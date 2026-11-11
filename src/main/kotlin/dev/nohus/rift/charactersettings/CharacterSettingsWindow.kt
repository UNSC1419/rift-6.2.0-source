package dev.nohus.rift.charactersettings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.rememberWindowState
import dev.nohus.rift.charactersettings.CharacterSettingsViewModel.CharacterItem
import dev.nohus.rift.charactersettings.CharacterSettingsViewModel.CopySetting
import dev.nohus.rift.charactersettings.CharacterSettingsViewModel.CopyingState
import dev.nohus.rift.charactersettings.CharacterSettingsViewModel.UiState
import dev.nohus.rift.charactersettings.compose.ChatChannels
import dev.nohus.rift.charactersettings.compose.EveResourceIcon
import dev.nohus.rift.charactersettings.compose.NeocomButtonDefinitions
import dev.nohus.rift.charactersettings.compose.NeocomButtons
import dev.nohus.rift.charactersettings.compose.ProbeFormations
import dev.nohus.rift.charactersettings.compose.ShipState
import dev.nohus.rift.charactersettings.compose.ShipStateToggle
import dev.nohus.rift.charactersettings.compose.Watchlist
import dev.nohus.rift.charactersettings.compose.WindowLayoutPreview
import dev.nohus.rift.charactersettings.io.ReadAccountSettingsUseCase
import dev.nohus.rift.charactersettings.io.ReadCharacterSettingsUseCase.NeocomButton
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.ContextMenuItem
import dev.nohus.rift.compose.RequirementIcon
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftCheckboxWithLabel
import dev.nohus.rift.compose.RiftContextMenuArea
import dev.nohus.rift.compose.RiftDialog
import dev.nohus.rift.compose.RiftDropdown
import dev.nohus.rift.compose.RiftMessageDialog
import dev.nohus.rift.compose.RiftSideNavigation
import dev.nohus.rift.compose.RiftSideNavigationHeader
import dev.nohus.rift.compose.RiftSideNavigationItem
import dev.nohus.rift.compose.RiftSlider
import dev.nohus.rift.compose.RiftTabBar
import dev.nohus.rift.compose.RiftTextField
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.RiftWarningBanner
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.Tab
import dev.nohus.rift.compose.getNow
import dev.nohus.rift.compose.getRelativeTimeText
import dev.nohus.rift.compose.text.parseEveFormattedText
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.dynamicportraits.DynamicCharacterPortraitParallax
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.character_settings_account_assign_button
import dev.nohus.rift.generated.resources.character_settings_account_assignment_description
import dev.nohus.rift.generated.resources.character_settings_account_character_limit_warning
import dev.nohus.rift.generated.resources.character_settings_account_label
import dev.nohus.rift.generated.resources.character_settings_account_reassign_button
import dev.nohus.rift.generated.resources.character_settings_account_reassignment_tooltip
import dev.nohus.rift.generated.resources.character_settings_account_unassigned_warning
import dev.nohus.rift.generated.resources.character_settings_add_window_button
import dev.nohus.rift.generated.resources.character_settings_backup_create_button
import dev.nohus.rift.generated.resources.character_settings_backup_create_dialog_title
import dev.nohus.rift.generated.resources.character_settings_backup_create_tooltip
import dev.nohus.rift.generated.resources.character_settings_backup_created_time
import dev.nohus.rift.generated.resources.character_settings_backup_delete_button
import dev.nohus.rift.generated.resources.character_settings_backup_delete_confirmation
import dev.nohus.rift.generated.resources.character_settings_backup_delete_dialog_title
import dev.nohus.rift.generated.resources.character_settings_backup_delete_files_description
import dev.nohus.rift.generated.resources.character_settings_backup_name_description
import dev.nohus.rift.generated.resources.character_settings_backup_name_placeholder
import dev.nohus.rift.generated.resources.character_settings_backup_online_tooltip
import dev.nohus.rift.generated.resources.character_settings_backup_unsaved_changes_tooltip
import dev.nohus.rift.generated.resources.character_settings_backups_empty_description
import dev.nohus.rift.generated.resources.character_settings_backups_heading
import dev.nohus.rift.generated.resources.character_settings_cancel_button
import dev.nohus.rift.generated.resources.character_settings_character_load_failure
import dev.nohus.rift.generated.resources.character_settings_character_settings_missing_tooltip
import dev.nohus.rift.generated.resources.character_settings_characters_empty_description
import dev.nohus.rift.generated.resources.character_settings_chat_channels_description
import dev.nohus.rift.generated.resources.character_settings_chat_channels_tab
import dev.nohus.rift.generated.resources.character_settings_copy_affected_characters_message
import dev.nohus.rift.generated.resources.character_settings_copy_all_settings_label
import dev.nohus.rift.generated.resources.character_settings_copy_all_settings_summary
import dev.nohus.rift.generated.resources.character_settings_copy_all_settings_tooltip
import dev.nohus.rift.generated.resources.character_settings_copy_button
import dev.nohus.rift.generated.resources.character_settings_copy_cancel_button
import dev.nohus.rift.generated.resources.character_settings_copy_characters_confirm_button
import dev.nohus.rift.generated.resources.character_settings_copy_destination_description
import dev.nohus.rift.generated.resources.character_settings_copy_destinations_message
import dev.nohus.rift.generated.resources.character_settings_copy_online_tooltip
import dev.nohus.rift.generated.resources.character_settings_copy_selected_settings_message
import dev.nohus.rift.generated.resources.character_settings_copy_settings_confirm_button
import dev.nohus.rift.generated.resources.character_settings_copy_settings_description
import dev.nohus.rift.generated.resources.character_settings_copy_source_message
import dev.nohus.rift.generated.resources.character_settings_copy_tooltip
import dev.nohus.rift.generated.resources.character_settings_copy_unsaved_changes_tooltip
import dev.nohus.rift.generated.resources.character_settings_drones_visibility_label
import dev.nohus.rift.generated.resources.character_settings_drones_visibility_tooltip
import dev.nohus.rift.generated.resources.character_settings_fleet_visibility_label
import dev.nohus.rift.generated.resources.character_settings_fleet_visibility_tooltip
import dev.nohus.rift.generated.resources.character_settings_last_login_message
import dev.nohus.rift.generated.resources.character_settings_launcher_profile_label
import dev.nohus.rift.generated.resources.character_settings_launcher_profiles_empty_description
import dev.nohus.rift.generated.resources.character_settings_minimized_visibility_label
import dev.nohus.rift.generated.resources.character_settings_minimized_visibility_tooltip
import dev.nohus.rift.generated.resources.character_settings_neocom_add_button
import dev.nohus.rift.generated.resources.character_settings_neocom_description
import dev.nohus.rift.generated.resources.character_settings_neocom_tab
import dev.nohus.rift.generated.resources.character_settings_neocom_width_prefix
import dev.nohus.rift.generated.resources.character_settings_online_warning
import dev.nohus.rift.generated.resources.character_settings_paste_button
import dev.nohus.rift.generated.resources.character_settings_probe_formations_tab
import dev.nohus.rift.generated.resources.character_settings_profile_settings_empty_description
import dev.nohus.rift.generated.resources.character_settings_resolution_label
import dev.nohus.rift.generated.resources.character_settings_revert_button
import dev.nohus.rift.generated.resources.character_settings_save_button
import dev.nohus.rift.generated.resources.character_settings_save_online_tooltip
import dev.nohus.rift.generated.resources.character_settings_save_tooltip
import dev.nohus.rift.generated.resources.character_settings_settings_files_heading
import dev.nohus.rift.generated.resources.character_settings_shared_cache_warning
import dev.nohus.rift.generated.resources.character_settings_ui_scale_label
import dev.nohus.rift.generated.resources.character_settings_ui_scale_tooltip
import dev.nohus.rift.generated.resources.character_settings_unassigned_accounts_heading
import dev.nohus.rift.generated.resources.character_settings_watchlist_tab
import dev.nohus.rift.generated.resources.character_settings_window_layout_description
import dev.nohus.rift.generated.resources.character_settings_window_layout_tab
import dev.nohus.rift.generated.resources.character_settings_window_title
import dev.nohus.rift.generated.resources.character_settings_window_visibility_label
import dev.nohus.rift.generated.resources.copy_16px
import dev.nohus.rift.generated.resources.recall_drones_16px
import dev.nohus.rift.generated.resources.window_character_settings
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.utils.withStyle
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jsoup.nodes.Entities
import java.time.ZoneId
import kotlin.math.roundToInt

@Composable
fun CharacterSettingsWindow(
    windowState: RiftWindowState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: CharacterSettingsViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    RiftWindow(
        title = stringResource(Res.string.character_settings_window_title),
        icon = Res.drawable.window_character_settings,
        state = windowState,
        onCloseClick = onCloseRequest,
        withContentPadding = false,
    ) {
        CharacterSettingsWindowContent(
            state = state,
            onCharacterSelected = viewModel::onCharacterSelected,
            onAssignAccount = viewModel::onAssignAccount,
            onCopySource = viewModel::onCopySourceClick,
            onProfileSelected = viewModel::onProfileSelected,
            onCopyDestination = viewModel::onCopyDestinationClick,
            onCopySettingChanged = viewModel::onCopySettingChanged,
            onCopySettingsSelected = viewModel::onCopySettingsSelected,
            onCopyConfirm = viewModel::onCopySettingsConfirmClick,
            onCancelCopy = viewModel::onCancelCopy,
            onWatchlistCharacterNameChanged = viewModel::onWatchlistCharacterNameChanged,
            onWatchlistUpdated = viewModel::onWatchlistUpdated,
            onWatchlistCharacterRemoved = viewModel::onWatchlistCharacterRemoved,
            onNeocomButtonColorChanged = viewModel::onNeocomButtonColorChanged,
            onNeocomButtonsReordered = viewModel::onNeocomButtonsReordered,
            onNeocomButtonAdded = viewModel::onNeocomButtonAdded,
            onNeocomButtonRemoved = viewModel::onNeocomButtonRemoved,
            onNeocomWidthChanged = viewModel::onNeocomWidthChanged,
            onWindowBoundsChanged = viewModel::onWindowBoundsChanged,
            onWindowMinimizedChanged = viewModel::onWindowMinimizedChanged,
            onWindowOpened = viewModel::onWindowOpened,
            onWindowClosed = viewModel::onWindowClosed,
            onShipUiOffsetChanged = viewModel::onShipUiOffsetChanged,
            onShipUiVerticalPositionChanged = viewModel::onShipUiVerticalPositionChanged,
            onTargetOriginChanged = viewModel::onTargetOriginChanged,
            onTargetsAlignmentChanged = viewModel::onTargetsAlignmentChanged,
            onChatChannelsChanged = viewModel::onChatChannelsChanged,
            onProbeFormationsChanged = viewModel::onProbeFormationsChanged,
            onRevertChanges = viewModel::onRevertChanges,
            onSaveChanges = viewModel::onSaveChanges,
            onBackupSettings = viewModel::onBackupSettingsClick,
            onDeleteBackup = viewModel::onDeleteBackupClick,
        )

        state.backupDialogCharacterId?.let {
            BackupNameDialog(
                parentWindowState = windowState,
                onDismiss = viewModel::onBackupDialogDismissed,
                onConfirm = viewModel::onCreateBackup,
            )
        }

        state.deleteBackupDialogCharacterId?.let { settingsId ->
            state.characters.firstOrNull { it.settingsId == settingsId }?.let { backup ->
                DeleteBackupDialog(
                    parentWindowState = windowState,
                    backup = backup,
                    onDismiss = viewModel::onDeleteBackupDialogDismissed,
                    onConfirm = viewModel::onDeleteBackupConfirm,
                )
            }
        }

        state.dialogMessage?.let {
            RiftMessageDialog(
                dialog = it,
                parentWindowState = windowState,
                onDismiss = viewModel::onCloseDialogMessage,
            )
        }
    }
}

@Composable
private fun WindowScope.CharacterSettingsWindowContent(
    state: UiState,
    onCharacterSelected: (Int) -> Unit,
    onAssignAccount: (characterId: Int, accountId: Int) -> Unit,
    onCopySource: (Int) -> Unit,
    onProfileSelected: (Int, String) -> Unit,
    onCopyDestination: (Int) -> Unit,
    onCopySettingChanged: (CopySetting?, Boolean) -> Unit,
    onCopySettingsSelected: () -> Unit,
    onCopyConfirm: () -> Unit,
    onCancelCopy: () -> Unit,
    onWatchlistCharacterNameChanged: (String) -> Unit,
    onWatchlistUpdated: (Int, Color?) -> Unit,
    onWatchlistCharacterRemoved: (Int) -> Unit,
    onNeocomButtonColorChanged: (String, Int?) -> Unit,
    onNeocomButtonsReordered: (String?, Int, Int) -> Unit,
    onNeocomButtonAdded: (String) -> Unit,
    onNeocomButtonRemoved: (String) -> Unit,
    onNeocomWidthChanged: (Int) -> Unit,
    onWindowBoundsChanged: (String, Int, Int, Int, Int, Pair<Int, Int>) -> Unit,
    onWindowMinimizedChanged: (String) -> Unit,
    onWindowOpened: (String, Pair<Int, Int>) -> Unit,
    onWindowClosed: (String) -> Unit,
    onShipUiOffsetChanged: (Float) -> Unit,
    onShipUiVerticalPositionChanged: () -> Unit,
    onTargetOriginChanged: (Pair<Float, Float>) -> Unit,
    onTargetsAlignmentChanged: () -> Unit,
    onChatChannelsChanged: (Map<String, String>) -> Unit,
    onProbeFormationsChanged: (List<ReadAccountSettingsUseCase.ProbeFormation>) -> Unit,
    onRevertChanges: (Int) -> Unit,
    onSaveChanges: (Int) -> Unit,
    onBackupSettings: (Int) -> Unit,
    onDeleteBackup: (Int) -> Unit,
) {
    Row(Modifier.fillMaxSize()) {
        SideNavigation(state, onCharacterSelected)
        Column {
            CopyControls(
                state = state,
                onCopySettingChanged = onCopySettingChanged,
                onCopySettingsSelected = onCopySettingsSelected,
                onCopyConfirm = onCopyConfirm,
                onCancelCopy = onCancelCopy,
            )
            state.selectedCharacterId
                ?.let { characterId -> state.characters.firstOrNull { it.settingsId == characterId } }
                ?.let { character ->
                    CharacterPage(
                        state = state,
                        character = character,
                        onAssignAccount = onAssignAccount,
                        onProfileSelected = onProfileSelected,
                        onCopySource = onCopySource,
                        onCopyDestination = onCopyDestination,
                        watchlistCharacterName = state.watchlistCharacterName,
                        watchlistCharacterId = state.watchlistCharacterId,
                        isWatchlistCharacterLookupComplete = state.isWatchlistCharacterLookupComplete,
                        onWatchlistCharacterNameChanged = onWatchlistCharacterNameChanged,
                        onWatchlistUpdated = onWatchlistUpdated,
                        onWatchlistCharacterRemoved = onWatchlistCharacterRemoved,
                        onNeocomButtonColorChanged = onNeocomButtonColorChanged,
                        onNeocomButtonsReordered = onNeocomButtonsReordered,
                        onNeocomButtonAdded = onNeocomButtonAdded,
                        onNeocomButtonRemoved = onNeocomButtonRemoved,
                        onNeocomWidthChanged = onNeocomWidthChanged,
                        onWindowBoundsChanged = onWindowBoundsChanged,
                        onWindowMinimizedChanged = onWindowMinimizedChanged,
                        onWindowOpened = onWindowOpened,
                        onWindowClosed = onWindowClosed,
                        onShipUiOffsetChanged = onShipUiOffsetChanged,
                        onShipUiVerticalPositionChanged = onShipUiVerticalPositionChanged,
                        onTargetOriginChanged = onTargetOriginChanged,
                        onTargetsAlignmentChanged = onTargetsAlignmentChanged,
                        onChatChannelsChanged = onChatChannelsChanged,
                        onProbeFormationsChanged = onProbeFormationsChanged,
                        onRevertChanges = onRevertChanges,
                        onSaveChanges = onSaveChanges,
                        onBackupSettings = onBackupSettings,
                        onDeleteBackup = onDeleteBackup,
                    )
                } ?: EmptyCharacterPage()
        }
    }
}

@Composable
private fun SideNavigation(
    state: UiState,
    onCharacterSelected: (Int) -> Unit,
) {
    RiftSideNavigation(
        footer = {},
    ) {
        state.accounts.sortedByDescending { it.lastModified }.forEach { account ->
            val characters = state.characters
                .filter { it.backupName == null && it.accountId == account.id }
                .sortedBy { it.characterId }
            if (characters.isNotEmpty()) {
                item {
                    RiftTooltipArea(
                        tooltip = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(Spacing.small),
                                modifier = Modifier
                                    .widthIn(max = 400.dp)
                                    .padding(Spacing.large),
                            ) {
                                val now = getNow()
                                Text(
                                    text = parseEveFormattedText(stringResource(Res.string.character_settings_last_login_message, Entities.escape(getRelativeTimeText(account.lastModified, ZoneId.systemDefault(), now).resolve())), decodeEntities = true),
                                    style = RiftTheme.typography.bodySecondary,
                                )
                                Text(
                                    text = stringResource(Res.string.character_settings_settings_files_heading),
                                    style = RiftTheme.typography.bodySecondary,
                                )
                                account.paths.values.forEach { path ->
                                    Text(
                                        text = path.toString(),
                                        style = RiftTheme.typography.detailPrimary,
                                    )
                                }
                            }
                        },
                    ) {
                        RiftSideNavigationHeader(stringResource(Res.string.character_settings_account_label, account.id))
                    }
                }
                characters.forEach { character ->
                    characterItem(character, state.selectedCharacterId, onCharacterSelected)
                }
            }
        }

        val unassigned = state.characters.filter { it.backupName == null && it.accountId == null }.sortedBy { it.characterId }
        if (unassigned.isNotEmpty()) {
            item {
                RiftSideNavigationHeader(stringResource(Res.string.character_settings_unassigned_accounts_heading))
            }
            unassigned.forEach {
                characterItem(it, state.selectedCharacterId, onCharacterSelected)
            }
        }

        item {
            RiftSideNavigationHeader(stringResource(Res.string.character_settings_backups_heading))
        }
        val backups = state.characters
            .filter { it.backupName != null }
            .sortedByDescending { it.backupTimestamp }
        if (backups.isEmpty()) {
            item {
                RiftSideNavigationItem(
                    text = stringResource(Res.string.character_settings_backups_empty_description),
                    count = null,
                    icon = null,
                    isSelected = false,
                    onClick = {},
                )
            }
        } else {
            backups.forEach { characterItem(it, state.selectedCharacterId, onCharacterSelected) }
        }
    }
}

private fun LazyListScope.characterItem(
    character: CharacterItem,
    selectedCharacterId: Int?,
    onCharacterSelected: (Int) -> Unit,
) {
    item {
        RiftSideNavigationItem(
            text = if (character.backupName != null) {
                "${character.info?.name ?: character.characterId} - ${character.backupName}"
            } else {
                character.info?.name ?: stringResource(Res.string.character_settings_character_load_failure)
            },
            count = null,
            icon = {
                DynamicCharacterPortraitParallax(
                    characterId = character.characterId,
                    size = 32.dp,
                    enterTimestamp = null,
                    pointerInteractionStateHolder = null,
                )
            },
            isSelected = selectedCharacterId == character.settingsId,
            onClick = { onCharacterSelected(character.settingsId) },
        )
    }
}

@Composable
private fun WindowScope.CharacterPage(
    state: UiState,
    character: CharacterItem,
    onAssignAccount: (characterId: Int, accountId: Int) -> Unit,
    onProfileSelected: (Int, String) -> Unit,
    onCopySource: (Int) -> Unit,
    onCopyDestination: (Int) -> Unit,
    watchlistCharacterName: String,
    watchlistCharacterId: Int?,
    isWatchlistCharacterLookupComplete: Boolean,
    onWatchlistCharacterNameChanged: (String) -> Unit,
    onWatchlistUpdated: (Int, Color?) -> Unit,
    onWatchlistCharacterRemoved: (Int) -> Unit,
    onNeocomButtonColorChanged: (String, Int?) -> Unit,
    onNeocomButtonsReordered: (String?, Int, Int) -> Unit,
    onNeocomButtonAdded: (String) -> Unit,
    onNeocomButtonRemoved: (String) -> Unit,
    onNeocomWidthChanged: (Int) -> Unit,
    onWindowBoundsChanged: (String, Int, Int, Int, Int, Pair<Int, Int>) -> Unit,
    onWindowMinimizedChanged: (String) -> Unit,
    onWindowOpened: (String, Pair<Int, Int>) -> Unit,
    onWindowClosed: (String) -> Unit,
    onShipUiOffsetChanged: (Float) -> Unit,
    onShipUiVerticalPositionChanged: () -> Unit,
    onTargetOriginChanged: (Pair<Float, Float>) -> Unit,
    onTargetsAlignmentChanged: () -> Unit,
    onChatChannelsChanged: (Map<String, String>) -> Unit,
    onProbeFormationsChanged: (List<ReadAccountSettingsUseCase.ProbeFormation>) -> Unit,
    onRevertChanges: (Int) -> Unit,
    onSaveChanges: (Int) -> Unit,
    onBackupSettings: (Int) -> Unit,
    onDeleteBackup: (Int) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = Modifier
            .fillMaxSize()
            .padding(start = Spacing.large, end = Spacing.large, bottom = Spacing.large),
    ) {
        if (state.isOnline) {
            RiftWarningBanner(stringResource(Res.string.character_settings_online_warning))
        }
        if (
            character.backupName == null &&
            character.accountId != null &&
            state.characters.count { it.backupName == null && it.accountId == character.accountId } > 3
        ) {
            RiftWarningBanner(stringResource(Res.string.character_settings_account_character_limit_warning))
        }
        if (character.accountId == null) {
            RiftWarningBanner(stringResource(Res.string.character_settings_account_unassigned_warning))
        }
        if (state.isSharedCacheMissing) {
            RiftWarningBanner(stringResource(Res.string.character_settings_shared_cache_warning))
        }

        val profiles = if (character.backupName != null) character.settingsFiles.keys.sorted() else state.profiles
        val selectedProfile = state.selectedProfiles[character.settingsId]?.takeIf { it in profiles }

        CharacterPageHeader(
            state = state,
            character = character,
            selectedProfile = selectedProfile,
            profiles = profiles,
            onProfileSelected = onProfileSelected,
            onAssignAccount = onAssignAccount,
            onCopySource = onCopySource,
            onCopyDestination = onCopyDestination,
            onRevertChanges = onRevertChanges,
            onSaveChanges = onSaveChanges,
            onBackupSettings = onBackupSettings,
            onDeleteBackup = onDeleteBackup,
        )

        val allSettings = state.displayedSettings[character.settingsId]
        val characterSettings = allSettings?.characterSettings
        val accountSettings = allSettings?.accountSettings

        if (selectedProfile == null || selectedProfile !in character.settingsFiles) {
            Text(
                text = if (selectedProfile == null) {
                    stringResource(Res.string.character_settings_launcher_profiles_empty_description)
                } else {
                    stringResource(Res.string.character_settings_profile_settings_empty_description, selectedProfile)
                },
                style = RiftTheme.typography.bodySecondary,
                modifier = Modifier.padding(top = Spacing.large),
            )
            return
        }

        var selectedTab by remember { mutableStateOf(TAB_WINDOW_LAYOUT) }
        RiftTabBar(
            tabs = listOf(
                Tab(id = TAB_WINDOW_LAYOUT, title = stringResource(Res.string.character_settings_window_layout_tab), isCloseable = false),
                Tab(id = TAB_NEOCOM, title = stringResource(Res.string.character_settings_neocom_tab), isCloseable = false),
                Tab(id = TAB_WATCHLIST, title = stringResource(Res.string.character_settings_watchlist_tab), isCloseable = false),
                Tab(id = TAB_CHAT_CHANNELS, title = stringResource(Res.string.character_settings_chat_channels_tab), isCloseable = false),
                Tab(id = TAB_PROBE_FORMATIONS, title = stringResource(Res.string.character_settings_probe_formations_tab), isCloseable = false),
            ),
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            onTabClosed = {},
        )

        when (selectedTab) {
            TAB_WINDOW_LAYOUT -> {
                Text(
                    text = stringResource(Res.string.character_settings_window_layout_description),
                    style = RiftTheme.typography.bodySecondary,
                )

                var shipState by remember { mutableStateOf(ShipState.InSpace) }
                var isShowingFleet by remember { mutableStateOf(true) }
                var isShowingDrones by remember { mutableStateOf(true) }
                var isShowingMinimized by remember { mutableStateOf(false) }
                val defaultScreenResolution = characterSettings?.screenResolution
                val defaultUiScale = characterSettings?.uiScale ?: 1.0
                var selectedUiScale by remember(character.settingsId, selectedProfile, defaultUiScale) {
                    mutableStateOf(defaultUiScale)
                }
                val uiScales = remember(defaultUiScale) {
                    (EVE_UI_SCALES + defaultUiScale).distinct().sorted()
                }
                var selectedScreenResolution by remember(character.settingsId, selectedProfile, defaultScreenResolution) {
                    mutableStateOf(defaultScreenResolution)
                }
                val screenResolutions = remember(characterSettings?.windowSizesAndPositions, selectedScreenResolution) {
                    val savedResolutions = characterSettings?.windowSizesAndPositions?.values.orEmpty()
                        .mapNotNull { bounds ->
                            bounds.takeLast(2)
                                .takeIf { (width, height) -> width > 0 && height > 0 }
                                ?.let { (width, height) -> width to height }
                        }
                        .groupingBy { it }
                        .eachCount()
                        .entries
                        .sortedWith(
                            compareByDescending<Map.Entry<Pair<Int, Int>, Int>> { it.value }
                                .thenByDescending { it.key.first.toLong() * it.key.second },
                        )
                        .map { it.key }
                    (savedResolutions + listOfNotNull(selectedScreenResolution)).distinct()
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                ) {
                    ShipStateToggle(shipState, onChange = { shipState = it })
                    RiftTooltipArea(stringResource(Res.string.character_settings_ui_scale_tooltip)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                        ) {
                            Text(
                                text = stringResource(Res.string.character_settings_ui_scale_label),
                                style = RiftTheme.typography.bodyPrimary,
                            )
                            RiftDropdown(
                                items = uiScales,
                                selectedItem = selectedUiScale,
                                onItemSelected = { selectedUiScale = it },
                                getItemName = { "${(it * 100).roundToInt()}%" },
                            )
                        }
                    }
                    selectedScreenResolution?.let { resolution ->
                        Text(
                            text = stringResource(Res.string.character_settings_resolution_label),
                            style = RiftTheme.typography.bodyPrimary,
                        )
                        RiftDropdown(
                            items = screenResolutions,
                            selectedItem = resolution,
                            onItemSelected = { selectedScreenResolution = it },
                            getItemName = { item ->
                                val (width, height) = item
                                val displayWidth = (width * selectedUiScale).roundToInt()
                                val displayHeight = (height * selectedUiScale).roundToInt()
                                "$displayWidth × $displayHeight"
                            },
                        )
                    }
                    Text(
                        text = stringResource(Res.string.character_settings_window_visibility_label),
                        style = RiftTheme.typography.bodyPrimary,
                    )
                    RiftCheckboxWithLabel(
                        label = stringResource(Res.string.character_settings_fleet_visibility_label),
                        tooltip = stringResource(Res.string.character_settings_fleet_visibility_tooltip),
                        isChecked = isShowingFleet,
                        onCheckedChange = { isShowingFleet = it },
                    )
                    RiftCheckboxWithLabel(
                        label = stringResource(Res.string.character_settings_drones_visibility_label),
                        tooltip = stringResource(Res.string.character_settings_drones_visibility_tooltip),
                        isChecked = isShowingDrones,
                        onCheckedChange = { isShowingDrones = it },
                    )
                    RiftCheckboxWithLabel(
                        label = stringResource(Res.string.character_settings_minimized_visibility_label),
                        tooltip = stringResource(Res.string.character_settings_minimized_visibility_tooltip),
                        isChecked = isShowingMinimized,
                        onCheckedChange = { isShowingMinimized = it },
                    )
                    val categorizeWindowUseCase: CategorizeWindowUseCase = remember { koin.get() }
                    val windowNames = categorizeWindowUseCase.persistentWindows.mapValues { entry -> entry.value.displayName.resolve() }
                    RiftContextMenuArea(
                        items = categorizeWindowUseCase.persistentWindows
                            .filter { (windowId, window) ->
                                window.isPersistent &&
                                    window.isOpenStateControlled &&
                                    window.isAddable &&
                                    windowId !in characterSettings?.openWindows.orEmpty()
                            }
                            .toList()
                            .sortedBy { it.second.name }
                            .map { (windowId, window) ->
                                ContextMenuItem.TextItem(
                                    text = windowNames.getValue(windowId),
                                    iconContent = window.icon?.let { icon ->
                                        { _ ->
                                            Image(
                                                painter = painterResource(icon),
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp),
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedScreenResolution?.let { resolution ->
                                            onWindowOpened(windowId, resolution)
                                        }
                                    },
                                )
                            },
                        acceptsLeftClick = true,
                        acceptsRightClick = false,
                    ) {
                        RiftButton(
                            text = stringResource(Res.string.character_settings_add_window_button),
                            onClick = {},
                        )
                    }
                }
                WindowLayoutPreview(
                    selectedProfile = selectedProfile,
                    settings = characterSettings,
                    screenResolution = selectedScreenResolution,
                    shipState = shipState,
                    isShowingFleet = isShowingFleet,
                    isShowingDrones = isShowingDrones,
                    isShowingMinimized = isShowingMinimized,
                    accountSettings = accountSettings,
                    onWindowBoundsChanged = onWindowBoundsChanged,
                    onWindowMinimizedChanged = onWindowMinimizedChanged,
                    onWindowClosed = onWindowClosed,
                    onShipUiOffsetChanged = onShipUiOffsetChanged,
                    onShipUiVerticalPositionChanged = onShipUiVerticalPositionChanged,
                    onTargetOriginChanged = onTargetOriginChanged,
                    onTargetsAlignmentChanged = onTargetsAlignmentChanged,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            }
            TAB_NEOCOM -> {
                if (characterSettings != null) {
                    ScrollbarColumn(
                        contentPadding = PaddingValues(end = Spacing.medium),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                            modifier = Modifier.padding(bottom = Spacing.medium),
                        ) {
                            Text(
                                text = stringResource(Res.string.character_settings_neocom_description),
                                style = RiftTheme.typography.bodySecondary,
                            )
                            Column {
                                val widthPrefix = stringResource(Res.string.character_settings_neocom_width_prefix)
                                Text(
                                    text = buildAnnotatedString {
                                        append(widthPrefix)
                                        withStyle(RiftTheme.colors.textSecondary) {
                                            append("${characterSettings.neocomWidthPx}px")
                                        }
                                    },
                                    style = RiftTheme.typography.bodyPrimary,
                                )
                                RiftSlider(
                                    width = 200.dp,
                                    range = NEOCOM_WIDTH_RANGE,
                                    currentValue = characterSettings.neocomWidthPx,
                                    onValueChange = onNeocomWidthChanged,
                                    getValueName = { "${it}px" },
                                    isPreciseScroll = true,
                                    isImmediate = true,
                                )
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(with(LocalDensity.current) { characterSettings.neocomWidthPx.toDp() })
                                    .background(Color.Black.copy(alpha = 0.4f))
                                    .border(1.dp, RiftTheme.colors.borderGreyLight)
                                    .padding(with(LocalDensity.current) { 4.toDp() }),
                            ) {
                                characterSettings.neocomButtons.firstOrNull()?.let { button ->
                                    EveResourceIcon(
                                        resourcePath = button.iconPath,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }
                            }

                            val buttonIds = characterSettings.neocomButtons.getNeocomButtonIds()
                            val buttonNames = NeocomButtonDefinitions.associateWith { stringResource(it.displayName) }
                            RiftContextMenuArea(
                                items = NeocomButtonDefinitions
                                    .filter { it.isAddable && it.id !in buttonIds }
                                    .sortedBy { it.name }
                                    .map { button ->
                                        ContextMenuItem.TextItem(
                                            text = buttonNames.getValue(button),
                                            iconContent = { _ ->
                                                EveResourceIcon(
                                                    resourcePath = button.iconPath,
                                                    modifier = Modifier.size(24.dp),
                                                )
                                            },
                                            onClick = { onNeocomButtonAdded(button.id) },
                                        )
                                    },
                                acceptsLeftClick = true,
                                acceptsRightClick = false,
                            ) {
                                RiftButton(
                                    text = stringResource(Res.string.character_settings_neocom_add_button),
                                    onClick = {},
                                )
                            }
                        }
                        Spacer(Modifier.height(Spacing.small))
                        NeocomButtons(
                            buttons = characterSettings.neocomButtons,
                            parentId = null,
                            onButtonColorChanged = onNeocomButtonColorChanged,
                            onButtonsReordered = onNeocomButtonsReordered,
                            onButtonRemoved = onNeocomButtonRemoved,
                        )
                    }
                }
            }
            TAB_WATCHLIST -> {
                if (characterSettings != null) {
                    Watchlist(
                        settings = characterSettings,
                        characterName = watchlistCharacterName,
                        characterId = watchlistCharacterId,
                        isCharacterLookupComplete = isWatchlistCharacterLookupComplete,
                        onCharacterNameChanged = onWatchlistCharacterNameChanged,
                        onWatchlistUpdated = onWatchlistUpdated,
                        onWatchlistCharacterRemoved = onWatchlistCharacterRemoved,
                    )
                }
            }
            TAB_CHAT_CHANNELS -> {
                if (characterSettings != null) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text(
                            text = stringResource(Res.string.character_settings_chat_channels_description),
                            style = RiftTheme.typography.bodySecondary,
                        )
                        ChatChannels(characterSettings.joinedChatChannels, onChatChannelsChanged)
                    }
                }
            }
            TAB_PROBE_FORMATIONS -> {
                ProbeFormations(accountSettings?.probeFormations.orEmpty(), onProbeFormationsChanged)
            }
        }
    }
}

private const val TAB_WINDOW_LAYOUT = 0
private const val TAB_NEOCOM = 1
private const val TAB_WATCHLIST = 2
private const val TAB_CHAT_CHANNELS = 3
private const val TAB_PROBE_FORMATIONS = 4
private val NEOCOM_WIDTH_RANGE = 24..72
private val EVE_UI_SCALES = listOf(0.9, 1.0, 1.1, 1.25, 1.5, 1.75, 2.0, 2.5, 3.0, 4.0)

private fun List<NeocomButton>.getNeocomButtonIds(): Set<String> {
    return flatMapTo(mutableSetOf()) { button -> setOf(button.id) + button.children.getNeocomButtonIds() }
}

@Composable
private fun AccountAssignmentControls(
    character: CharacterItem,
    state: UiState,
    onAssignAccount: (Int, Int) -> Unit,
) {
    var isAssigningAccount: Boolean by remember(character) { mutableStateOf(false) }

    AnimatedContent(isAssigningAccount) { isAssigning ->
        if (isAssigning) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(Res.string.character_settings_account_assignment_description),
                    style = RiftTheme.typography.bodyPrimary,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small),
                ) {
                    state.accounts.forEach { account ->
                        RiftButton(
                            stringResource(Res.string.character_settings_account_label, account.id),
                            isCompact = true,
                            onClick = {
                                onAssignAccount(character.characterId, account.id)
                                isAssigningAccount = false
                            },
                        )
                    }
                }
            }
        } else {
            if (character.accountId != null && state.accounts.size > 1) {
                RiftTooltipArea(
                    text = stringResource(Res.string.character_settings_account_reassignment_tooltip),
                ) {
                    RiftButton(
                        text = stringResource(Res.string.character_settings_account_reassign_button),
                        type = ButtonType.Secondary,
                        cornerCut = ButtonCornerCut.None,
                        onClick = { isAssigningAccount = true },
                    )
                }
            } else if (character.accountId == null) {
                RiftButton(
                    text = stringResource(Res.string.character_settings_account_assign_button),
                    type = ButtonType.Secondary,
                    cornerCut = ButtonCornerCut.None,
                    onClick = { isAssigningAccount = true },
                )
            }
        }
    }
}

@Composable
private fun CharacterPageHeader(
    state: UiState,
    character: CharacterItem,
    selectedProfile: String?,
    profiles: List<String>,
    onProfileSelected: (Int, String) -> Unit,
    onAssignAccount: (characterId: Int, accountId: Int) -> Unit,
    onCopySource: (Int) -> Unit,
    onCopyDestination: (Int) -> Unit,
    onRevertChanges: (Int) -> Unit,
    onSaveChanges: (Int) -> Unit,
    onBackupSettings: (Int) -> Unit,
    onDeleteBackup: (Int) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DynamicCharacterPortraitParallax(
            characterId = character.characterId,
            size = 64.dp,
            enterTimestamp = null,
            pointerInteractionStateHolder = null,
        )

        Column(
            modifier = Modifier.padding(horizontal = Spacing.medium),
        ) {
            if (character.backupName != null) {
                Text(
                    text = "${character.info?.name ?: character.characterId} - ${character.backupName}",
                    style = RiftTheme.typography.headerHighlighted,
                )
                character.backupTimestamp?.let { timestamp ->
                    Text(
                        text = stringResource(Res.string.character_settings_backup_created_time, getRelativeTimeText(timestamp, ZoneId.systemDefault(), getNow()).resolve()),
                        style = RiftTheme.typography.bodySecondary,
                    )
                }
            } else if (character.info != null) {
                Text(
                    text = character.info.name,
                    style = RiftTheme.typography.headerHighlighted,
                )
            } else {
                Text(
                    text = stringResource(Res.string.character_settings_character_load_failure),
                    style = RiftTheme.typography.bodySecondary.copy(color = RiftTheme.colors.borderError),
                )
            }

            if (selectedProfile != null) {
                Text(
                    text = stringResource(Res.string.character_settings_launcher_profile_label),
                    style = RiftTheme.typography.bodySecondary,
                )
                if (character.backupName != null) {
                    Text(
                        text = selectedProfile,
                        style = RiftTheme.typography.bodyPrimary,
                    )
                } else {
                    RiftDropdown(
                        items = profiles,
                        selectedItem = selectedProfile,
                        onItemSelected = { onProfileSelected(character.settingsId, it) },
                        getItemName = { it },
                    )
                }
            } else {
                RequirementIcon(
                    isFulfilled = false,
                    fulfilledTooltip = "",
                    notFulfilledTooltip = stringResource(Res.string.character_settings_character_settings_missing_tooltip),
                )
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier.padding(start = Spacing.medium),
        ) {
            if (character.backupName == null) {
                AccountAssignmentControls(
                    character = character,
                    state = state,
                    onAssignAccount = onAssignAccount,
                )
            }

            CharacterCopyControls(
                state = state,
                character = character,
                selectedProfile = selectedProfile,
                onCopySource = onCopySource,
                onCopyDestination = onCopyDestination,
                onRevertChanges = onRevertChanges,
                onSaveChanges = onSaveChanges,
                onBackupSettings = onBackupSettings,
                onDeleteBackup = onDeleteBackup,
            )
        }
    }
}

@Composable
private fun CopyControls(
    state: UiState,
    onCopySettingChanged: (CopySetting?, Boolean) -> Unit,
    onCopySettingsSelected: () -> Unit,
    onCopyConfirm: () -> Unit,
    onCancelCopy: () -> Unit,
) {
    AnimatedContent(state.copying, contentKey = { it == CopyingState.SelectingSource }) {
        when (state.copying) {
            CopyingState.SelectingSource -> {}
            else -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                    modifier = Modifier.padding(start = Spacing.large, end = Spacing.large, bottom = Spacing.large),
                ) {
                    when (val copying = state.copying) {
                        is CopyingState.SelectingSettings -> {
                            Text(
                                text = parseEveFormattedText(stringResource(Res.string.character_settings_copy_source_message, Entities.escape(copying.source.name)), decodeEntities = true),
                                style = RiftTheme.typography.headerPrimary,
                            )
                            Text(stringResource(Res.string.character_settings_copy_settings_description), style = RiftTheme.typography.bodyPrimary)
                            FlowRow(
                                verticalArrangement = Arrangement.spacedBy(Spacing.small),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                            ) {
                                CopySetting.entries.forEach { setting ->
                                    RiftCheckboxWithLabel(
                                        label = stringResource(setting.displayName),
                                        isChecked = setting in copying.settings,
                                        onCheckedChange = { onCopySettingChanged(setting, it) },
                                    )
                                }
                                RiftCheckboxWithLabel(
                                    label = stringResource(Res.string.character_settings_copy_all_settings_label),
                                    tooltip = stringResource(Res.string.character_settings_copy_all_settings_tooltip),
                                    isChecked = copying.allSettings,
                                    onCheckedChange = { onCopySettingChanged(null, it) },
                                )
                            }
                        }
                        is CopyingState.SelectingDestination -> {
                            Text(
                                text = parseEveFormattedText(stringResource(Res.string.character_settings_copy_selected_settings_message, Entities.escape(copying.getSettingsDescription()), Entities.escape(copying.source.name)), decodeEntities = true),
                                style = RiftTheme.typography.headerPrimary,
                            )
                            Text(
                                text = stringResource(Res.string.character_settings_copy_destination_description),
                                style = RiftTheme.typography.bodyPrimary,
                            )
                        }
                        is CopyingState.DestinationSelected -> {
                            Text(
                                text = parseEveFormattedText(stringResource(Res.string.character_settings_copy_selected_settings_message, Entities.escape(copying.getSettingsDescription()), Entities.escape(copying.source.name)), decodeEntities = true),
                                style = RiftTheme.typography.headerPrimary,
                            )

                            val destinationIds = copying.destinations.map { it.character.id }
                            val sourceAccountId = state.characters.firstOrNull { it.characterId == copying.source.id }?.accountId
                            val charactersOnSourceAccount = state.characters.filter { it.accountId == sourceAccountId }.map { it.characterId }
                            val charactersOnTargetAccounts = destinationIds.flatMap { destinationId ->
                                val accountId = state.characters.firstOrNull { it.characterId == destinationId }?.accountId
                                state.characters.filter { it.accountId == accountId }
                            }.distinctBy { it.characterId }
                            val unselectedAffectedCharacters = charactersOnTargetAccounts
                                .filter { it.characterId !in destinationIds }
                                .filter { it.characterId !in charactersOnSourceAccount }
                            val copiesAccountSettings = copying.allSettings ||
                                CopySetting.WindowLayout in copying.settings ||
                                CopySetting.ProbeFormations in copying.settings

                            Text(
                                text = parseEveFormattedText(stringResource(Res.string.character_settings_copy_destinations_message, copying.destinations.joinToString { "<font color=\"textHighlighted\">${Entities.escape(it.character.name)}</font>" }), decodeEntities = true),
                                style = RiftTheme.typography.bodyPrimary,
                            )
                            if (copiesAccountSettings && unselectedAffectedCharacters.isNotEmpty()) {
                                Text(
                                    text = parseEveFormattedText(stringResource(Res.string.character_settings_copy_affected_characters_message, unselectedAffectedCharacters.joinToString { "<font color=\"textHighlighted\">${Entities.escape(it.info?.name ?: "${it.characterId}")}</font>" }), decodeEntities = true),
                                    style = RiftTheme.typography.bodyPrimary,
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                    ) {
                        when (val copying = state.copying) {
                            is CopyingState.SelectingSettings -> {
                                RiftButton(
                                    text = stringResource(Res.string.character_settings_copy_cancel_button),
                                    type = ButtonType.Secondary,
                                    cornerCut = ButtonCornerCut.None,
                                    onClick = onCancelCopy,
                                )
                                RiftButton(
                                    text = stringResource(Res.string.character_settings_copy_settings_confirm_button),
                                    isEnabled = copying.settings.isNotEmpty(),
                                    onClick = onCopySettingsSelected,
                                )
                            }
                            is CopyingState.SelectingDestination -> {
                                RiftButton(
                                    text = stringResource(Res.string.character_settings_copy_cancel_button),
                                    type = ButtonType.Secondary,
                                    onClick = onCancelCopy,
                                )
                            }
                            is CopyingState.DestinationSelected -> {
                                RiftButton(
                                    text = stringResource(Res.string.character_settings_copy_cancel_button),
                                    type = ButtonType.Secondary,
                                    cornerCut = ButtonCornerCut.None,
                                    onClick = onCancelCopy,
                                )
                                RiftButton(
                                    text = stringResource(Res.string.character_settings_copy_characters_confirm_button),
                                    isEnabled = !state.isOnline,
                                    onClick = onCopyConfirm,
                                )
                            }
                        }
                    }

                    Divider(
                        color = RiftTheme.colors.divider,
                        modifier = Modifier.padding(top = Spacing.medium),
                    )
                }
            }
        }
    }
}

@Composable
private fun CharacterCopyControls(
    state: UiState,
    character: CharacterItem,
    selectedProfile: String?,
    onCopySource: (Int) -> Unit,
    onCopyDestination: (Int) -> Unit,
    onRevertChanges: (Int) -> Unit,
    onSaveChanges: (Int) -> Unit,
    onBackupSettings: (Int) -> Unit,
    onDeleteBackup: (Int) -> Unit,
) {
    when (val copying = state.copying) {
        CopyingState.SelectingSource -> {
            val hasUnsavedChanges = state.hasChanges(character.settingsId)
            val selectedProfile = state.selectedProfiles[character.settingsId]
            val hasSettingsInSelectedProfile = selectedProfile in character.settingsFiles
            RiftTooltipArea(
                text = when {
                    state.isOnline -> stringResource(Res.string.character_settings_backup_online_tooltip)
                    hasUnsavedChanges -> stringResource(Res.string.character_settings_backup_unsaved_changes_tooltip)
                    else -> stringResource(Res.string.character_settings_backup_create_tooltip)
                },
            ) {
                RiftButton(
                    text = stringResource(Res.string.character_settings_backup_create_button),
                    type = ButtonType.Secondary,
                    cornerCut = ButtonCornerCut.None,
                    isEnabled = !state.isOnline && !hasUnsavedChanges && character.accountId != null && hasSettingsInSelectedProfile,
                    onClick = { onBackupSettings(character.settingsId) },
                )
            }
            if (character.backupName != null) {
                RiftButton(
                    text = stringResource(Res.string.character_settings_backup_delete_button),
                    type = ButtonType.Negative,
                    cornerCut = ButtonCornerCut.None,
                    onClick = { onDeleteBackup(character.settingsId) },
                )
            }
            RiftTooltipArea(
                text = when {
                    state.isOnline -> stringResource(Res.string.character_settings_copy_online_tooltip)
                    hasUnsavedChanges -> stringResource(Res.string.character_settings_copy_unsaved_changes_tooltip)
                    else -> stringResource(Res.string.character_settings_copy_tooltip)
                },
            ) {
                RiftButton(
                    text = stringResource(Res.string.character_settings_copy_button),
                    icon = Res.drawable.copy_16px,
                    isEnabled = !state.isOnline && !hasUnsavedChanges && character.accountId != null && hasSettingsInSelectedProfile,
                    onClick = { onCopySource(character.settingsId) },
                )
            }
            AnimatedVisibility(hasUnsavedChanges, enter = fadeIn(), exit = fadeOut()) {
                RiftButton(
                    text = stringResource(Res.string.character_settings_revert_button),
                    type = ButtonType.Secondary,
                    cornerCut = ButtonCornerCut.None,
                    onClick = { onRevertChanges(character.settingsId) },
                )
            }
            AnimatedVisibility(hasUnsavedChanges, enter = fadeIn(), exit = fadeOut()) {
                RiftTooltipArea(
                    text = if (state.isOnline) stringResource(Res.string.character_settings_save_online_tooltip) else stringResource(Res.string.character_settings_save_tooltip),
                ) {
                    RiftButton(
                        text = stringResource(Res.string.character_settings_save_button),
                        isEnabled = !state.isOnline,
                        onClick = { onSaveChanges(character.settingsId) },
                    )
                }
            }
        }

        is CopyingState.SelectingDestination -> {
            val isCopyingFromThisCharacter = copying.source.id == character.settingsId
            val isCopyingFromThisProfile = copying.sourceProfile == selectedProfile
            val isAccountAssigned = character.accountId != null
            if (!(isCopyingFromThisCharacter && isCopyingFromThisProfile) && isAccountAssigned) {
                RiftButton(
                    text = stringResource(Res.string.character_settings_paste_button),
                    icon = Res.drawable.recall_drones_16px,
                    isEnabled = !state.isOnline,
                    onClick = { onCopyDestination(character.settingsId) },
                )
            }
        }

        is CopyingState.SelectingSettings -> {}

        is CopyingState.DestinationSelected -> {
            if (copying.source.id != character.settingsId && copying.destinations.none { it.character.id == character.settingsId } && character.accountId != null) {
                RiftButton(
                    text = stringResource(Res.string.character_settings_paste_button),
                    icon = Res.drawable.recall_drones_16px,
                    isEnabled = !state.isOnline,
                    onClick = { onCopyDestination(character.settingsId) },
                )
            }
        }
    }
}

@Composable
private fun CopyingState.SelectingDestination.getSettingsDescription(): String = if (allSettings) stringResource(Res.string.character_settings_copy_all_settings_summary) else settings.map { stringResource(it.summary) }.joinToString()

@Composable
private fun CopyingState.DestinationSelected.getSettingsDescription(): String = if (allSettings) stringResource(Res.string.character_settings_copy_all_settings_summary) else settings.map { stringResource(it.summary) }.joinToString()

@Composable
private fun WindowScope.BackupNameDialog(
    parentWindowState: RiftWindowState,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    RiftDialog(
        title = stringResource(Res.string.character_settings_backup_create_dialog_title),
        icon = Res.drawable.window_character_settings,
        parentState = parentWindowState,
        state = rememberWindowState(width = 360.dp, height = Dp.Unspecified),
        onCloseClick = onDismiss,
    ) {
        var name by remember { mutableStateOf("") }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            Text(stringResource(Res.string.character_settings_backup_name_description), style = RiftTheme.typography.bodyPrimary)
            RiftTextField(
                text = name,
                placeholder = stringResource(Res.string.character_settings_backup_name_placeholder),
                onTextChanged = { name = it.take(64) },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                RiftButton(
                    text = stringResource(Res.string.character_settings_cancel_button),
                    type = ButtonType.Secondary,
                    cornerCut = ButtonCornerCut.BottomLeft,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
                RiftButton(
                    text = stringResource(Res.string.character_settings_backup_create_button),
                    isEnabled = name.isNotBlank(),
                    onClick = { onConfirm(name) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun WindowScope.DeleteBackupDialog(
    parentWindowState: RiftWindowState,
    backup: CharacterItem,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    RiftDialog(
        title = stringResource(Res.string.character_settings_backup_delete_dialog_title),
        icon = Res.drawable.window_character_settings,
        parentState = parentWindowState,
        state = rememberWindowState(width = 520.dp, height = Dp.Unspecified),
        onCloseClick = onDismiss,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            Text(
                text = stringResource(Res.string.character_settings_backup_delete_confirmation, backup.info?.name ?: backup.characterId, backup.backupName.toString()),
                style = RiftTheme.typography.headerPrimary,
            )
            Text(
                text = stringResource(Res.string.character_settings_backup_delete_files_description),
                style = RiftTheme.typography.bodySecondary,
            )
            (backup.settingsFiles.values + backup.accountSettingsFiles.values).forEach { path ->
                Text(
                    text = path.toString(),
                    style = RiftTheme.typography.detailPrimary,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                RiftButton(
                    text = stringResource(Res.string.character_settings_cancel_button),
                    type = ButtonType.Secondary,
                    cornerCut = ButtonCornerCut.BottomLeft,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
                RiftButton(
                    text = stringResource(Res.string.character_settings_backup_delete_button),
                    type = ButtonType.Negative,
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun EmptyCharacterPage() {
    Text(
        text = stringResource(Res.string.character_settings_characters_empty_description),
        style = RiftTheme.typography.headerPrimary,
        modifier = Modifier.fillMaxSize().padding(Spacing.large),
    )
}
