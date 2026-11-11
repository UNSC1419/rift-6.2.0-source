package dev.nohus.rift.startupwarning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.RiftCheckboxWithLabel
import dev.nohus.rift.compose.RiftWindow
import dev.nohus.rift.compose.ScrollbarColumn
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.startup_warning_dont_show_again
import dev.nohus.rift.generated.resources.startup_warning_understood
import dev.nohus.rift.generated.resources.startup_warning_window_title
import dev.nohus.rift.generated.resources.window_warning
import dev.nohus.rift.get
import dev.nohus.rift.localization.resolve
import dev.nohus.rift.startupwarning.StartupWarningViewModel.UiState
import dev.nohus.rift.utils.toggle
import dev.nohus.rift.viewModel
import dev.nohus.rift.windowing.WindowManager
import org.jetbrains.compose.resources.stringResource

@Composable
fun StartupWarningWindow(
    inputModel: StartupWarningInputModel?,
    windowState: WindowManager.RiftWindowState,
    onCloseRequest: () -> Unit,
) {
    val viewModel: StartupWarningViewModel = viewModel(inputModel)
    val state by viewModel.state.collectAsState()

    if (state.closeEvent.get()) {
        onCloseRequest()
    }

    val title = state.warnings.singleOrNull()?.title?.resolve() ?: stringResource(Res.string.startup_warning_window_title)
    RiftWindow(
        title = title,
        icon = Res.drawable.window_warning,
        state = windowState,
        onCloseClick = onCloseRequest,
        isResizable = false,
    ) {
        StartupWarningContent(
            state = state,
            onDoneClick = viewModel::onDoneClick,
        )
    }
}

@Composable
private fun StartupWarningContent(
    state: UiState,
    onDoneClick: (dismissedIds: List<String>) -> Unit,
) {
    ScrollbarColumn(
        verticalArrangement = Arrangement.spacedBy(Spacing.large),
        isScrollbarConditional = true,
        modifier = Modifier.heightIn(max = 500.dp),
    ) {
        var checkedIds by remember { mutableStateOf(listOf<String>()) }
        val showTitles = state.warnings.size > 1
        state.warnings.forEach { warning ->
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            ) {
                if (showTitles) {
                    Text(
                        text = warning.title.resolve(),
                        style = RiftTheme.typography.headerHighlighted,
                    )
                }
                Text(
                    text = warning.description.resolve(),
                    style = RiftTheme.typography.bodyPrimary,
                )
                if (warning.detail.isNotEmpty()) {
                    Text(
                        text = warning.detail.map { it.resolve() }.joinToString("\n"),
                        style = RiftTheme.typography.bodyHighlighted,
                    )
                }
                RiftCheckboxWithLabel(
                    label = stringResource(Res.string.startup_warning_dont_show_again),
                    isChecked = warning.id in checkedIds,
                    onCheckedChange = { checkedIds = checkedIds.toggle(warning.id) },
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            Spacer(Modifier.weight(1f))
            RiftButton(
                text = stringResource(Res.string.startup_warning_understood),
                onClick = { onDoneClick(checkedIds) },
            )
        }
    }
}
