package dev.nohus.rift.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.ComposeWindow
import com.sun.jna.NativeLibrary
import com.sun.jna.Pointer
import dev.nohus.rift.di.koin
import dev.nohus.rift.utils.OperatingSystem
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.beans.PropertyChangeListener
import javax.swing.SwingUtilities

private val logger = KotlinLogging.logger {}

@Composable
internal fun MacAlwaysAboveHandler(window: ComposeWindow) {
    val operatingSystem: OperatingSystem = remember { koin.get() }
    if (operatingSystem != OperatingSystem.MacOs) return

    DisposableEffect(window) {
        val controller = MacAlwaysAboveController(window)
        controller.start()
        onDispose { controller.close() }
    }
}

internal class MacAlwaysAboveController(private val window: ComposeWindow) {
    private var isClosed = false
    private var hasReportedFailure = false
    private val propertyListener = PropertyChangeListener { updateLevel() }
    private val componentListener = object : ComponentAdapter() {
        override fun componentShown(event: ComponentEvent) = updateLevel()
    }
    private val windowListener = object : WindowAdapter() {
        override fun windowOpened(event: WindowEvent) = updateLevel()
        override fun windowDeiconified(event: WindowEvent) = updateLevel()
        override fun windowActivated(event: WindowEvent) = updateLevel()
    }

    fun start() {
        window.addPropertyChangeListener("alwaysOnTop", propertyListener)
        window.addComponentListener(componentListener)
        window.addWindowListener(windowListener)
        updateLevel()
    }

    fun close() {
        isClosed = true
        window.removePropertyChangeListener("alwaysOnTop", propertyListener)
        window.removeComponentListener(componentListener)
        window.removeWindowListener(windowListener)
    }

    private fun updateLevel() {
        // Compose/AWT must apply their window properties before the native override.
        SwingUtilities.invokeLater {
            if (isClosed || !window.isDisplayable) return@invokeLater
            try {
                val handle = window.windowHandle.takeIf { it != 0L } ?: return@invokeLater
                MacAlwaysAboveLevel.setLevel(handle, window.isAlwaysOnTop)
            } catch (exception: Exception) {
                reportFailure(exception)
            } catch (error: LinkageError) {
                reportFailure(error)
            }
        }
    }

    private fun reportFailure(error: Throwable) {
        if (!hasReportedFailure) {
            hasReportedFailure = true
            logger.error(error) { "Could not set the macOS always-above window level" }
        }
    }
}

private object MacAlwaysAboveLevel {
    private const val CG_NORMAL_WINDOW_LEVEL_KEY = 4
    private const val CG_STATUS_WINDOW_LEVEL_KEY = 9

    private val objc = NativeLibrary.getInstance("objc")
    private val sendMessage = objc.getFunction("objc_msgSend")
    private val setLevel = objc.getFunction("sel_registerName").invokePointer(arrayOf("setLevel:"))
    private val coreGraphics = NativeLibrary.getInstance("CoreGraphics")
    private val windowLevelForKey = coreGraphics.getFunction("CGWindowLevelForKey")
    private val normalLevel = windowLevelForKey.invokeInt(arrayOf(CG_NORMAL_WINDOW_LEVEL_KEY))
    private val alwaysAboveLevel = windowLevelForKey.invokeInt(arrayOf(CG_STATUS_WINDOW_LEVEL_KEY)) + 1
    private val runOnMainThread = Class.forName("sun.lwawt.macosx.LWCToolkit")
        .getMethod("performOnMainThreadAndWait", Runnable::class.java)
        .apply { isAccessible = true }

    fun setLevel(handle: Long, enabled: Boolean) {
        val level = if (enabled) alwaysAboveLevel else normalLevel
        runOnMainThread.invoke(
            null,
            Runnable {
                sendMessage.invokeVoid(arrayOf(Pointer(handle), setLevel, level.toLong()))
            },
        )
    }
}
