package dev.nohus.rift.alerts.creategroup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.rememberWindowState
import dev.nohus.rift.compose.ButtonCornerCut
import dev.nohus.rift.compose.ButtonType
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftDialog
import dev.nohus.rift.compose.RiftTextField
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.common_action_cancel
import dev.nohus.rift.generated.resources.groups_action_create
import dev.nohus.rift.generated.resources.groups_action_rename
import dev.nohus.rift.generated.resources.groups_create_title
import dev.nohus.rift.generated.resources.groups_name_placeholder
import dev.nohus.rift.generated.resources.groups_rename_title
import dev.nohus.rift.generated.resources.window_smallfolder
import dev.nohus.rift.windowing.WindowManager
import org.jetbrains.compose.resources.stringResource

@Composable
fun WindowScope.CreateGroupDialog(
    inputModel: CreateGroupInputModel,
    parentWindowState: WindowManager.RiftWindowState,
    description: String,
    onDismiss: () -> Unit,
    onConfirmClick: (name: String) -> Unit,
) {
    val title = when (inputModel) {
        CreateGroupInputModel.New -> stringResource(Res.string.groups_create_title)
        is CreateGroupInputModel.Rename -> stringResource(Res.string.groups_rename_title)
    }
    RiftDialog(
        title = title,
        icon = Res.drawable.window_smallfolder,
        parentState = parentWindowState,
        state = rememberWindowState(width = 300.dp, height = Dp.Unspecified),
        onCloseClick = onDismiss,
    ) {
        CreateGroupDialogContent(
            description = description,
            inputModel = inputModel,
            onCancelClick = onDismiss,
            onConfirmClick = onConfirmClick,
        )
    }
}

@Composable
private fun CreateGroupDialogContent(
    description: String,
    inputModel: CreateGroupInputModel,
    onCancelClick: () -> Unit,
    onConfirmClick: (name: String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
    ) {
        if (inputModel is CreateGroupInputModel.New) {
            Text(
                text = description,
            )
        }
        val initialText = (inputModel as? CreateGroupInputModel.Rename)?.name ?: ""
        var text by remember { mutableStateOf(initialText) }
        RiftTextField(
            text = text,
            placeholder = stringResource(Res.string.groups_name_placeholder),
            onTextChanged = { text = it.take(24) },
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            RiftButton(
                text = stringResource(Res.string.common_action_cancel),
                cornerCut = ButtonCornerCut.BottomLeft,
                type = ButtonType.Secondary,
                onClick = onCancelClick,
                modifier = Modifier.weight(1f),
            )
            val label = when (inputModel) {
                CreateGroupInputModel.New -> stringResource(Res.string.groups_action_create)
                is CreateGroupInputModel.Rename -> stringResource(Res.string.groups_action_rename)
            }
            RiftButton(
                text = label,
                onClick = { onConfirmClick(text) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}
