package dev.nohus.rift.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.rememberWindowState
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.common_action_ok
import dev.nohus.rift.generated.resources.window_info
import dev.nohus.rift.generated.resources.window_warning
import dev.nohus.rift.localization.UiText
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.localization.toUiText
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import org.jetbrains.compose.resources.stringResource

data class DialogMessage(
    val title: UiText,
    val message: UiText,
    val type: MessageDialogType,
) {
    constructor(title: String, message: String, type: MessageDialogType) : this(
        title.toUiText(),
        message.toUiText(),
        type,
    )
}

enum class MessageDialogType {
    Info,
    Warning,
}

@Composable
fun WindowScope.RiftMessageDialog(
    dialog: DialogMessage,
    parentWindowState: RiftWindowState,
    onDismiss: () -> Unit,
) {
    RiftMessageDialog(
        title = dialog.title.resolve(),
        message = dialog.message.resolve(),
        type = dialog.type,
        parentWindowState = parentWindowState,
        onDismiss = onDismiss,
    )
}

@Composable
fun WindowScope.RiftMessageDialog(
    title: String,
    message: String,
    type: MessageDialogType,
    parentWindowState: RiftWindowState,
    onDismiss: () -> Unit,
) {
    val icon = when (type) {
        MessageDialogType.Info -> Res.drawable.window_info
        MessageDialogType.Warning -> Res.drawable.window_warning
    }
    RiftDialog(
        title = title,
        icon = icon,
        parentState = parentWindowState,
        state = rememberWindowState(width = 300.dp, height = Dp.Unspecified),
        onCloseClick = onDismiss,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = message,
                modifier = Modifier.heightIn(min = 60.dp),
            )
            RiftButton(
                text = stringResource(Res.string.common_action_ok),
                cornerCut = ButtonCornerCut.Both,
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.large),
            )
        }
    }
}
