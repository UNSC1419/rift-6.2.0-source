package dev.nohus.rift.map.markers

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.rememberWindowState
import dev.nohus.rift.alerts.creategroup.CreateGroupDialog
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.ExpandChevron
import dev.nohus.rift.compose.PointerInteractionStateHolder
import dev.nohus.rift.compose.RiftAutocompleteTextField
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftCheckbox
import dev.nohus.rift.compose.RiftCheckboxWithLabel
import dev.nohus.rift.compose.RiftDialog
import dev.nohus.rift.compose.RiftDropdownWithLabel
import dev.nohus.rift.compose.RiftImageButton
import dev.nohus.rift.compose.RiftMessageDialog
import dev.nohus.rift.compose.RiftTextField
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.ScrollbarLazyColumn
import dev.nohus.rift.compose.hoverBackground
import dev.nohus.rift.compose.modifyIf
import dev.nohus.rift.compose.pointerInteraction
import dev.nohus.rift.compose.theme.Cursors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.allDrawableResources
import dev.nohus.rift.generated.resources.common_action_cancel
import dev.nohus.rift.generated.resources.delete
import dev.nohus.rift.generated.resources.editplanicon
import dev.nohus.rift.generated.resources.flag_background
import dev.nohus.rift.generated.resources.map_markers_action_add
import dev.nohus.rift.generated.resources.map_markers_action_create_group
import dev.nohus.rift.generated.resources.map_markers_action_delete
import dev.nohus.rift.generated.resources.map_markers_action_delete_group
import dev.nohus.rift.generated.resources.map_markers_action_delete_group_keep_markers
import dev.nohus.rift.generated.resources.map_markers_action_disable_group
import dev.nohus.rift.generated.resources.map_markers_action_edit
import dev.nohus.rift.generated.resources.map_markers_action_enable_group
import dev.nohus.rift.generated.resources.map_markers_action_export
import dev.nohus.rift.generated.resources.map_markers_action_import
import dev.nohus.rift.generated.resources.map_markers_action_pin
import dev.nohus.rift.generated.resources.map_markers_action_rename_group
import dev.nohus.rift.generated.resources.map_markers_action_show
import dev.nohus.rift.generated.resources.map_markers_action_unpin
import dev.nohus.rift.generated.resources.map_markers_action_update
import dev.nohus.rift.generated.resources.map_markers_color_label
import dev.nohus.rift.generated.resources.map_markers_default_group
import dev.nohus.rift.generated.resources.map_markers_empty
import dev.nohus.rift.generated.resources.map_markers_enabled_count
import dev.nohus.rift.generated.resources.map_markers_export_description
import dev.nohus.rift.generated.resources.map_markers_export_title
import dev.nohus.rift.generated.resources.map_markers_group_description
import dev.nohus.rift.generated.resources.map_markers_group_empty
import dev.nohus.rift.generated.resources.map_markers_group_enabled_summary
import dev.nohus.rift.generated.resources.map_markers_group_label
import dev.nohus.rift.generated.resources.map_markers_group_summary
import dev.nohus.rift.generated.resources.map_markers_icon_label
import dev.nohus.rift.generated.resources.map_markers_label_placeholder
import dev.nohus.rift.generated.resources.map_markers_location
import dev.nohus.rift.generated.resources.map_markers_pin_description
import dev.nohus.rift.generated.resources.map_markers_system_placeholder
import dev.nohus.rift.generated.resources.map_markers_unknown_location
import dev.nohus.rift.generated.resources.map_markers_window_title
import dev.nohus.rift.generated.resources.menu_pinned
import dev.nohus.rift.generated.resources.toggle_off_18
import dev.nohus.rift.generated.resources.toggle_on_18
import dev.nohus.rift.generated.resources.window_locations
import dev.nohus.rift.map.markers.MapMarkersViewModel.UiState
import dev.nohus.rift.repositories.SolarSystemsRepository
import dev.nohus.rift.utils.formatNumber
import dev.nohus.rift.utils.sound.UiSound
import dev.nohus.rift.utils.sound.rememberUiSoundPlayer
import dev.nohus.rift.utils.withColor
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import java.util.UUID

@Composable
fun MapMarkersWindow(
    inputModel: MapMarkersInputModel,
    windowState: RiftWindowState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: MapMarkersViewModel = viewModel(inputModel)
    val state by viewModel.state.collectAsState()

    RiftWindow(
        title = stringResource(Res.string.map_markers_window_title),
        icon = Res.drawable.window_locations,
        state = windowState,
        onCloseClick = onCloseRequest,
        isResizable = true,
    ) {
        MapMarkersWindowContent(
            state = state,
            onSystemTextChange = viewModel::onSystemTextChange,
            onAddMarkerClick = viewModel::onAddMarkerClick,
            onDeleteMarkerClick = viewModel::onDeleteMarkerClick,
            onCancelEditClick = viewModel::onCancelEditClick,
            onMarkerClick = viewModel::onMarkerClick,
            onShowMarkerClick = viewModel::onShowMarkerClick,
            onEditMarkerClick = viewModel::onEditMarkerClick,
            onToggleMarker = viewModel::onToggleMarker,
            onToggleMarkerPinned = viewModel::onToggleMarkerPinned,
            onGroupChange = viewModel::onGroupChange,
            onGroupClick = viewModel::onGroupClick,
            onCreateGroupClick = viewModel::onCreateGroupClick,
            onGroupRenameClick = viewModel::onGroupRenameClick,
            onGroupDeleteClick = viewModel::onGroupDeleteClick,
            onGroupToggleMarkers = viewModel::onGroupToggleMarkers,
            onImportClick = viewModel::onImportClick,
            onExportClick = viewModel::onExportClick,
        )

        val isCreateGroupDialogOpen = state.isCreateGroupDialogOpen
        if (isCreateGroupDialogOpen != null) {
            CreateGroupDialog(
                inputModel = isCreateGroupDialogOpen,
                parentWindowState = windowState,
                description = stringResource(Res.string.map_markers_group_description),
                onDismiss = viewModel::onCloseCreateGroup,
                onConfirmClick = viewModel::onCreateGroupConfirm,
            )
        }

        state.exportGroups?.let { selectedGroups ->
            ExportMarkersDialog(
                groups = (state.markers.map { it.group } + state.groups).toSet(),
                selectedGroups = selectedGroups,
                parentWindowState = windowState,
                onGroupToggle = viewModel::onExportGroupToggle,
                onDismiss = viewModel::onExportCancel,
                onExportClick = viewModel::onExportConfirm,
            )
        }

        state.dialog?.let {
            RiftMessageDialog(
                dialog = it,
                parentWindowState = windowState,
                onDismiss = viewModel::onCloseDialogMessage,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MapMarkersWindowContent(
    state: UiState,
    onSystemTextChange: (String) -> Unit,
    onAddMarkerClick: (NewMarkerInput) -> Boolean,
    onDeleteMarkerClick: (UUID) -> Unit,
    onCancelEditClick: () -> Unit,
    onMarkerClick: (UUID) -> Unit,
    onShowMarkerClick: (UUID) -> Unit,
    onEditMarkerClick: (UUID) -> Unit,
    onToggleMarker: (UUID, Boolean) -> Unit,
    onToggleMarkerPinned: (UUID) -> Unit,
    onGroupChange: (UUID, String?) -> Unit,
    onGroupClick: (String?) -> Unit,
    onCreateGroupClick: () -> Unit,
    onGroupRenameClick: (String) -> Unit,
    onGroupDeleteClick: (String) -> Unit,
    onGroupToggleMarkers: (String?) -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        ScrollbarLazyColumn(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, RiftTheme.colors.borderGrey),
            scrollbarModifier = Modifier.padding(vertical = Spacing.small),
        ) {
            if (state.markers.isEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.map_markers_empty),
                        style = RiftTheme.typography.headerPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.large)
                            .padding(horizontal = Spacing.large),
                    )
                }
            } else {
                val nonEmptyGroups = state.markers.mapNotNull { it.group }.toSet()
                val emptyGroups = state.groups - nonEmptyGroups
                state.markers
                    .groupBy { it.group }
                    .let { it + emptyGroups.associateWith { emptyList() } }
                    .entries
                    .sortedWith(compareBy({ it.key == null }, { it.key }))
                    .forEach { (group, markersInGroup) ->
                        val isExpanded = group !in state.collapsedGroups
                        stickyHeader {
                            val total = markersInGroup.size
                            val enabled = markersInGroup.count { it.isEnabled }
                            val groupName = group ?: stringResource(Res.string.map_markers_default_group)
                            val summary = if (enabled < total) {
                                pluralStringResource(Res.plurals.map_markers_group_enabled_summary, total, formatNumber(total), pluralStringResource(Res.plurals.map_markers_enabled_count, enabled, formatNumber(enabled)))
                            } else {
                                pluralStringResource(Res.plurals.map_markers_group_summary, total, formatNumber(total))
                            }
                            val text = buildAnnotatedString {
                                withColor(RiftTheme.colors.textPrimary) {
                                    append(groupName)
                                }
                                append(" - ")
                                append(summary)
                            }
                            MarkerGroupHeader(
                                name = text,
                                isEmpty = markersInGroup.isEmpty(),
                                isExpanded = isExpanded,
                                isDefault = group == null,
                                hasEnabledMarkers = markersInGroup.any { it.isEnabled },
                                onClick = { onGroupClick(group) },
                                onGroupToggleMarkers = { onGroupToggleMarkers(group) },
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
                            items(markersInGroup, key = { it.id }) { marker ->
                                MarkerItem(
                                    marker = marker,
                                    isExpanded = marker.id == state.expandedMarker,
                                    groups = state.groups,
                                    onMarkerClick = { onMarkerClick(marker.id) },
                                    onShowMarkerClick = { onShowMarkerClick(marker.id) },
                                    onEditMarkerClick = { onEditMarkerClick(marker.id) },
                                    onDeleteMarkerClick = { onDeleteMarkerClick(marker.id) },
                                    onToggleMarker = { onToggleMarker(marker.id, it) },
                                    onToggleMarkerPinned = { onToggleMarkerPinned(marker.id) },
                                    onGroupChange = { onGroupChange(marker.id, it) },
                                )
                            }
                        }
                    }
            }
        }

        var markerText by remember { mutableStateOf("") }
        var markerColor: Color? by remember { mutableStateOf(null) }
        var markerIcon by remember { mutableStateOf("map_marker_place_bookmark") }

        LaunchedEffect(state.editingMarker) {
            state.editingMarker?.let {
                markerText = it.label
                markerColor = it.color
                markerIcon = it.iconName
            } ?: run {
                markerText = ""
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            val solarSystemsRepository: SolarSystemsRepository = remember { koin.get() }
            RiftAutocompleteTextField(
                text = state.systemText,
                suggestions = remember(state.systemText) { getSuggestions(solarSystemsRepository, state.systemText) },
                placeholder = stringResource(Res.string.map_markers_system_placeholder),
                onTextChanged = {
                    onSystemTextChange(it)
                },
                onDeleteClick = { onSystemTextChange("") },
                modifier = Modifier.weight(1f),
            )
            RiftTextField(
                text = markerText,
                placeholder = stringResource(Res.string.map_markers_label_placeholder),
                onTextChanged = { markerText = it.take(50) },
                onDeleteClick = { markerText = "" },
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Spacer(Modifier.weight(1f))
            RiftButton(
                text = stringResource(Res.string.map_markers_action_import),
                type = ButtonType.Secondary,
                cornerCut = ButtonCornerCut.None,
                onClick = onImportClick,
            )
            RiftButton(
                text = stringResource(Res.string.map_markers_action_export),
                type = ButtonType.Secondary,
                cornerCut = ButtonCornerCut.None,
                isEnabled = state.markers.isNotEmpty(),
                onClick = onExportClick,
            )
            if (state.markers.isNotEmpty()) {
                RiftButton(
                    text = stringResource(Res.string.map_markers_action_create_group),
                    type = ButtonType.Secondary,
                    cornerCut = ButtonCornerCut.None,
                    onClick = onCreateGroupClick,
                )
            }
            val isEditing = state.editingMarker != null
            if (isEditing) {
                RiftButton(
                    text = stringResource(Res.string.common_action_cancel),
                    type = ButtonType.Secondary,
                    cornerCut = ButtonCornerCut.None,
                    onClick = onCancelEditClick,
                )
            }
            RiftButton(
                text = if (isEditing) stringResource(Res.string.map_markers_action_update) else stringResource(Res.string.map_markers_action_add),
                onClick = {
                    val added = onAddMarkerClick(NewMarkerInput(state.systemText, markerText, markerColor, markerIcon))
                    if (added) {
                        onSystemTextChange("")
                        markerText = ""
                    }
                },
            )
        }

        Text(
            text = stringResource(Res.string.map_markers_color_label),
            style = RiftTheme.typography.headerPrimary,
        )
        val animatedColor by animateColorAsState(markerColor ?: Color.White)
        val colors = listOf(
            null,
            Color(0xFF2C75E1),
            Color(0xFF399AEB),
            Color(0xFF4ECEF8),
            Color(0xFF60DBA3),
            Color(0xFF71E754),
            Color(0xFFF5FF83),
            Color(0xFFDC6C06),
            Color(0xFFCE440F),
            Color(0xFFBB1116),
            Color(0xFF731F1F),
            Color(0xFF8D3163),
        )
        Row(
            modifier = Modifier
                .border(1.dp, RiftTheme.colors.borderGreyLight),
        ) {
            for (color in colors) {
                Image(
                    painter = painterResource(Res.drawable.flag_background),
                    contentDescription = null,
                    colorFilter = color?.let { ColorFilter.tint(it) },
                    modifier = Modifier
                        .hoverBackground()
                        .modifyIf(color == markerColor) {
                            background(RiftTheme.colors.backgroundSelected)
                        }
                        .onClick {
                            markerColor = color
                        }
                        .pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
                        .padding(6.dp)
                        .size(12.dp),
                )
            }
        }

        Text(
            text = stringResource(Res.string.map_markers_icon_label),
            style = RiftTheme.typography.headerPrimary,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            Res.allDrawableResources
                .filter { (name, _) -> name.startsWith("map_marker_") }
                .entries
                .groupBy { it.key.substringAfter("map_marker_").substringBefore("_") }
                .forEach { (_, markers) ->
                    Row(
                        modifier = Modifier
                            .border(1.dp, RiftTheme.colors.borderGreyLight),
                    ) {
                        for ((name, drawable) in markers) {
                            Image(
                                painter = painterResource(drawable),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(animatedColor),
                                modifier = Modifier
                                    .hoverBackground()
                                    .modifyIf(name == markerIcon) {
                                        background(RiftTheme.colors.backgroundSelected)
                                    }
                                    .onClick {
                                        markerIcon = name
                                    }
                                    .pointerHoverIcon(PointerIcon(Cursors.pointerInteractive))
                                    .padding(Spacing.small)
                                    .size(16.dp),
                            )
                        }
                    }
                }
        }
    }
}

@Composable
private fun WindowScope.ExportMarkersDialog(
    groups: Set<String?>,
    selectedGroups: Set<String?>,
    parentWindowState: RiftWindowState,
    onGroupToggle: (String?, Boolean) -> Unit,
    onDismiss: () -> Unit,
    onExportClick: () -> Unit,
) {
    RiftDialog(
        title = stringResource(Res.string.map_markers_export_title),
        icon = Res.drawable.window_locations,
        parentState = parentWindowState,
        state = rememberWindowState(width = 360.dp, height = 400.dp),
        onCloseClick = onDismiss,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(Res.string.map_markers_export_description),
                style = RiftTheme.typography.bodyPrimary,
            )
            ScrollbarLazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                contentPadding = PaddingValues(Spacing.medium),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, RiftTheme.colors.borderGrey),
                scrollbarModifier = Modifier.padding(vertical = Spacing.small),
            ) {
                items(groups.sortedWith(compareBy({ it != null }, { it }))) { group ->
                    RiftCheckboxWithLabel(
                        label = group ?: stringResource(Res.string.map_markers_default_group),
                        isChecked = group in selectedGroups,
                        onCheckedChange = { onGroupToggle(group, it) },
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                modifier = Modifier.fillMaxWidth(),
            ) {
                RiftButton(
                    text = stringResource(Res.string.common_action_cancel),
                    type = ButtonType.Secondary,
                    cornerCut = ButtonCornerCut.BottomLeft,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
                RiftButton(
                    text = stringResource(Res.string.map_markers_action_export),
                    isEnabled = selectedGroups.isNotEmpty(),
                    onClick = onExportClick,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.MarkerGroupHeader(
    name: AnnotatedString,
    isEmpty: Boolean,
    isExpanded: Boolean,
    isDefault: Boolean,
    hasEnabledMarkers: Boolean,
    onClick: () -> Unit,
    onGroupToggleMarkers: () -> Unit,
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
                text = if (hasEnabledMarkers) stringResource(Res.string.map_markers_action_disable_group) else stringResource(Res.string.map_markers_action_enable_group),
            ) {
                RiftImageButton(
                    resource = if (hasEnabledMarkers) Res.drawable.toggle_on_18 else Res.drawable.toggle_off_18,
                    size = 18.dp,
                    onClick = onGroupToggleMarkers,
                    modifier = Modifier.alpha(buttonsAlpha),
                )
            }
        }
        if (!isDefault) {
            RiftTooltipArea(
                text = stringResource(Res.string.map_markers_action_rename_group),
            ) {
                RiftImageButton(
                    resource = Res.drawable.editplanicon,
                    size = 20.dp,
                    onClick = onGroupRenameClick,
                    modifier = Modifier.alpha(buttonsAlpha),
                )
            }
            RiftTooltipArea(
                text = if (isEmpty) stringResource(Res.string.map_markers_action_delete_group) else stringResource(Res.string.map_markers_action_delete_group_keep_markers),
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
            .padding(vertical = Spacing.medium, horizontal = Spacing.medium)
            .fillMaxWidth()
            .animateItem()
            .animateContentSize(),
    ) {
        Text(
            text = stringResource(Res.string.map_markers_group_empty),
            style = RiftTheme.typography.bodySecondary,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.MarkerItem(
    marker: MapMarkerItem,
    isExpanded: Boolean,
    groups: Set<String>,
    onMarkerClick: () -> Unit,
    onShowMarkerClick: () -> Unit,
    onEditMarkerClick: () -> Unit,
    onDeleteMarkerClick: () -> Unit,
    onToggleMarker: (Boolean) -> Unit,
    onToggleMarkerPinned: () -> Unit,
    onGroupChange: (String?) -> Unit,
) {
    val player = rememberUiSoundPlayer()
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
                onMarkerClick()
            },
    ) {
        val alpha = if (marker.isEnabled) 1f else 0.5f
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = Spacing.small),
        ) {
            RiftCheckbox(
                isChecked = marker.isEnabled,
                onCheckedChange = onToggleMarker,
            )
            Image(
                painter = painterResource(marker.icon),
                contentDescription = null,
                colorFilter = marker.color?.let { ColorFilter.tint(it) },
                modifier = Modifier
                    .alpha(alpha)
                    .size(16.dp),
            )
            RiftTooltipArea(
                tooltip = @Composable {
                    Column(
                        modifier = Modifier.padding(Spacing.large),
                    ) {
                        Text(
                            text = if (marker.isPinned) stringResource(Res.string.map_markers_action_unpin) else stringResource(Res.string.map_markers_action_pin),
                            style = RiftTheme.typography.bodyPrimary,
                        )
                        Text(
                            text = stringResource(Res.string.map_markers_pin_description),
                            style = RiftTheme.typography.bodySecondary,
                        )
                    }
                },
            ) {
                RiftImageButton(
                    resource = Res.drawable.menu_pinned,
                    size = 16.dp,
                    onClick = onToggleMarkerPinned,
                    isFullAlpha = marker.isPinned,
                    modifier = Modifier.alpha(if (marker.isPinned) alpha else alpha * 0.35f),
                )
            }
            Text(
                text = stringResource(Res.string.map_markers_location, marker.systemName ?: stringResource(Res.string.map_markers_unknown_location), marker.regionName ?: stringResource(Res.string.map_markers_unknown_location)),
                style = RiftTheme.typography.bodyHighlighted,
                maxLines = 1,
                modifier = Modifier.alpha(alpha),
            )
            Text(
                text = marker.label,
                style = RiftTheme.typography.bodyPrimary,
                modifier = Modifier
                    .weight(1f)
                    .alpha(alpha),
            )
        }
        if (isExpanded) {
            val defaultGroup = stringResource(Res.string.map_markers_default_group)
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(horizontal = Spacing.medium)
                    .fillMaxWidth(),
            ) {
                RiftDropdownWithLabel(
                    label = stringResource(Res.string.map_markers_group_label),
                    items = (groups.sorted() + listOf(null)).toList(),
                    selectedItem = marker.group,
                    onItemSelected = onGroupChange,
                    getItemName = { it ?: defaultGroup },
                    maxItems = 3,
                    modifier = Modifier
                        .widthIn(max = 170.dp)
                        .padding(end = Spacing.medium),
                )
                RiftButton(
                    text = stringResource(Res.string.map_markers_action_show),
                    type = ButtonType.Secondary,
                    cornerCut = ButtonCornerCut.None,
                    onClick = onShowMarkerClick,
                    modifier = Modifier.padding(end = Spacing.medium),
                )
                RiftButton(
                    text = stringResource(Res.string.map_markers_action_edit),
                    type = ButtonType.Secondary,
                    cornerCut = ButtonCornerCut.None,
                    onClick = onEditMarkerClick,
                    modifier = Modifier.padding(end = Spacing.medium),
                )
                RiftButton(
                    text = stringResource(Res.string.map_markers_action_delete),
                    type = ButtonType.Negative,
                    onClick = onDeleteMarkerClick,
                )
            }
        }
    }
}

private fun getSuggestions(solarSystemsRepository: SolarSystemsRepository, text: String): List<String> {
    solarSystemsRepository.getSystem(text)?.let { return emptyList() }
    return solarSystemsRepository.getSystems()
        .asSequence()
        .map { it.name }
        .filter { it.lowercase().startsWith(text.lowercase()) }
        .take(5)
        .toList()
}
