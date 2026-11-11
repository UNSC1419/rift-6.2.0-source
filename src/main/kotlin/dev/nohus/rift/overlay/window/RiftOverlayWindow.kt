package dev.nohus.rift.overlay.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.AwtWindow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowScope
import dev.nohus.rift.compose.UiScaleController
import dev.nohus.rift.di.koin
import dev.nohus.rift.localization.AppLocale
import dev.nohus.rift.localization.LocalAppLocale
import dev.nohus.rift.utils.OperatingSystem
import dev.nohus.rift.utils.evewindows.EveWindowId
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import io.kamel.image.config.LocalKamelConfig
import java.awt.Dimension
import java.awt.Point
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.SwingUtilities
import java.awt.Window as AwtWindowHost

@Composable
fun RiftOverlayWindow(
    windowState: RiftWindowState,
    eveWindowId: EveWindowId,
    isEveActive: Boolean,
    eveProcessId: Int?,
    eveWindowLevel: Int?,
    onMouseExited: () -> Unit,
    onNativeWindowInvalidated: (OverlayWindowFailureReason) -> Unit,
    content: @Composable WindowScope.() -> Unit,
) {
    val operatingSystem: OperatingSystem = remember { koin.get() }
    NativeRiftOverlayWindow(
        windowState, eveWindowId, operatingSystem, isEveActive, eveProcessId, eveWindowLevel,
        onCloseRequest = {}, onMouseExited, onNativeWindowInvalidated, content,
    )
}

@Composable
private fun NativeRiftOverlayWindow(
    windowState: RiftWindowState,
    eveWindowId: EveWindowId,
    operatingSystem: OperatingSystem,
    isEveActive: Boolean,
    eveProcessId: Int?,
    eveWindowLevel: Int?,
    onCloseRequest: () -> Unit,
    onMouseExited: () -> Unit,
    onNativeWindowInvalidated: (OverlayWindowFailureReason) -> Unit,
    content: @Composable WindowScope.() -> Unit,
) {
    val size = windowState.windowState.size
    val position = windowState.windowState.position
    val platform = remember(operatingSystem) {
        when (operatingSystem) {
            OperatingSystem.Linux -> LinuxOverlayPlatform
            OperatingSystem.MacOs -> MacOverlayPlatform
            OperatingSystem.Windows -> WindowsOverlayPlatform
        }
    }
    val stacking = remember(eveWindowId, eveProcessId, platform) {
        platform.createStacking(eveWindowId, eveProcessId, eveWindowLevel)
    }
    AwtWindow(
        create = {
            createNativeOverlayWindow(
                windowState,
                eveWindowId,
                platform,
                stacking,
                onCloseRequest,
                onMouseExited,
                onNativeWindowInvalidated,
                content,
            )
        },
        dispose = { window ->
            stacking?.onClosed()
            window.dispose()
        },
        update = { window ->
            platform.updateStacking(stacking, eveWindowLevel)
            stacking?.update(window, isEveActive)
            val dimension = Dimension(size.width.value.toInt(), size.height.value.toInt())
            if (window.size != dimension) window.size = dimension
            if (position is WindowPosition.Absolute) {
                val location = Point(position.x.value.toInt(), position.y.value.toInt())
                if (window.location != location) window.location = location
            }
        },
    )
}

private fun createNativeOverlayWindow(
    windowState: RiftWindowState,
    eveWindowId: EveWindowId,
    platform: OverlayWindowPlatform,
    stacking: OverlayStacking?,
    onCloseRequest: () -> Unit,
    onMouseExited: () -> Unit,
    onNativeWindowInvalidated: (OverlayWindowFailureReason) -> Unit,
    content: @Composable WindowScope.() -> Unit,
): AwtWindowHost {
    val window = AwtWindowHost(null, platform.getGraphicsConfiguration()).apply {
        background = platform.background
        platform.setInitialBounds(this, windowState)
        addWindowListener(object : WindowAdapter() {
            override fun windowOpened(event: WindowEvent) {
                stacking?.onOpened(event.window)
            }
            override fun windowClosing(event: WindowEvent) = onCloseRequest()
        })
        addComponentListener(object : ComponentAdapter() {
            override fun componentMoved(event: ComponentEvent) {
                val position = WindowPosition(x.dp, y.dp)
                if (windowState.windowState.position != position) windowState.windowState.position = position
            }

            override fun componentResized(event: ComponentEvent) {
                val size = DpSize(width.dp, height.dp)
                if (windowState.windowState.size != size) windowState.windowState.size = size
            }
        })
    }
    val size = windowState.windowState.size
    val uiScaleController: UiScaleController = koin.get()
    val appLocale: AppLocale = koin.get()
    val windowScope = object : WindowScope {
        override val window: AwtWindowHost = window
    }
    val component = OffscreenComposeComponent(
        window,
        eveWindowId,
        platform,
        size.width.value.toInt(),
        size.height.value.toInt(),
        onMouseExited,
        onNativeWindowInvalidated,
    ) {
        uiScaleController.withScale {
            with(windowScope) {
                CompositionLocalProvider(
                    LocalAppLocale provides appLocale.locale,
                    LocalKamelConfig provides remember { koin.get() },
                ) {
                    content()
                }
            }
        }
    }
    window.add(component)
    if (platform.shouldUpdateSizeOnMove) {
        window.addComponentListener(object : ComponentAdapter() {
            override fun componentMoved(event: ComponentEvent) = component.updateSceneSize()
            override fun componentResized(event: ComponentEvent) = component.updateSceneSize()
        })
    }
    window.addNotify()
    try {
        if (platform.attach(window, eveWindowId, stacking)) {
            component.startRendering()
        } else {
            SwingUtilities.invokeLater { onNativeWindowInvalidated(OverlayWindowFailureReason.NativeFailure) }
        }
    } catch (error: Throwable) {
        stacking?.onClosed()
        window.dispose()
        throw error
    }
    return window
}
