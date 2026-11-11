package dev.nohus.rift.notifications.eve

import org.jetbrains.compose.resources.stringResource
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.repositories.getDisplayText
import dev.nohus.rift.generated.resources.common_action_close
import dev.nohus.rift.generated.resources.notification_history_filters_action
import dev.nohus.rift.generated.resources.notification_history_link_hint
import dev.nohus.rift.generated.resources.notification_history_eve_empty
import dev.nohus.rift.generated.resources.notification_history_filters_empty
import dev.nohus.rift.generated.resources.notification_history_settings_title
import dev.nohus.rift.generated.resources.notification_history_history_visibility_title
import dev.nohus.rift.generated.resources.notification_history_history_visibility_description
import dev.nohus.rift.generated.resources.notification_history_alert_visibility_title
import dev.nohus.rift.generated.resources.notification_history_alert_visibility_description
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.onClick
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.rememberWindowState
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.ExpandChevron
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftCheckbox
import dev.nohus.rift.compose.RiftDialog
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.ScrollbarLazyVerticalGrid
import dev.nohus.rift.compose.hoverBackground
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.notifications_settingspopupicon
import dev.nohus.rift.generated.resources.notifications_settingsvisibleicon
import dev.nohus.rift.generated.resources.window_settings
import dev.nohus.rift.notifications.history.NotificationsHistoryViewModel
import dev.nohus.rift.repositories.NotificationTypeGroup
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import org.jetbrains.compose.resources.painterResource

@Composable
fun WindowScope.EveNotificationsContent(
    state: NotificationsHistoryViewModel.UiState,
    parentWindowState: RiftWindowState,
    viewModel: NotificationsHistoryViewModel,
) {
    val notifications = state.eveNotifications
    val visibleNotifications = notifications.notifications
        .filter { it.type !in state.excludedEveNotificationTypes }
        .sortedByDescending { it.timestamp }
    val gridState = rememberLazyGridState()
    LaunchedEffect(visibleNotifications.firstOrNull()?.notificationId) {
        if (visibleNotifications.isNotEmpty()) {
            gridState.animateScrollToItem(0)
        }
    }
    var isFilterDialogOpen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier.padding(start = Spacing.medium, end = Spacing.medium, bottom = Spacing.medium, top = Spacing.small),
        ) {
            RiftButton(
                text = stringResource(Res.string.notification_history_filters_action),
                type = ButtonType.Secondary,
                isCompact = true,
                onClick = { isFilterDialogOpen = true },
            )
            if (visibleNotifications.isNotEmpty()) {
                Text(
                    text = stringResource(Res.string.notification_history_link_hint),
                    style = RiftTheme.typography.bodySecondary
                )
            }
        }
        if (visibleNotifications.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f).fillMaxWidth(),
            ) {
                Text(
                    text = if (notifications.notifications.isEmpty()) stringResource(Res.string.notification_history_eve_empty) else stringResource(Res.string.notification_history_filters_empty),
                    style = RiftTheme.typography.bodySecondary,
                )
            }
        } else {
            ScrollbarLazyVerticalGrid(
                columns = GridCells.Adaptive(300.dp),
                gridState = gridState,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                contentPadding = PaddingValues(Spacing.medium),
                modifier = Modifier
                    .background(RiftTheme.colors.windowBackgroundSecondary)
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                items(items = visibleNotifications, key = { it.notificationId }) { notification ->
                    EveNotificationCard(
                        notification = notification,
                        typeDetails = notifications.typeDetails,
                        notificationTypeTitles = state.eveNotificationTypeTitles,
                        displayTimezone = state.displayTimezone,
                        onHideType = { type -> viewModel.onEveNotificationTypesChecked(setOf(type), false) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
    if (isFilterDialogOpen) {
        EveNotificationsFilterDialog(
            parentWindowState = parentWindowState,
            notificationTypeTitles = state.eveNotificationTypeTitles,
            notificationTypeGroups = state.eveNotificationTypeGroups,
            notifications = notifications.notifications,
            excludedTypes = state.excludedEveNotificationTypes,
            excludedAlertTypes = state.excludedEveNotificationAlertTypes,
            onTypesChecked = viewModel::onEveNotificationTypesChecked,
            onAlertTypesChecked = viewModel::onEveNotificationAlertTypesChecked,
            onDismiss = { isFilterDialogOpen = false },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WindowScope.EveNotificationsFilterDialog(
    parentWindowState: RiftWindowState,
    notificationTypeTitles: Map<String, String>,
    notificationTypeGroups: List<NotificationTypeGroup>,
    notifications: List<EveNotification>,
    excludedTypes: Set<String>,
    excludedAlertTypes: Set<String>,
    onTypesChecked: (Set<String>, Boolean) -> Unit,
    onAlertTypesChecked: (Set<String>, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val counts = remember(notifications) { notifications.groupingBy { it.type }.eachCount() }
    var expandedGroups by remember { mutableStateOf<Set<String>>(emptySet()) }
    val groups = remember(notificationTypeGroups, counts) {
        val groupedTypes = notificationTypeGroups.flatMap { it.types }.toSet()
        notificationTypeGroups.map { group ->
            group.name to if (group.name == "Miscellaneous") {
                group.types + (counts.keys - groupedTypes)
            } else {
                group.types
            }
        }
    }
    RiftDialog(
        title = stringResource(Res.string.notification_history_settings_title),
        icon = Res.drawable.window_settings,
        parentState = parentWindowState,
        state = rememberWindowState(width = 400.dp, height = 500.dp),
        onCloseClick = onDismiss,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row {
                Spacer(Modifier.weight(1f))
                RiftTooltipArea(
                    tooltip = {
                        Column(
                            modifier = Modifier.padding(Spacing.large)
                        ) {
                            Text(
                                text = stringResource(Res.string.notification_history_history_visibility_title),
                                style = RiftTheme.typography.bodyPrimary,
                            )
                            Text(
                                text = stringResource(Res.string.notification_history_history_visibility_description),
                                style = RiftTheme.typography.bodySecondary,
                            )
                        }
                    }
                ) {
                    Image(
                        painter = painterResource(Res.drawable.notifications_settingsvisibleicon),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(8.5.dp))
                RiftTooltipArea(
                    tooltip = {
                        Column(
                            modifier = Modifier.padding(Spacing.large)
                        ) {
                            Text(
                                text = stringResource(Res.string.notification_history_alert_visibility_title),
                                style = RiftTheme.typography.bodyPrimary,
                            )
                            Text(
                                text = stringResource(Res.string.notification_history_alert_visibility_description),
                                style = RiftTheme.typography.bodySecondary,
                            )
                        }
                    }
                ) {
                    Image(
                        painter = painterResource(Res.drawable.notifications_settingspopupicon),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(17.dp))
            }
            ScrollbarColumn(
                modifier = Modifier.weight(1f).heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
                scrollbarModifier = Modifier.padding(start = Spacing.medium),
            ) {
                groups.forEach { [groupName, groupTypes] ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.verySmall),
                        modifier = Modifier.animateContentSize()
                    ) {
                        val groupCount = groupTypes.sumOf { counts[it] ?: 0 }
                        val isGroupChecked = groupTypes.none { it in excludedTypes }
                        val isGroupAlertChecked = groupTypes.none { it in excludedAlertTypes }
                        val isExpanded = groupName in expandedGroups
                        val groupStyle =
                            if (groupCount > 0) RiftTheme.typography.bodyPrimary else RiftTheme.typography.bodySecondary
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .hoverBackground()
                                .fillMaxWidth()
                                .onClick {
                                    expandedGroups =
                                        if (isExpanded) expandedGroups - groupName else expandedGroups + groupName
                                },
                        ) {
                            ExpandChevron(isExpanded = isExpanded)
                            Text(
                                text = notificationTypeGroups.first { it.name == groupName }.getDisplayText().resolve(),
                                style = groupStyle,
                                modifier = Modifier.weight(1f),
                            )
                            Text(text = groupCount.toString(), style = groupStyle)
                            RiftCheckbox(
                                isChecked = isGroupChecked,
                                onCheckedChange = { isChecked -> onTypesChecked(groupTypes, isChecked) },
                                modifier = Modifier.padding(start = Spacing.small),
                            )
                            RiftCheckbox(
                                isChecked = isGroupAlertChecked,
                                onCheckedChange = { isChecked -> onAlertTypesChecked(groupTypes, isChecked) },
                                modifier = Modifier.padding(start = Spacing.small),
                            )
                        }
                        if (isExpanded) {
                            groupTypes.groupBy { notificationTypeTitles[it] ?: it }
                                .toSortedMap()
                                .forEach { [title, types] ->
                                    val count = types.sumOf { counts[it] ?: 0 }
                                    val isChecked = types.none { it in excludedTypes }
                                    val isAlertChecked = types.none { it in excludedAlertTypes }
                                    val style =
                                        if (count > 0) RiftTheme.typography.bodyPrimary else RiftTheme.typography.bodySecondary
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth().padding(start = Spacing.veryLarge),
                                    ) {
                                        Text(
                                            text = title,
                                            style = style,
                                            modifier = Modifier.weight(1f),
                                        )
                                        Text(text = count.toString(), style = style)
                                        RiftCheckbox(
                                            isChecked = isChecked,
                                            onCheckedChange = { checked -> onTypesChecked(types.toSet(), checked) },
                                            modifier = Modifier.padding(start = Spacing.small),
                                        )
                                        RiftCheckbox(
                                            isChecked = isAlertChecked,
                                            onCheckedChange = { checked -> onAlertTypesChecked(types.toSet(), checked) },
                                            modifier = Modifier.padding(start = Spacing.small),
                                        )
                                    }
                                }
                        }
                    }
                }
            }
            RiftButton(
                text = stringResource(Res.string.common_action_close),
                type = ButtonType.Secondary,
                cornerCut = ButtonCornerCut.Both,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.medium),
            )
        }
    }
}
