package dev.nohus.rift.push

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.LinkText
import dev.nohus.rift.compose.LoadingSpinner
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftMessageDialog
import dev.nohus.rift.compose.RiftTabBar
import dev.nohus.rift.compose.RiftTextField
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.Tab
import dev.nohus.rift.compose.text.parseEveFormattedText
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.push_ntfy_choose_topic_instruction
import dev.nohus.rift.generated.resources.push_ntfy_install_outro
import dev.nohus.rift.generated.resources.push_ntfy_subscribe_instruction
import dev.nohus.rift.generated.resources.push_ntfy_topic_placeholder
import dev.nohus.rift.generated.resources.push_pushover_create_token_action
import dev.nohus.rift.generated.resources.push_pushover_install_outro
import dev.nohus.rift.generated.resources.push_pushover_token_outro
import dev.nohus.rift.generated.resources.push_pushover_token_placeholder
import dev.nohus.rift.generated.resources.push_pushover_user_key_instruction
import dev.nohus.rift.generated.resources.push_pushover_user_key_placeholder
import dev.nohus.rift.generated.resources.push_send_test_action
import dev.nohus.rift.generated.resources.push_service_description
import dev.nohus.rift.generated.resources.push_service_website_action
import dev.nohus.rift.generated.resources.push_setup_account_intro
import dev.nohus.rift.generated.resources.push_setup_account_outro
import dev.nohus.rift.generated.resources.push_setup_complete_instruction
import dev.nohus.rift.generated.resources.push_setup_heading
import dev.nohus.rift.generated.resources.push_setup_install_intro
import dev.nohus.rift.generated.resources.push_setup_platform_separator
import dev.nohus.rift.generated.resources.push_window_title
import dev.nohus.rift.generated.resources.window_loudspeaker_icon
import dev.nohus.rift.push.PushViewModel.UiState
import dev.nohus.rift.utils.openBrowser
import dev.nohus.rift.utils.toURIOrNull
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import org.jetbrains.compose.resources.stringResource

@Composable
fun PushWindow(
    windowState: RiftWindowState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: PushViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    RiftWindow(
        title = stringResource(Res.string.push_window_title),
        icon = Res.drawable.window_loudspeaker_icon,
        state = windowState,
        withContentPadding = false,
        isResizable = false,
        onCloseClick = onCloseRequest,
    ) {
        PushWindowContent(
            state = state,
            onPushoverApiTokenChanged = viewModel::onPushoverApiTokenChanged,
            onPushoverUserKeyChanged = viewModel::onPushoverUserKeyChanged,
            onPushoverSendTest = viewModel::onPushoverSendTest,
            onNtfyTopicChanged = viewModel::onNtfyTopicChanged,
            onNtfySendTest = viewModel::onNtfySendTest,
        )

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
fun PushWindowContent(
    state: UiState,
    onPushoverApiTokenChanged: (String) -> Unit,
    onPushoverUserKeyChanged: (String) -> Unit,
    onPushoverSendTest: () -> Unit,
    onNtfyTopicChanged: (String) -> Unit,
    onNtfySendTest: () -> Unit,
) {
    Column {
        var selectedTab by remember { mutableStateOf(0) }
        RiftTabBar(
            tabs = listOf(
                Tab(0, "ntfy", isCloseable = false),
                Tab(1, "Pushover", isCloseable = false),
            ),
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            onTabClosed = {},
        )
        Box(
            modifier = Modifier.padding(Spacing.medium),
        ) {
            if (selectedTab == 0) {
                Ntfy(
                    state = state,
                    onTopicChanged = onNtfyTopicChanged,
                    onSendTest = onNtfySendTest,
                )
            } else if (selectedTab == 1) {
                Pushover(
                    state = state,
                    onApiTokenChanged = onPushoverApiTokenChanged,
                    onUserKeyChanged = onPushoverUserKeyChanged,
                    onSendTest = onPushoverSendTest,
                )
            }
        }
    }
}

@Composable
private fun Ntfy(
    state: UiState,
    onTopicChanged: (String) -> Unit,
    onSendTest: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        Text(
            text = parseEveFormattedText(stringResource(Res.string.push_service_description, "ntfy")),
            style = RiftTheme.typography.bodyPrimary,
        )
        Text(
            text = stringResource(Res.string.push_setup_heading),
            style = RiftTheme.typography.headerPrimary,
            modifier = Modifier.padding(top = Spacing.medium),
        )
        Row {
            Text(
                text = stringResource(Res.string.push_setup_account_intro),
                style = RiftTheme.typography.bodyPrimary,
            )
            LinkText(
                text = stringResource(Res.string.push_service_website_action, "ntfy"),
                onClick = { "https://ntfy.sh".toURIOrNull()?.openBrowser() },
            )
            Text(
                text = stringResource(Res.string.push_setup_account_outro),
                style = RiftTheme.typography.bodyPrimary,
            )
        }
        Text(
            text = parseEveFormattedText(stringResource(Res.string.push_ntfy_choose_topic_instruction, "Subscribe to a topic", "GENERATE NAME")),
            style = RiftTheme.typography.bodyPrimary,
        )
        var topic by remember { mutableStateOf(state.ntfyTopic) }
        RiftTextField(
            text = topic,
            placeholder = stringResource(Res.string.push_ntfy_topic_placeholder),
            onTextChanged = {
                topic = it
                onTopicChanged(it)
            },
        )
        Text(
            text = parseEveFormattedText(stringResource(Res.string.push_ntfy_subscribe_instruction, "SUBSCRIBE")),
            style = RiftTheme.typography.bodyPrimary,
        )

        FlowRow {
            Text(
                text = stringResource(Res.string.push_setup_install_intro),
                style = RiftTheme.typography.bodyPrimary,
            )
            LinkText(
                text = "Android",
                onClick = { "https://play.google.com/store/apps/details?id=io.heckel.ntfy".toURIOrNull()?.openBrowser() },
            )
            Text(
                text = stringResource(Res.string.push_setup_platform_separator),
                style = RiftTheme.typography.bodyPrimary,
            )
            LinkText(
                text = "iOS",
                onClick = { "https://apps.apple.com/us/app/ntfy/id1625396347".toURIOrNull()?.openBrowser() },
            )
            Text(
                text = " ",
                style = RiftTheme.typography.bodyPrimary,
            )
            Text(
                text = stringResource(Res.string.push_ntfy_install_outro),
                style = RiftTheme.typography.bodyPrimary,
            )
        }
        Text(
            text = stringResource(Res.string.push_setup_complete_instruction),
            style = RiftTheme.typography.bodyPrimary,
        )
        Spacer(Modifier.weight(1f))
        AnimatedContent(
            targetState = state.isLoading,
            modifier = Modifier.align(Alignment.End).padding(top = Spacing.medium),
        ) { isLoading ->
            if (isLoading) {
                LoadingSpinner(modifier = Modifier.size(36.dp))
            } else {
                RiftButton(
                    text = stringResource(Res.string.push_send_test_action),
                    onClick = onSendTest,
                )
            }
        }
    }
}

@Composable
private fun Pushover(
    state: UiState,
    onApiTokenChanged: (String) -> Unit,
    onUserKeyChanged: (String) -> Unit,
    onSendTest: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        Text(
            text = parseEveFormattedText(stringResource(Res.string.push_service_description, "Pushover")),
            style = RiftTheme.typography.bodyPrimary,
        )
        Text(
            text = stringResource(Res.string.push_setup_heading),
            style = RiftTheme.typography.headerPrimary,
            modifier = Modifier.padding(top = Spacing.medium),
        )
        Row {
            Text(
                text = stringResource(Res.string.push_setup_account_intro),
                style = RiftTheme.typography.bodyPrimary,
            )
            LinkText(
                text = stringResource(Res.string.push_service_website_action, "Pushover"),
                onClick = { "https://pushover.net".toURIOrNull()?.openBrowser() },
            )
            Text(
                text = stringResource(Res.string.push_setup_account_outro),
                style = RiftTheme.typography.bodyPrimary,
            )
        }
        Text(
            text = stringResource(Res.string.push_pushover_user_key_instruction),
            style = RiftTheme.typography.bodyPrimary,
        )
        var userKey by remember { mutableStateOf(state.pushoverApiKey) }
        RiftTextField(
            text = userKey,
            placeholder = stringResource(Res.string.push_pushover_user_key_placeholder),
            onTextChanged = {
                userKey = it
                onUserKeyChanged(it)
            },
        )
        Row {
            Text(
                text = "3. ",
                style = RiftTheme.typography.bodyPrimary,
            )
            LinkText(
                text = stringResource(Res.string.push_pushover_create_token_action),
                onClick = { "https://pushover.net/apps/build".toURIOrNull()?.openBrowser() },
            )
            Text(
                text = stringResource(Res.string.push_pushover_token_outro),
                style = RiftTheme.typography.bodyPrimary,
            )
        }
        var apiToken by remember { mutableStateOf(state.pushoverApiToken) }
        RiftTextField(
            text = apiToken,
            placeholder = stringResource(Res.string.push_pushover_token_placeholder),
            onTextChanged = {
                apiToken = it
                onApiTokenChanged(it)
            },
        )
        Row {
            Text(
                text = stringResource(Res.string.push_setup_install_intro),
                style = RiftTheme.typography.bodyPrimary,
            )
            LinkText(
                text = "Android",
                onClick = { "https://pushover.net/clients/android".toURIOrNull()?.openBrowser() },
            )
            Text(
                text = stringResource(Res.string.push_setup_platform_separator),
                style = RiftTheme.typography.bodyPrimary,
            )
            LinkText(
                text = "iOS",
                onClick = { "https://pushover.net/clients/ios".toURIOrNull()?.openBrowser() },
            )
            Text(
                text = stringResource(Res.string.push_pushover_install_outro),
                style = RiftTheme.typography.bodyPrimary,
            )
        }
        Text(
            text = stringResource(Res.string.push_setup_complete_instruction),
            style = RiftTheme.typography.bodyPrimary,
        )
        Spacer(Modifier.weight(1f))
        AnimatedContent(
            targetState = state.isLoading,
            modifier = Modifier.align(Alignment.End).padding(top = Spacing.medium),
        ) { isLoading ->
            if (isLoading) {
                LoadingSpinner(modifier = Modifier.size(36.dp))
            } else {
                RiftButton(
                    text = stringResource(Res.string.push_send_test_action),
                    onClick = onSendTest,
                )
            }
        }
    }
}
