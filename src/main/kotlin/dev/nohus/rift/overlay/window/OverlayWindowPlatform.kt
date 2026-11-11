package dev.nohus.rift.overlay.window

import androidx.compose.ui.geometry.Offset
import dev.nohus.rift.utils.evewindows.EveWindowId
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import java.awt.Color
import java.awt.GraphicsConfiguration
import java.awt.GraphicsDevice.WindowTranslucency
import java.awt.GraphicsEnvironment
import java.awt.Window
import java.awt.event.MouseEvent
import javax.swing.JComponent

internal interface OverlayWindowPlatform {
    val background: Color
    val maxFramePixels: Long get() = 16_000_000L
    val shouldUpdateSizeOnMove: Boolean get() = false
    val shouldPollSize: Boolean get() = false

    fun getGraphicsConfiguration(): GraphicsConfiguration? = null
    fun createStacking(eveWindowId: EveWindowId, eveProcessId: Int?, eveWindowLevel: Int?): OverlayStacking? = null
    fun updateStacking(stacking: OverlayStacking?, eveWindowLevel: Int?) = Unit
    fun setInitialBounds(window: Window, windowState: RiftWindowState) = Unit
    fun attach(window: Window, eveWindowId: EveWindowId, stacking: OverlayStacking?): Boolean
    fun createInputMask(window: Window): OverlayInputMask? = null
    fun getSceneSize(window: Window, component: JComponent): Pair<Int, Int>? = component.width to component.height
    fun getInitialDensity(window: Window): Float = 1f
    fun getSceneDensity(window: Window, physicalWidth: Int): Float = 1f
    fun createFrameOutput(
        component: JComponent,
        window: Window,
        eveWindowId: EveWindowId,
        width: Int,
        height: Int,
        inputMask: OverlayInputMask?,
    ): OverlayFrameOutput
    fun isOutputValid(output: OverlayFrameOutput?): Boolean = true
    fun getPointerPosition(event: MouseEvent, component: JComponent, sceneWidth: Int, sceneHeight: Int): Offset = Offset(event.x.toFloat(), event.y.toFloat())
}

internal fun getTranslucentGraphicsConfiguration(): GraphicsConfiguration? {
    val graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
    val device = graphicsEnvironment.defaultScreenDevice
    if (!device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) return null
    return device.configurations.firstOrNull { it.isTranslucencyCapable }
}
