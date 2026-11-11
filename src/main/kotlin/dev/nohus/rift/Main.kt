package dev.nohus.rift

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.LocalWindowExceptionHandlerFactory
import androidx.compose.ui.window.application
import dev.nohus.rift.compose.theme.EveFontsRepository
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.crash.RiftExceptionHandlerFactory
import dev.nohus.rift.crash.handleFatalException
import dev.nohus.rift.di.koin
import dev.nohus.rift.di.startKoin
import dev.nohus.rift.localization.AppLocale
import dev.nohus.rift.localization.LocalAppLocale
import dev.nohus.rift.logging.initializeLogging
import dev.nohus.rift.notifications.NotificationsController
import dev.nohus.rift.overlay.Overlays
import dev.nohus.rift.singleinstance.SingleInstanceWrapper
import dev.nohus.rift.splash.SplashWindowWrapper
import dev.nohus.rift.startupmigration.migrateWindowsAppData
import dev.nohus.rift.startupwarning.checkAppDataAccess
import dev.nohus.rift.startupwarning.isDiskSpaceFailure
import dev.nohus.rift.startupwarning.showTemporaryStorageFailureWindow
import dev.nohus.rift.tray.RiftTray
import dev.nohus.rift.windowing.WindowManager
import dev.nohus.rift.wizard.WizardWindowWrapper
import io.kamel.image.config.LocalKamelConfig
import kotlin.system.exitProcess

fun main() {
    try {
        if (!migrateWindowsAppData()) return
        initializeLogging()
        startKoin()
        if (!checkAppDataAccess()) return
        koin.get<EveFontsRepository>().initialize()
        koin.get<AppLocale>().initialize()
        application {
            riftApplication()
        }
    } catch (e: Throwable) {
        if (isDiskSpaceFailure(e)) {
            showTemporaryStorageFailureWindow()
            exitProcess(1)
        }
        handleFatalException(e)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ApplicationScope.riftApplication() {
    CompositionLocalProvider(
        LocalAppLocale provides remember { koin.get<AppLocale>().locale },
        LocalKamelConfig provides remember { koin.get() },
        LocalWindowExceptionHandlerFactory provides RiftExceptionHandlerFactory,
    ) {
        val viewModel: ApplicationViewModel = viewModel()
        val windowManager: WindowManager = remember { koin.get() }
        val notificationsController: NotificationsController = remember { koin.get() }
        val state by viewModel.state.collectAsState()

        RiftTheme {
            if (state.isAnotherInstanceDialogShown) {
                SingleInstanceWrapper(
                    onCloseRequest = viewModel::onQuit,
                )
            } else {
                RiftTray(state.isTrayIconShown)
                windowManager.composeWindows()
                if (!state.isSplashScreenShown) {
                    Overlays()
                }
                SplashWindowWrapper(
                    isVisible = state.isSplashScreenShown,
                    onCloseRequest = {},
                )
                WizardWindowWrapper(
                    isVisible = state.isSetupWizardShown,
                    onCloseRequest = viewModel::onWizardCloseRequest,
                )
            }
            notificationsController.composeNotification()
        }

        if (!state.isApplicationRunning) exitApplication()
    }
}
