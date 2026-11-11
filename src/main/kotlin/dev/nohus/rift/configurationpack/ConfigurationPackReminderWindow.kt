package dev.nohus.rift.configurationpack

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.configurationpack.ConfigurationPackReminderViewModel.UiState
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.common_action_confirm
import dev.nohus.rift.generated.resources.common_action_done
import dev.nohus.rift.generated.resources.settings_other_settings_default_text
import dev.nohus.rift.generated.resources.settings_pack_decline
import dev.nohus.rift.generated.resources.settings_pack_enabled
import dev.nohus.rift.generated.resources.settings_pack_offer
import dev.nohus.rift.generated.resources.settings_pack_reminder_title
import dev.nohus.rift.generated.resources.window_info
import dev.nohus.rift.get
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfigurationPackReminderWindow(
    windowState: WindowManager.RiftWindowState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: ConfigurationPackReminderViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    if (state.closeEvent.get()) {
        onCloseRequest()
    }

    RiftWindow(
        title = stringResource(Res.string.settings_pack_reminder_title),
        icon = Res.drawable.window_info,
        state = windowState,
        onCloseClick = onCloseRequest,
        isResizable = false,
    ) {
        ConfigurationPackReminderContent(
            state = state,
            onCancelClick = viewModel::onCancelClick,
            onConfirmClick = viewModel::onConfirmClick,
            onDoneClick = viewModel::onDoneClick,
        )
    }
}

@Composable
private fun ConfigurationPackReminderContent(
    state: UiState,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onDoneClick: () -> Unit,
) {
    val defaultPackName = stringResource(Res.string.settings_other_settings_default_text)
    val packName = state.suggestedConfigurationPack?.displayName ?: defaultPackName
    AnimatedContent(targetState = state.isSuccessful) { isSuccessful ->
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            if (isSuccessful) {
                Text(
                    text = stringResource(Res.string.settings_pack_enabled, packName),
                    style = RiftTheme.typography.bodyPrimary,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                ) {
                    Spacer(Modifier.weight(1f))
                    RiftButton(
                        text = stringResource(Res.string.common_action_done),
                        onClick = onDoneClick,
                    )
                }
            } else {
                Text(
                    text = stringResource(Res.string.settings_pack_offer, packName),
                    style = RiftTheme.typography.bodyPrimary,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                ) {
                    Spacer(Modifier.weight(1f))
                    RiftButton(
                        text = stringResource(Res.string.settings_pack_decline),
                        type = ButtonType.Secondary,
                        cornerCut = ButtonCornerCut.None,
                        onClick = onCancelClick,
                    )
                    RiftButton(
                        text = stringResource(Res.string.common_action_confirm),
                        onClick = onConfirmClick,
                    )
                }
            }
        }
    }
}
