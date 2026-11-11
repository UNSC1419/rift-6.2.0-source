package dev.nohus.rift.overlay.window

import com.sun.jna.NativeLibrary
import com.sun.jna.Pointer
import dev.nohus.rift.utils.evewindows.EveWindowId
import java.awt.Color
import java.awt.Component
import java.awt.MouseInfo
import java.awt.Window as AwtWindowHost
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy
import java.util.BitSet
import javax.swing.JComponent
import javax.swing.Timer

internal object MacOverlayPlatform : OverlayWindowPlatform {
    override val background = Color(0, 0, 0, 0)

    override fun getGraphicsConfiguration() = getTranslucentGraphicsConfiguration()

    override fun createStacking(eveWindowId: EveWindowId, eveProcessId: Int?, eveWindowLevel: Int?) =
        MacOverlayStacking(eveWindowId, eveProcessId, eveWindowLevel)

    override fun updateStacking(stacking: OverlayStacking?, eveWindowLevel: Int?) {
        (stacking as? MacOverlayStacking)?.eveWindowLevel = eveWindowLevel
    }

    override fun attach(window: AwtWindowHost, eveWindowId: EveWindowId, stacking: OverlayStacking?): Boolean {
        stacking?.onPeerReady(window)
        return true
    }

    override fun createInputMask(window: AwtWindowHost): OverlayInputMask = MacInputMask(window)

    override fun createFrameOutput(
        component: JComponent,
        window: AwtWindowHost,
        eveWindowId: EveWindowId,
        width: Int,
        height: Int,
        inputMask: OverlayInputMask?,
    ): OverlayFrameOutput = RasterFrameOutput(component, width, height, checkNotNull(inputMask))
}

internal class MacOverlayStacking(
    eveWindowId: EveWindowId,
    private val eveProcessId: Int?,
    var eveWindowLevel: Int?,
) : OverlayStacking {
    private val eveWindowNumber = eveWindowId.value
        .takeIf { it.startsWith("macos:") }
        ?.removePrefix("macos:")
        ?.toIntOrNull()
    private var nativeWindow: MacNativeWindow? = null
    private var isFloating: Boolean? = null
    private var timer: Timer? = null

    override fun update(window: AwtWindowHost, isActive: Boolean) {
        if (window.isVisible) refreshLevel()
    }

    override fun onPeerReady(window: AwtWindowHost) {
        nativeWindow = MacNativeWindow(window)
        refreshLevel()
    }

    override fun onOpened(window: AwtWindowHost) {
        isFloating = null
        refreshLevel()
        timer?.stop()
        timer = Timer(50) {
            if (window.isDisplayable) refreshLevel() else onClosed()
        }.also { it.start() }
    }

    override fun onClosed() {
        timer?.stop()
        timer = null
        nativeWindow = null
        isFloating = null
    }

    private fun refreshLevel() {
        val nativeWindow = nativeWindow ?: return
        val [frontmostProcessId, currentLevel] = nativeWindow.stackingState()
        if (frontmostProcessId == null) return
        val shouldFloat = frontmostProcessId == eveProcessId
        val targetLevel = if (shouldFloat) {
            maxOf(MAC_FLOATING_WINDOW_LEVEL, (eveWindowLevel ?: MAC_NORMAL_WINDOW_LEVEL) + 1)
        } else {
            MAC_NORMAL_WINDOW_LEVEL
        }
        if (shouldFloat == isFloating && currentLevel == targetLevel) return
        isFloating = shouldFloat
        nativeWindow.setLevel(targetLevel)
        if (shouldFloat) {
            nativeWindow.orderFrontRegardless()
        } else {
            eveWindowNumber?.let(nativeWindow::orderAbove)
        }
    }

    private companion object {
        const val MAC_NORMAL_WINDOW_LEVEL = 0
        const val MAC_FLOATING_WINDOW_LEVEL = 3
    }
}

internal class MacInputMask(private val window: AwtWindowHost) : OverlayInputMask {
    private val nativeWindow = MacNativeWindow(window)
    private var opaquePixels = BitSet()
    private var width = 0
    private var height = 0
    private var isMousePressed = false
    private var isIgnoringMouseEvents: Boolean? = null

    override fun update(rectangles: List<InputRectangle>, width: Int, height: Int) {
        val pixels = BitSet(width * height)
        rectangles.forEach { rectangle ->
            val start = rectangle.y * width + rectangle.x
            pixels.set(start, start + rectangle.width)
        }
        opaquePixels = pixels
        this.width = width
        this.height = height
        refreshPointer()
    }

    override fun refreshPointer() {
        if (isMousePressed && !nativeWindow.hasPressedMouseButtons()) {
            isMousePressed = false
        }
        val pointer = MouseInfo.getPointerInfo()?.location
        val x = pointer?.x?.minus(window.x) ?: -1
        val y = pointer?.y?.minus(window.y) ?: -1
        val isOverContent = window.isVisible && x in 0 until width && y in 0 until height && opaquePixels[y * width + x]
        val shouldIgnore = !isMousePressed && !isOverContent
        if (isIgnoringMouseEvents != shouldIgnore) {
            nativeWindow.setIgnoresMouseEvents(shouldIgnore)
            isIgnoringMouseEvents = shouldIgnore
        }
    }

    override fun onMousePressed() {
        isMousePressed = true
    }

    override fun onMouseReleased() {
        isMousePressed = false
        refreshPointer()
    }

    override fun close() = Unit
}

private class MacNativeWindow(window: AwtWindowHost) {
    private val platformWindow = run {
        val peerField = Component::class.java.getDeclaredField("peer").apply { isAccessible = true }
        val peer = checkNotNull(peerField.get(window)) { "macOS overlay window has no native peer" }
        val getPlatformWindow = peer.javaClass.getMethod("getPlatformWindow").apply { isAccessible = true }
        checkNotNull(getPlatformWindow.invoke(peer)) { "macOS overlay window has no platform window" }
    }
    private val nativeActionClass = Class.forName("sun.lwawt.macosx.CFRetainedResource\$CFNativeAction")
    private val executeNativeAction = platformWindow.javaClass.getMethod("execute", nativeActionClass)
        .apply { isAccessible = true }
    private val objc = NativeLibrary.getInstance("objc")
    private val sendMessage = objc.getFunction("objc_msgSend")
    private val setIgnoresMouseEvents = objc.getFunction("sel_registerName")
        .invokePointer(arrayOf("setIgnoresMouseEvents:"))
    private val orderWindow = objc.getFunction("sel_registerName")
        .invokePointer(arrayOf("orderWindow:relativeTo:"))
    private val orderFrontRegardless = objc.getFunction("sel_registerName")
        .invokePointer(arrayOf("orderFrontRegardless"))
    private val setLevel = objc.getFunction("sel_registerName")
        .invokePointer(arrayOf("setLevel:"))
    private val getLevel = objc.getFunction("sel_registerName")
        .invokePointer(arrayOf("level"))
    private val workspaceClass = checkNotNull(objc.getFunction("objc_getClass").invokePointer(arrayOf("NSWorkspace"))) {
        "macOS NSWorkspace is unavailable"
    }
    private val eventClass = checkNotNull(objc.getFunction("objc_getClass").invokePointer(arrayOf("NSEvent"))) {
        "macOS NSEvent is unavailable"
    }
    private val pressedMouseButtons = objc.getFunction("sel_registerName")
        .invokePointer(arrayOf("pressedMouseButtons"))
    private val sharedWorkspace = objc.getFunction("sel_registerName")
        .invokePointer(arrayOf("sharedWorkspace"))
    private val frontmostApplication = objc.getFunction("sel_registerName")
        .invokePointer(arrayOf("frontmostApplication"))
    private val processIdentifier = objc.getFunction("sel_registerName")
        .invokePointer(arrayOf("processIdentifier"))
    private val performOnMainThread = Class.forName("sun.lwawt.macosx.LWCToolkit")
        .getMethod("performOnMainThreadAndWait", Runnable::class.java)
        .apply { isAccessible = true }

    fun setIgnoresMouseEvents(ignores: Boolean) {
        executeWithWindow { handle ->
            sendMessage.invokeVoid(arrayOf(handle, setIgnoresMouseEvents, if (ignores) 1 else 0))
        }
    }

    fun hasPressedMouseButtons(): Boolean {
        var pressedButtons = 0L
        invokeOnMainThread {
            pressedButtons = sendMessage.invokeLong(arrayOf(eventClass, pressedMouseButtons))
        }
        return pressedButtons != 0L
    }

    fun orderAbove(windowNumber: Int) {
        executeWithWindow { handle ->
            sendMessage.invokeVoid(arrayOf(handle, orderWindow, 1L, windowNumber.toLong()))
        }
    }

    fun orderFrontRegardless() {
        executeWithWindow { handle ->
            sendMessage.invokeVoid(arrayOf(handle, orderFrontRegardless))
        }
    }

    fun setLevel(level: Int) {
        executeWithWindow { handle ->
            sendMessage.invokeVoid(arrayOf(handle, setLevel, level.toLong()))
        }
    }

    fun stackingState(): Pair<Int?, Int> {
        var processId: Int? = null
        var level = 0
        executeWithWindow { handle ->
            val workspace = sendMessage.invokePointer(arrayOf(workspaceClass, sharedWorkspace))
            val application = workspace?.let { sendMessage.invokePointer(arrayOf(it, frontmostApplication)) }
            if (application != null) processId = sendMessage.invokeInt(arrayOf(application, processIdentifier))
            level = sendMessage.invokeInt(arrayOf(handle, getLevel))
        }
        return processId to level
    }

    private fun executeWithWindow(action: (Pointer) -> Unit) {
        val nativeAction = Proxy.newProxyInstance(nativeActionClass.classLoader, arrayOf(nativeActionClass)) { _, _, arguments ->
            action(Pointer(arguments[0] as Long))
            null
        }
        invokeOnMainThread { executeNativeAction.invoke(platformWindow, nativeAction) }
    }

    private fun invokeOnMainThread(action: () -> Unit) {
        var failure: Throwable? = null
        performOnMainThread.invoke(null, Runnable {
            // Java exceptions escaping this callback become fatal Objective-C exceptions
            try {
                action()
            } catch (error: Throwable) {
                failure = if (error is InvocationTargetException) error.targetException else error
            }
        })
        failure?.let { throw it }
    }
}
