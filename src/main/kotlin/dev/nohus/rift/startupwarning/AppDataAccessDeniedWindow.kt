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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.nohus.rift.compose.RiftButton
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.di.koin
import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.app_data_access_denied_description
import dev.nohus.rift.generated.resources.app_data_access_denied_fallback
import dev.nohus.rift.generated.resources.app_data_access_denied_heading
import dev.nohus.rift.generated.resources.app_data_access_denied_quit
import dev.nohus.rift.generated.resources.app_data_access_denied_steps
import dev.nohus.rift.generated.resources.app_data_access_denied_title
import dev.nohus.rift.generated.resources.window_warning
import dev.nohus.rift.singleinstance.SingleInstanceController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun checkAppDataAccess(): Boolean {
    if (koin.get<SingleInstanceController>().hasAppDataAccess()) return true
    showAppDataAccessDeniedWindow()
    return false
}

fun showAppDataAccessDeniedWindow() {
    // This window must work before settings and other Koin services can be initialized
    application(exitProcessOnExit = false) {
        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.app_data_access_denied_title),
            icon = painterResource(Res.drawable.window_warning),
            state = rememberWindowState(position = WindowPosition.Aligned(Alignment.Center), width = 560.dp, height = 460.dp),
            resizable = true,
        ) {
            RiftTheme {
                Column(
                    modifier = Modifier.fillMaxSize().background(RiftTheme.colors.windowBackground).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                ) {
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                    ) {
                        Text(
                            text = stringResource(Res.string.app_data_access_denied_heading),
                            style = RiftTheme.typography.headlineHighlighted,
                        )
                        Text(
                            text = stringResource(Res.string.app_data_access_denied_description),
                            style = RiftTheme.typography.bodyPrimary,
                        )
                        Text(
                            text = stringResource(Res.string.app_data_access_denied_steps),
                            style = RiftTheme.typography.bodyPrimary.copy(lineHeight = 22.sp),
                        )
                        Text(
                            text = stringResource(Res.string.app_data_access_denied_fallback),
                            style = RiftTheme.typography.bodySecondary,
                        )
                    }
                    RiftButton(
                        text = stringResource(Res.string.app_data_access_denied_quit),
                        modifier = Modifier.align(Alignment.End),
                        isSoundEnabled = false,
                        onClick = ::exitApplication,
                    )
                }
            }
        }
    }
}
