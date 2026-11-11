package dev.nohus.rift.startupwarning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.common_action_close
import dev.nohus.rift.generated.resources.database_load_failure_message
import dev.nohus.rift.generated.resources.database_load_failure_title
import dev.nohus.rift.generated.resources.window_warning
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val diskSpaceFailureMessages = listOf(
    "There is not enough space on the disk",
    "No space left on device",
    "Disk full",
)

fun isDiskSpaceFailure(throwable: Throwable): Boolean {
    val visited = mutableSetOf<Throwable>()
    return generateSequence(throwable) { it.cause }
        .takeWhile { visited.add(it) }
        .any { cause ->
            val message = cause.message.orEmpty()
            diskSpaceFailureMessages.any { message.contains(it, ignoreCase = true) }
        }
}

fun showTemporaryStorageFailureWindow() {
    val temporaryDirectory = System.getProperty("jna.tmpdir") ?: System.getProperty("java.io.tmpdir")
    application(exitProcessOnExit = false) {
        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.database_load_failure_title),
            icon = painterResource(Res.drawable.window_warning),
            state = rememberWindowState(position = WindowPosition.Aligned(Alignment.Center), width = 560.dp, height = 360.dp),
        ) {
            RiftTheme {
                Column(
                    modifier = Modifier.fillMaxSize().background(RiftTheme.colors.windowBackground).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                ) {
                    Text(
                        text = stringResource(Res.string.database_load_failure_title),
                        style = RiftTheme.typography.headlineHighlighted,
                    )
                    Text(
                        text = stringResource(Res.string.database_load_failure_message, temporaryDirectory),
                        style = RiftTheme.typography.bodyPrimary,
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                    )
                    RiftButton(
                        text = stringResource(Res.string.common_action_close),
                        modifier = Modifier.align(Alignment.End),
                        isSoundEnabled = false,
                        onClick = ::exitApplication,
                    )
                }
            }
        }
    }
}
