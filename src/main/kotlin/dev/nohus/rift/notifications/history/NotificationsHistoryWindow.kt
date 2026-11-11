package dev.nohus.rift.notifications.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.nohus.rift.alerts.list.getAlertDescription
import dev.nohus.rift.compose.ContextMenuItem
import dev.nohus.rift.compose.MulticolorIconType
import dev.nohus.rift.compose.RiftMulticolorIcon
import dev.nohus.rift.compose.RiftTabBar
import dev.nohus.rift.compose.RiftTooltipArea
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.ScrollbarLazyVerticalGrid
import dev.nohus.rift.compose.Tab
import dev.nohus.rift.compose.TitleBarStyle
import dev.nohus.rift.compose.getRelativeTimeText
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.notification_history_compact_mode
import dev.nohus.rift.generated.resources.notification_history_default_reason
import dev.nohus.rift.generated.resources.notification_history_deleted_alert_suffix
import dev.nohus.rift.generated.resources.notification_history_empty
import dev.nohus.rift.generated.resources.notification_history_eve_tab
import dev.nohus.rift.generated.resources.notification_history_group_suffix
import dev.nohus.rift.generated.resources.notification_history_reason_prefix
import dev.nohus.rift.generated.resources.notification_history_rift_tab
import dev.nohus.rift.generated.resources.notification_history_window_title
import dev.nohus.rift.generated.resources.window_event_icon
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.notifications.NotificationContent
import dev.nohus.rift.notifications.eve.EveNotificationsContent
import dev.nohus.rift.notifications.getNotificationTitle
import dev.nohus.rift.settings.persistence.NotificationsTab
import dev.nohus.rift.utils.withColor
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import org.jetbrains.compose.resources.stringResource

@Composable
fun NotificationsHistoryWindow(
    windowState: RiftWindowState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: NotificationsHistoryViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    RiftWindow(
        title = stringResource(Res.string.notification_history_window_title),
        icon = Res.drawable.window_event_icon,
        state = windowState,
        tuneContextMenuItems = listOf(
            ContextMenuItem.CheckboxItem(
                text = stringResource(Res.string.notification_history_compact_mode),
                isSelected = state.isUsingCompactMode,
                onClick = { viewModel.onIsUsingCompactModeChange(!state.isUsingCompactMode) },
            ),
        ),
        onCloseClick = onCloseRequest,
        titleBarStyle = if (state.isUsingCompactMode) TitleBarStyle.Small else TitleBarStyle.Full,
        titleBarContent = { height ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                RiftTabBar(
                    tabs = listOf(
                        Tab(id = 0, title = if (state.isUsingCompactMode) "RIFT".toUiText() else Res.string.notification_history_rift_tab.toUiText(), isCloseable = false),
                        Tab(id = 1, title = if (state.isUsingCompactMode) "EVE".toUiText() else Res.string.notification_history_eve_tab.toUiText(), isCloseable = false),
                    ),
                    selectedTab = state.selectedTab.ordinal,
                    onTabSelected = { tab ->
                        viewModel.onTabSelected(NotificationsTab.entries[tab])
                    },
                    onTabClosed = {},
                    withUnderline = false,
                    withWideTabs = true,
                    fixedHeight = height,
                )
            }
        },
        withContentPadding = false,
    ) {
        when (state.selectedTab) {
            NotificationsTab.Rift -> RiftNotificationsContent(state)
            NotificationsTab.Eve -> EveNotificationsContent(state, windowState, viewModel)
        }
    }
}

@Composable
private fun RiftNotificationsContent(state: NotificationsHistoryViewModel.UiState) {
    if (state.notifications.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = stringResource(Res.string.notification_history_empty),
                textAlign = TextAlign.Center,
                style = RiftTheme.typography.bodySecondary,
            )
        }
    } else {
        val spacing = if (state.isUsingCompactMode) Spacing.small else Spacing.medium
        ScrollbarLazyVerticalGrid(
            columns = GridCells.Adaptive(300.dp),
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            contentPadding = PaddingValues(spacing),
            modifier = Modifier
                .background(RiftTheme.colors.windowBackgroundSecondary)
                .fillMaxSize(),
        ) {
            items(state.notifications) { notification ->
                NotificationHistoryEntry(
                    notification = notification,
                    state = state,
                )
            }
        }
    }
}

@Composable
private fun NotificationHistoryEntry(
    notification: NotificationHistoryItem,
    state: NotificationsHistoryViewModel.UiState,
) {
    Column(
        modifier = Modifier
            .border(1.dp, RiftTheme.colors.borderGrey),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(RiftTheme.colors.backgroundPrimaryDark)
                .padding(Spacing.medium)
                .fillMaxWidth(),
        ) {
            val notificationTitle = getNotificationTitle(notification.notification)
            Text(
                text = notificationTitle,
                style = RiftTheme.typography.headerPrimary,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = getRelativeTimeText(notification.timestamp, state.displayTimezone).resolve(),
                style = RiftTheme.typography.bodySecondary,
            )
            Spacer(Modifier.width(8.dp))
            RiftTooltipArea(
                tooltip = {
                    ReasonTooltip(notification, state)
                },
            ) {
                RiftMulticolorIcon(type = MulticolorIconType.Info)
            }
        }
        Divider(color = RiftTheme.colors.divider)
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier
                .background(RiftTheme.colors.windowBackground)
                .padding(vertical = Spacing.medium),
        ) {
            NotificationContent(
                notification = notification.notification,
                isShowingTitle = false,
                onCloseClick = null,
            )
        }
    }
}

@Composable
private fun ReasonTooltip(
    notification: NotificationHistoryItem,
    state: NotificationsHistoryViewModel.UiState,
) {
    val reasonPrefix = stringResource(Res.string.notification_history_reason_prefix)
    val groupSuffix = stringResource(Res.string.notification_history_group_suffix)
    val deletedAlertSuffix = stringResource(Res.string.notification_history_deleted_alert_suffix)
    val defaultReason = stringResource(Res.string.notification_history_default_reason)
    val reason = notification.reason?.toUiText()?.resolve() ?: defaultReason
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        modifier = Modifier.padding(Spacing.large),
    ) {
        val alert = notification.alert
        if (alert != null) {
            Text(
                text = buildAnnotatedString {
                    withColor(RiftTheme.colors.textSecondary) {
                        append(reasonPrefix)
                    }
                    if (alert.group != null) {
                        withColor(RiftTheme.colors.textPrimary) {
                            append(alert.group)
                        }
                        withColor(RiftTheme.colors.textSecondary) {
                            append(groupSuffix)
                        }
                    }
                },
            )
            Text(
                text = buildAnnotatedString {
                    append(
                        getAlertDescription(
                            alert = alert,
                            characters = state.characters,
                            sounds = state.sounds,
                            labels = state.labels,
                        ),
                    )
                    if (alert.id !in state.alertIds) {
                        withColor(RiftTheme.colors.textSecondary) {
                            append(deletedAlertSuffix)
                        }
                    }
                },
            )
        } else {
            Text(
                text = buildAnnotatedString {
                    withColor(RiftTheme.colors.textSecondary) {
                        append(reasonPrefix)
                    }
                    withColor(RiftTheme.colors.textPrimary) {
                        append(reason)
                    }
                },
            )
        }
    }
}
