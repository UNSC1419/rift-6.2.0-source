package dev.nohus.rift.overlay.window

import com.sun.jna.platform.unix.X11
import io.github.oshai.kotlinlogging.KotlinLogging
import java.lang.reflect.Proxy
import java.util.concurrent.atomic.AtomicReference
import javax.swing.SwingUtilities

private val logger = KotlinLogging.logger {}

// AWT owns the X11 connection receiving ClientMessage events; a separate JNA connection cannot receive them
internal class LinuxOverlayFocusHandler(
    private val windowId: Long,
    private val protocolsAtom: Long,
    private val takeFocusAtom: Long,
    onTakeFocus: (Long) -> Unit,
) : AutoCloseable {

    private val focusCallback = AtomicReference<((Long) -> Unit)?>(onTakeFocus)
    private val toolkitClass = Class.forName("sun.awt.X11.XToolkit")
    private val dispatcherClass = Class.forName("sun.awt.X11.XEventDispatcher")
    private val eventClass = Class.forName("sun.awt.X11.XEvent")
    private val clientMessageClass = Class.forName("sun.awt.X11.XClientMessageEvent")
    private val getEventType = eventClass.getMethod("get_type")
    private val getClientMessage = eventClass.getMethod("get_xclient")
    private val getMessageType = clientMessageClass.getMethod("get_message_type")
    private val getMessageWindow = clientMessageClass.getMethod("get_window")
    private val getMessageFormat = clientMessageClass.getMethod("get_format")
    private val getMessageData = clientMessageClass.getMethod("get_data", Int::class.javaPrimitiveType)
    private val removeDispatcher = toolkitClass.getDeclaredMethod(
        "removeEventDispatcher",
        Long::class.javaPrimitiveType,
        dispatcherClass,
    ).apply { isAccessible = true }

    private val dispatcher = Proxy.newProxyInstance(dispatcherClass.classLoader, arrayOf(dispatcherClass)) { proxy, method, arguments ->
        when (method.name) {
            "dispatchEvent" -> {
                try {
                    dispatchEvent(checkNotNull(arguments?.get(0)))
                } catch (exception: Exception) {
                    logger.error(exception) { "Could not handle X11 overlay focus request" }
                } catch (error: LinkageError) {
                    logger.error(error) { "Could not handle X11 overlay focus request" }
                }
                null
            }
            "equals" -> proxy === arguments?.get(0)
            "hashCode" -> System.identityHashCode(proxy)
            "toString" -> "LinuxOverlayFocusHandler($windowId)"
            else -> null
        }
    }

    init {
        toolkitClass.getDeclaredMethod(
            "addEventDispatcher",
            Long::class.javaPrimitiveType,
            dispatcherClass,
        ).apply { isAccessible = true }.invoke(null, windowId, dispatcher)
    }

    private fun dispatchEvent(event: Any) {
        if (focusCallback.get() == null || getEventType.invoke(event) != X11.ClientMessage) return
        val message = getClientMessage.invoke(event)
        if (getMessageWindow.invoke(message) != windowId) return
        if (getMessageType.invoke(message) != protocolsAtom || getMessageFormat.invoke(message) != 32) return
        if (getMessageData.invoke(message, 0) != takeFocusAtom) return
        val timestamp = (getMessageData.invoke(message, 1) as Long) and 0xffffffffL
        if (timestamp == 0L) return
        // Copy native data before AWT reuses it, and make X11 requests outside AWT's native lock
        SwingUtilities.invokeLater {
            try {
                focusCallback.get()?.invoke(timestamp)
            } catch (exception: Exception) {
                logger.error(exception) { "Could not forward X11 overlay focus request" }
            } catch (error: LinkageError) {
                logger.error(error) { "Could not forward X11 overlay focus request" }
            }
        }
    }

    override fun close() {
        // Cancel queued requests and release the window reference even if native dispatcher removal fails
        if (focusCallback.getAndSet(null) == null) return
        try {
            removeDispatcher.invoke(null, windowId, dispatcher)
        } catch (exception: Exception) {
            logger.error(exception) { "Could not remove X11 overlay focus dispatcher" }
        } catch (error: LinkageError) {
            logger.error(error) { "Could not remove X11 overlay focus dispatcher" }
        }
    }
}
