package dev.nohus.rift.overlay.window

import com.sun.jna.Library
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import com.sun.jna.platform.unix.X11
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import dev.nohus.rift.utils.evewindows.EveWindowId
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.Color
import java.awt.Window as AwtWindowHost
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.JComponent
import javax.swing.Timer

private val logger = KotlinLogging.logger {}

internal object LinuxOverlayPlatform : OverlayWindowPlatform {
    override val background = Color(0, 0, 0, 1)

    override fun getGraphicsConfiguration() = getTranslucentGraphicsConfiguration()

    override fun createStacking(eveWindowId: EveWindowId, eveProcessId: Int?, eveWindowLevel: Int?) =
        LinuxOverlayStacking(eveWindowId)

    override fun attach(window: AwtWindowHost, eveWindowId: EveWindowId, stacking: OverlayStacking?): Boolean {
        window.focusableWindowState = false
        return (stacking as LinuxOverlayStacking).attachWindow(window)
    }

    override fun createInputMask(window: AwtWindowHost): OverlayInputMask = LinuxInputShape(window)

    override fun createFrameOutput(
        component: JComponent,
        window: AwtWindowHost,
        eveWindowId: EveWindowId,
        width: Int,
        height: Int,
        inputMask: OverlayInputMask?,
    ): OverlayFrameOutput = RasterFrameOutput(component, width, height, checkNotNull(inputMask))
}

internal class LinuxOverlayStacking(private val eveWindowId: EveWindowId) : OverlayStacking {
    private var isEveActive = false
    private var focusHandler: LinuxOverlayFocusHandler? = null

    override fun update(window: AwtWindowHost, isActive: Boolean) {
        val becameActive = isActive && !isEveActive
        isEveActive = isActive
        if (becameActive && window.isVisible) {
            if (attachWindow(window)) raiseLinuxOverlay(window)
        }
    }

    override fun onOpened(window: AwtWindowHost) {
        if (attachWindow(window) && isEveActive) raiseLinuxOverlay(window)
        Timer(100) {
            if (window.isDisplayable) {
                if (attachWindow(window) && isEveActive) raiseLinuxOverlay(window)
            }
        }.apply {
            isRepeats = false
            start()
        }
    }

    fun attachWindow(window: AwtWindowHost): Boolean {
        val eveWindow = eveWindowId.getNativeId("x11:") ?: return false
        val attached = try {
            attachWindowWithFocusHandler(window, eveWindow)
        } catch (exception: Exception) {
            logger.error(exception) { "Could not attach X11 overlay with focus forwarding" }
            false
        } catch (error: LinkageError) {
            logger.error(error) { "Could not attach X11 overlay with focus forwarding" }
            false
        }
        if (!attached) logger.warn { "Could not attach overlay to EVE X11 window ${eveWindowId.value}" }
        return attached
    }

    private fun attachWindowWithFocusHandler(window: AwtWindowHost, eveWindow: Long): Boolean =
        withHandledX11Errors { display ->
            val x11 = X11.INSTANCE
            val overlay = X11.Window(Native.getWindowID(window))
            val target = X11.Window(eveWindow)
            if (x11.XGetWindowAttributes(display, target, X11.XWindowAttributes()) == 0) {
                false
            } else {
                val protocolsAtom = x11.XInternAtom(display, "WM_PROTOCOLS", false)
                val takeFocusAtom = x11.XInternAtom(display, "WM_TAKE_FOCUS", false)
                if (focusHandler == null) {
                    focusHandler = LinuxOverlayFocusHandler(overlay.toLong(), protocolsAtom.toLong(), takeFocusAtom.toLong()) { timestamp ->
                        if (window.isDisplayable && window.isVisible) requestLinuxEveActivation(eveWindowId, overlay, timestamp)
                    }
                }
                // Advertise after mapping so creating an overlay does not itself activate EVE
                val protocolsUpdated = if (window.isVisible) {
                    val protocols = getLinuxWindowProtocols(display, overlay)
                    if (protocols.any { it.toLong() == takeFocusAtom.toLong() }) {
                        true
                    } else {
                        val updatedProtocols = (protocols + takeFocusAtom).toTypedArray()
                        x11.XSetWMProtocols(display, overlay, updatedProtocols, updatedProtocols.size) != 0
                    }
                } else {
                    true
                }
                protocolsUpdated && X11Transient.Instance.XSetTransientForHint(display, overlay, target) != 0
            }
        }

    override fun onClosed() {
        focusHandler?.close()
        focusHandler = null
    }
}

private fun getLinuxWindowProtocols(display: X11.Display, window: X11.Window): List<X11.Atom> {
    val x11 = X11.INSTANCE
    val protocols = PointerByReference()
    val count = IntByReference()
    val result = x11.XGetWMProtocols(display, window, protocols, count)
    val pointer = protocols.value ?: return emptyList()
    return try {
        if (result == 0) emptyList() else List(count.value) { index ->
            X11.Atom(pointer.getNativeLong(index.toLong() * NativeLong.SIZE).toLong())
        }
    } finally {
        x11.XFree(pointer)
    }
}

private fun requestLinuxEveActivation(eveWindowId: EveWindowId, overlay: X11.Window, timestamp: Long) {
    val eveWindow = eveWindowId.getNativeId("x11:") ?: return
    val activated = withHandledX11Errors { display ->
        val x11 = X11.INSTANCE
        val target = X11.Window(eveWindow)
        if (x11.XGetWindowAttributes(display, target, X11.XWindowAttributes()) == 0) return@withHandledX11Errors false
        val message = X11.XClientMessageEvent().apply {
            type = X11.ClientMessage
            this.display = display
            window = target
            message_type = x11.XInternAtom(display, "_NET_ACTIVE_WINDOW", false)
            format = 32
            data = X11.XClientMessageEvent.Data().apply {
                setType(Array<NativeLong>::class.java)
                l = arrayOf(NativeLong(1), NativeLong(timestamp), NativeLong(overlay.toLong()), NativeLong(0), NativeLong(0))
            }
        }
        val event = X11.XEvent().apply {
            setType(X11.XClientMessageEvent::class.java)
            xclient = message
        }
        x11.XSendEvent(
            display, x11.XDefaultRootWindow(display), 0,
            NativeLong((X11.SubstructureRedirectMask or X11.SubstructureNotifyMask).toLong()), event,
        ) != 0
    }
    if (activated) {
        logger.debug { "Forwarded X11 overlay activation to EVE ${eveWindowId.value}" }
    } else {
        logger.warn { "Could not forward X11 overlay activation to EVE ${eveWindowId.value}" }
    }
}

private fun raiseLinuxOverlay(window: AwtWindowHost) {
    val raised = withHandledX11Errors { display ->
        X11Transient.Instance.XRaiseWindow(display, X11.Window(Native.getWindowID(window)))
        true
    }
    if (!raised) logger.warn { "Could not raise EVE X11 overlay" }
}

private val x11ErrorHandlerLock = Any()

private fun withHandledX11Errors(request: (X11.Display) -> Boolean): Boolean = synchronized(x11ErrorHandlerLock) {
    val x11 = X11.INSTANCE
    val display = x11.XOpenDisplay(null) ?: return@synchronized false
    val errorCode = AtomicInteger(0)
    var previousHandler: X11.XErrorHandler? = null
    val handler = X11.XErrorHandler { errorDisplay, error ->
        if (errorDisplay.pointer == display.pointer) {
            errorCode.compareAndSet(0, error.error_code.toInt() and 0xff)
            0
        } else {
            previousHandler?.apply(errorDisplay, error) ?: 0
        }
    }
    try {
        previousHandler = x11.XSetErrorHandler(handler)
        val requested = try {
            request(display)
        } finally {
            // Xlib reports protocol errors asynchronously; drain this connection before restoring the handler.
            x11.XSync(display, false)
        }
        if (errorCode.get() != 0) {
            logger.warn { "X11 overlay request failed with protocol error ${errorCode.get()}" }
        }
        requested && errorCode.get() == 0
    } finally {
        x11.XSetErrorHandler(previousHandler)
        x11.XCloseDisplay(display)
    }
}

private interface X11Transient : Library {
    fun XSetTransientForHint(display: X11.Display, window: X11.Window, target: X11.Window): Int
    fun XRaiseWindow(display: X11.Display, window: X11.Window): Int

    companion object {
        val Instance: X11Transient = Native.load("X11", X11Transient::class.java)
    }
}

internal class LinuxInputShape(
    private val window: AwtWindowHost,
) : OverlayInputMask {
    private val x11 = X11.INSTANCE
    private val display = checkNotNull(x11.XOpenDisplay(null)) { "Could not connect to X11" }
    private var hasClearedAwtShape = false

    override fun update(rectangles: List<InputRectangle>, width: Int, height: Int) {
        if (!hasClearedAwtShape) {
            window.shape = null
            hasClearedAwtShape = true
        }
        val nativeRectangles = rectangles.createNativeRectangles()
        XShape.Instance.XShapeCombineRectangles(
            display = display,
            window = X11.Window(Native.getWindowID(window)),
            shapeKind = SHAPE_INPUT,
            xOffset = 0,
            yOffset = 0,
            rectangles = nativeRectangles,
            rectangleCount = rectangles.size,
            operation = SHAPE_SET,
            ordering = RECTANGLES_UNSORTED,
        )
        x11.XFlush(display)
    }

    override fun close() {
        x11.XCloseDisplay(display)
    }

    private fun List<InputRectangle>.createNativeRectangles(): Pointer? {
        if (isEmpty()) return null

        return Memory(size * X_RECTANGLE_SIZE_BYTES.toLong()).also { memory ->
            forEachIndexed { index, rectangle ->
                val offset = index * X_RECTANGLE_SIZE_BYTES.toLong()
                memory.setShort(offset, rectangle.x.toShort())
                memory.setShort(offset + 2, rectangle.y.toShort())
                memory.setShort(offset + 4, rectangle.width.toShort())
                memory.setShort(offset + 6, rectangle.height.toShort())
            }
        }
    }

    private companion object {
        const val SHAPE_INPUT = 2
        const val SHAPE_SET = 0
        const val RECTANGLES_UNSORTED = 0
        const val X_RECTANGLE_SIZE_BYTES = 8
    }
}

private interface XShape : Library {
    fun XShapeCombineRectangles(
        display: X11.Display,
        window: X11.Window,
        shapeKind: Int,
        xOffset: Int,
        yOffset: Int,
        rectangles: Pointer?,
        rectangleCount: Int,
        operation: Int,
        ordering: Int,
    )

    companion object {
        val Instance: XShape = Native.load("Xext", XShape::class.java)
    }
}
