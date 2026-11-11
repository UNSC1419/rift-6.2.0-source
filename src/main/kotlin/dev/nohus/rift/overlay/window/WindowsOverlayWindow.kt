package dev.nohus.rift.overlay.window

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.window.WindowPosition
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.GDI32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.DWORD
import com.sun.jna.platform.win32.WinDef.HBITMAP
import com.sun.jna.platform.win32.WinDef.HDC
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.POINT
import com.sun.jna.platform.win32.WinDef.RECT
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO
import com.sun.jna.platform.win32.WinNT.HANDLE
import com.sun.jna.platform.win32.WinUser.BLENDFUNCTION
import com.sun.jna.platform.win32.WinUser.SIZE
import com.sun.jna.ptr.PointerByReference
import dev.nohus.rift.utils.evewindows.EveWindowId
import dev.nohus.rift.utils.evewindows.windows.WindowsDwmApi
import dev.nohus.rift.windowing.WindowManager.RiftWindowState
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Surface
import java.awt.Color
import java.awt.Window
import java.awt.event.MouseEvent
import javax.swing.JComponent
import kotlin.math.roundToInt

private val logger = KotlinLogging.logger {}

internal object WindowsOverlayPlatform : OverlayWindowPlatform {
    override val background = Color.BLACK
    override val maxFramePixels = 64_000_000L
    override val shouldUpdateSizeOnMove = true
    override val shouldPollSize = true

    override fun setInitialBounds(window: Window, windowState: RiftWindowState) {
        val size = windowState.windowState.size
        window.setSize(size.width.value.toInt(), size.height.value.toInt())
        val position = windowState.windowState.position
        if (position is WindowPosition.Absolute) {
            window.setLocation(position.x.value.toInt(), position.y.value.toInt())
        }
    }

    override fun attach(window: Window, eveWindowId: EveWindowId, stacking: OverlayStacking?) = attachWindowsOverlay(window, eveWindowId) && enableWindowsLayeredOverlay(window)

    override fun getSceneSize(window: Window, component: JComponent) = getWindowsOverlaySize(window)

    override fun getInitialDensity(window: Window) = window.graphicsConfiguration.defaultTransform.scaleX.toFloat()

    override fun getSceneDensity(window: Window, physicalWidth: Int): Float = if (physicalWidth > 0 && window.width > 0) {
        physicalWidth.toFloat() / window.width
    } else {
        getInitialDensity(window)
    }

    override fun createFrameOutput(
        component: JComponent,
        window: Window,
        eveWindowId: EveWindowId,
        width: Int,
        height: Int,
        inputMask: OverlayInputMask?,
    ): OverlayFrameOutput = WindowsFrameOutput(window, eveWindowId, width, height)

    override fun isOutputValid(output: OverlayFrameOutput?) = (output as? WindowsFrameOutput)?.isOwnerValid() != false

    override fun getPointerPosition(event: MouseEvent, component: JComponent, sceneWidth: Int, sceneHeight: Int) = Offset(
        event.x * sceneWidth.toFloat() / component.width.coerceAtLeast(1),
        event.y * sceneHeight.toFloat() / component.height.coerceAtLeast(1),
    )
}

internal fun attachWindowsOverlay(window: Window, eveWindowId: EveWindowId): Boolean {
    val eveWindow = eveWindowId.getNativeId("windows:") ?: return false
    val user32 = User32.INSTANCE
    val owner = HWND(Pointer.createConstant(eveWindow))
    if (!user32.IsWindow(owner)) return false

    val overlay = HWND(Native.getWindowPointer(window))
    if (!user32.IsWindow(overlay)) return false
    user32.SetWindowLongPtr(overlay, GWLP_HWNDPARENT, owner.pointer)
    val attachedOwner = user32.GetWindow(overlay, DWORD(GW_OWNER.toLong()))
    val attached = user32.IsWindow(owner) && user32.IsWindow(overlay) && attachedOwner == owner
    if (!attached) {
        logger.warn { "Could not attach overlay to EVE Windows window ${eveWindowId.value}" }
    }
    return attached
}

private const val GWLP_HWNDPARENT = -8
private const val GW_OWNER = 4

internal class WindowsFrameOutput(window: Window, eveWindowId: EveWindowId, width: Int, height: Int) : OverlayFrameOutput {
    private val layeredSurface = WindowsLayeredSurface(window, eveWindowId, width, height)
    override val surface: Surface get() = layeredSurface.surface
    fun isOwnerValid(): Boolean = layeredSurface.isOwnerValid()
    override fun present() = layeredSurface.present()
    override fun close() = layeredSurface.close()
}

private class WindowsLayeredSurface(
    window: Window,
    eveWindowId: EveWindowId,
    private val width: Int,
    private val height: Int,
) : AutoCloseable {
    private val user32 = User32.INSTANCE
    private val gdi32 = GDI32.INSTANCE
    private val hwnd = HWND(Native.getWindowPointer(window))
    private val owner = eveWindowId.getNativeId("windows:")?.let { HWND(Pointer.createConstant(it)) }
    private val dc: HDC = checkNotNull(gdi32.CreateCompatibleDC(null)) { "Could not create overlay memory DC" }
    private val bitmap: HBITMAP
    private val previousBitmap: HANDLE
    private val sourcePoint = POINT(0, 0)
    private val size = SIZE(width, height)
    private val blend = BLENDFUNCTION().apply {
        BlendOp = 0 // AC_SRC_OVER
        BlendFlags = 0
        SourceConstantAlpha = 0xff.toByte()
        AlphaFormat = 1 // AC_SRC_ALPHA
    }
    val surface: Surface

    init {
        val info = BITMAPINFO(1).apply {
            bmiHeader.biWidth = width
            bmiHeader.biHeight = -height // Top-down BGRA, matching Skia's native N32 pixels.
            bmiHeader.biPlanes = 1
            bmiHeader.biBitCount = 32
            bmiHeader.biCompression = 0 // BI_RGB
            bmiHeader.biSizeImage = width * height * 4
        }
        val bits = PointerByReference()
        val createdBitmap = gdi32.CreateDIBSection(null, info, 0, bits, null, 0)
        if (createdBitmap == null || bits.value == null) {
            gdi32.DeleteDC(dc)
            error("Could not create overlay DIB (Windows error ${Native.getLastError()})")
        }
        bitmap = createdBitmap
        previousBitmap = gdi32.SelectObject(dc, bitmap) ?: run {
            gdi32.DeleteObject(bitmap)
            gdi32.DeleteDC(dc)
            error("Could not select overlay DIB (Windows error ${Native.getLastError()})")
        }
        surface = try {
            Surface.makeRasterDirect(
                ImageInfo.makeS32(width, height, ColorAlphaType.PREMUL),
                Pointer.nativeValue(bits.value),
                width * 4,
            )
        } catch (error: Throwable) {
            gdi32.SelectObject(dc, previousBitmap)
            gdi32.DeleteObject(bitmap)
            gdi32.DeleteDC(dc)
            throw error
        }
    }

    fun isOwnerValid(): Boolean {
        return owner != null && user32.IsWindow(owner) && user32.IsWindow(hwnd) &&
            user32.GetWindow(hwnd, DWORD(GW_OWNER.toLong())) == owner
    }

    fun present(): Boolean {
        if (!isOwnerValid()) return false
        if (!user32.UpdateLayeredWindow(
                hwnd, null, null, size,
                dc, sourcePoint, 0, blend, ULW_ALPHA,
            )
        ) {
            logger.warn { "Could not update overlay (Windows error ${Native.getLastError()})" }
            return false
        }
        return true
    }

    override fun close() {
        surface.close()
        gdi32.SelectObject(dc, previousBitmap)
        gdi32.DeleteObject(bitmap)
        gdi32.DeleteDC(dc)
    }
}

internal fun enableWindowsLayeredOverlay(window: Window): Boolean {
    val user32 = User32.INSTANCE
    val hwnd = HWND(Native.getWindowPointer(window))
    if (!user32.IsWindow(hwnd)) return false
    val style = user32.GetWindowLong(hwnd, GWL_EXSTYLE)
    user32.SetWindowLong(hwnd, GWL_EXSTYLE, (style or WS_EX_LAYERED) and WS_EX_TRANSPARENT.inv())
    val updatedStyle = user32.GetWindowLong(hwnd, GWL_EXSTYLE)
    val enabled = user32.IsWindow(hwnd) && updatedStyle and WS_EX_LAYERED != 0 && updatedStyle and WS_EX_TRANSPARENT == 0
    if (!enabled) {
        logger.warn { "Could not enable layered overlay (Windows error ${Native.getLastError()})" }
    }
    return enabled
}

private const val GWL_EXSTYLE = -20
private const val WS_EX_LAYERED = 0x00080000
private const val WS_EX_TRANSPARENT = 0x00000020
private const val ULW_ALPHA = 2

internal fun getWindowsOverlaySize(window: Window): Pair<Int, Int>? {
    val hwnd = HWND(Native.getWindowPointer(window))
    if (!User32.INSTANCE.IsWindow(hwnd)) return null
    val bounds = RECT()
    val result = WindowsDwmApi.Instance.DwmGetWindowAttribute(
        hwnd,
        WindowsDwmApi.ExtendedFrameBounds,
        bounds,
        bounds.size(),
    )
    val physicalWidth = bounds.right - bounds.left
    val physicalHeight = bounds.bottom - bounds.top
    if (result.toInt() == 0 && physicalWidth > 0 && physicalHeight > 0) {
        return physicalWidth to physicalHeight
    }
    val transform = window.graphicsConfiguration.defaultTransform
    return (window.width * transform.scaleX).roundToInt() to (window.height * transform.scaleY).roundToInt()
}
