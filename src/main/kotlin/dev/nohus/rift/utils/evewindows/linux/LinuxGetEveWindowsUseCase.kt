package dev.nohus.rift.utils.evewindows.linux

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import com.sun.jna.platform.unix.X11
import com.sun.jna.platform.unix.X11.Atom
import com.sun.jna.platform.unix.X11.AtomByReference
import com.sun.jna.platform.unix.X11.Display
import com.sun.jna.platform.unix.X11.Window
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.NativeLongByReference
import com.sun.jna.ptr.PointerByReference
import dev.nohus.rift.utils.CommandRunner
import dev.nohus.rift.utils.IsLinuxBinaryFoundUseCase
import dev.nohus.rift.utils.evewindows.BoundsAccuracy
import dev.nohus.rift.utils.evewindows.EveWindow
import dev.nohus.rift.utils.evewindows.EveWindowGeometry
import dev.nohus.rift.utils.evewindows.EveWindowId
import dev.nohus.rift.utils.evewindows.EveWindowState
import dev.nohus.rift.utils.evewindows.EveWindowTitleParser
import dev.nohus.rift.utils.evewindows.GetEveWindowsResult
import dev.nohus.rift.utils.evewindows.GetEveWindowsUseCase
import dev.nohus.rift.utils.evewindows.NativeCoordinateUnit
import dev.nohus.rift.utils.evewindows.ScreenBoundsConverter
import dev.nohus.rift.utils.evewindows.ScreenRectangle
import dev.nohus.rift.utils.evewindows.WindowBoundsMeasurement

class LinuxGetEveWindowsUseCase(
    private val commandRunner: CommandRunner,
    private val isLinuxBinaryFound: IsLinuxBinaryFoundUseCase,
    private val boundsConverter: ScreenBoundsConverter,
    private val eveWindowTitleParser: EveWindowTitleParser,
) : GetEveWindowsUseCase {

    override fun invoke(): GetEveWindowsResult {
        val directResult = try {
            getUsingX11()
        } catch (_: Exception) {
            null
        } catch (_: LinkageError) {
            null
        }
        if (directResult != null) return directResult

        if (isLinuxBinaryFound("wmctrl")) {
            return try {
                getUsingWmCtrl()
            } catch (e: Exception) {
                GetEveWindowsResult.Failure(e.message ?: e.javaClass.simpleName)
            }
        }
        return if (System.getenv("WAYLAND_DISPLAY") != null && System.getenv("DISPLAY") == null) {
            GetEveWindowsResult.Unsupported("Native Wayland does not expose global window geometry")
        } else {
            GetEveWindowsResult.Failure("Could not connect to X11 and wmctrl is not available")
        }
    }

    private fun getUsingX11(): GetEveWindowsResult? {
        val x11 = X11.INSTANCE
        val display = x11.XOpenDisplay(null) ?: return null
        return try {
            val root = x11.XDefaultRootWindow(display)
            val clientListAtom = x11.internExistingAtom(display, "_NET_CLIENT_LIST")
            val activeWindowAtom = x11.internExistingAtom(display, "_NET_ACTIVE_WINDOW")
            val hiddenStateAtom = x11.internExistingAtom(display, "_NET_WM_STATE_HIDDEN")
            val windows = clientListAtom
                ?.let { getLongProperty(x11, display, root, it) }
                ?.takeIf { it.isNotEmpty() }
                ?.map(::Window)
                ?: getRootWindowTree(x11, display, root)
            val activeWindow = activeWindowAtom
                ?.let { getLongProperty(x11, display, root, it).orEmpty().firstOrNull() }
            val eveWindows = windows.mapNotNull { window ->
                try {
                    getEveWindow(x11, display, root, window, activeWindow, hiddenStateAtom)
                } catch (_: Exception) {
                    null
                }
            }
            GetEveWindowsResult.Success(eveWindows)
        } finally {
            x11.XCloseDisplay(display)
        }
    }

    private fun getRootWindowTree(x11: X11, display: Display, root: Window): List<Window> {
        val windows = ArrayList<Window>()
        val visited = HashSet<Long>()
        fun visit(parent: Window) {
            val rootReturn = X11.WindowByReference()
            val parentReturn = X11.WindowByReference()
            val childrenReturn = PointerByReference()
            val childCountReturn = IntByReference()
            if (x11.XQueryTree(
                    display,
                    parent,
                    rootReturn,
                    parentReturn,
                    childrenReturn,
                    childCountReturn,
                ) == 0
            ) return

            val children = childrenReturn.value ?: return
            try {
                repeat(childCountReturn.value) { index ->
                    val child = Window(children.getNativeLong(index.toLong() * NativeLong.SIZE).toLong())
                    if (visited.add(child.toLong())) {
                        windows += child
                        visit(child)
                    }
                }
            } finally {
                x11.XFree(children)
            }
        }
        visit(root)
        return windows
    }

    private fun getEveWindow(
        x11: X11,
        display: Display,
        root: Window,
        window: Window,
        activeWindow: Long?,
        hiddenStateAtom: Atom?,
    ): EveWindow? {
        val title = getWindowTitle(x11, display, window)
        val characterName = eveWindowTitleParser.getCharacterName(title) ?: return null
        val attributes = X11.XWindowAttributes()
        if (x11.XGetWindowAttributes(display, window, attributes) == 0) return null

        val translatedX = IntByReference()
        val translatedY = IntByReference()
        val child = X11.WindowByReference()
        if (!x11.XTranslateCoordinates(display, window, root, 0, 0, translatedX, translatedY, child)) return null
        val clientBounds = ScreenRectangle(
            x = translatedX.value,
            y = translatedY.value,
            width = attributes.width,
            height = attributes.height
        )
        val frameExtentsAtom = x11.internExistingAtom(display, "_NET_FRAME_EXTENTS")
        val frameExtents = frameExtentsAtom?.let { getLongProperty(x11, display, window, it) }.orEmpty()
        val frameBounds = if (frameExtents.size >= 4) {
            val left = frameExtents[0].toInt()
            val right = frameExtents[1].toInt()
            val top = frameExtents[2].toInt()
            val bottom = frameExtents[3].toInt()
            ScreenRectangle(
                x = clientBounds.x - left,
                y = clientBounds.y - top,
                width = clientBounds.width + left + right,
                height = clientBounds.height + top + bottom,
            )
        } else {
            clientBounds
        }
        val convertedClient = boundsConverter.convert(clientBounds, NativeCoordinateUnit.PhysicalPixels)
        val convertedFrame = boundsConverter.convert(frameBounds, NativeCoordinateUnit.PhysicalPixels)
        val stateAtom = x11.internExistingAtom(display, "_NET_WM_STATE")
        val states = stateAtom?.let { getLongProperty(x11, display, window, it) }.orEmpty()
        val isHidden = hiddenStateAtom != null && states.contains(hiddenStateAtom.toLong())

        return EveWindow(
            id = EveWindowId("x11:${window.toLong().toULong().toString(16)}"),
            characterName = characterName,
            title = title,
            geometry = EveWindowGeometry(
                frame = WindowBoundsMeasurement(
                    nativeBounds = frameBounds,
                    overlayBounds = convertedFrame.bounds,
                    accuracy = if (frameExtents.size >= 4) BoundsAccuracy.Exact else BoundsAccuracy.Estimated,
                ),
                client = WindowBoundsMeasurement(
                    nativeBounds = clientBounds,
                    overlayBounds = convertedClient.bounds,
                    accuracy = BoundsAccuracy.Exact,
                ),
                displayScale = convertedClient.scale,
            ),
            state = when {
                isHidden -> EveWindowState.Minimized
                attributes.map_state != X11.IsViewable -> EveWindowState.Hidden
                else -> EveWindowState.Normal
            },
            isActive = window.toLong() == activeWindow,
        )
    }

    private fun getWindowTitle(x11: X11, display: Display, window: Window): String {
        val nameAtom = x11.internExistingAtom(display, "_NET_WM_NAME")
        if (nameAtom != null) {
            val utf8Atom = x11.internExistingAtom(display, "UTF8_STRING")
            val title = getByteProperty(x11, display, window, nameAtom, utf8Atom)
            if (title != null) return title
        }
        val name = PointerByReference()
        if (x11.XFetchName(display, window, name) == 0 || name.value == null) return ""
        return try {
            name.value.getString(0)
        } finally {
            x11.XFree(name.value)
        }
    }

    private fun getLongProperty(
        x11: X11,
        display: Display,
        window: Window,
        property: Atom,
    ): List<Long>? {
        val value = getProperty(x11, display, window, property, null) ?: return null
        return try {
            if (value.format != 32) return null
            List(value.itemCount) { index ->
                value.pointer.getNativeLong(index.toLong() * NativeLong.SIZE).toLong()
            }
        } finally {
            x11.XFree(value.pointer)
        }
    }

    private fun getByteProperty(
        x11: X11,
        display: Display,
        window: Window,
        property: Atom,
        requestedType: Atom?,
    ): String? {
        val value = getProperty(x11, display, window, property, requestedType) ?: return null
        return try {
            if (value.format != 8) return null
            value.pointer.getByteArray(0, value.itemCount).toString(Charsets.UTF_8).trimEnd('\u0000')
        } finally {
            x11.XFree(value.pointer)
        }
    }

    private fun getProperty(
        x11: X11,
        display: Display,
        window: Window,
        property: Atom,
        requestedType: Atom?,
    ): PropertyValue? {
        val actualType = AtomByReference()
        val actualFormat = IntByReference()
        val itemCount = NativeLongByReference()
        val bytesAfter = NativeLongByReference()
        val data = PointerByReference()
        val result = x11.XGetWindowProperty(
            display,
            window,
            property,
            NativeLong(0),
            NativeLong(4096),
            false,
            requestedType ?: Atom(X11.AnyPropertyType.toLong()),
            actualType,
            actualFormat,
            itemCount,
            bytesAfter,
            data,
        )
        val pointer = data.value
        if (pointer == null || pointer == Pointer.NULL) return null
        if (result != X11.Success) {
            x11.XFree(pointer)
            return null
        }
        return PropertyValue(pointer, actualFormat.value, itemCount.value.toInt())
    }

    private fun getUsingWmCtrl(): GetEveWindowsResult {
        val activeId = commandRunner.run("xprop", "-root", "_NET_ACTIVE_WINDOW", ignoreFailure = true)
            .output
            .substringAfterLast(' ', missingDelimiterValue = "")
            .trim()
            .lowercase()
        val output = commandRunner.run("wmctrl", "-lpGx").output
        val windows = output.lineSequence().mapNotNull { line ->
            val columns = line.trim().split(Whitespace, limit = 10)
            if (columns.size < 10) return@mapNotNull null
            val title = columns[9]
            val characterName = eveWindowTitleParser.getCharacterName(title) ?: return@mapNotNull null
            val bounds = ScreenRectangle(
                x = columns[3].toIntOrNull() ?: return@mapNotNull null,
                y = columns[4].toIntOrNull() ?: return@mapNotNull null,
                width = columns[5].toIntOrNull() ?: return@mapNotNull null,
                height = columns[6].toIntOrNull() ?: return@mapNotNull null,
            )
            val converted = boundsConverter.convert(bounds, NativeCoordinateUnit.PhysicalPixels)
            EveWindow(
                id = EveWindowId("x11:${columns[0].removePrefix("0x").lowercase()}"),
                characterName = characterName,
                title = title,
                geometry = EveWindowGeometry(
                    frame = WindowBoundsMeasurement(bounds, converted.bounds, BoundsAccuracy.Estimated),
                    client = WindowBoundsMeasurement(null, null, BoundsAccuracy.Unavailable),
                    displayScale = converted.scale,
                ),
                state = EveWindowState.Normal,
                isActive = columns[0].lowercase() == activeId,
            )
        }.toList()
        return GetEveWindowsResult.Success(windows)
    }

    private data class PropertyValue(
        val pointer: Pointer,
        val format: Int,
        val itemCount: Int,
    )

    private fun X11.internExistingAtom(display: Display, name: String): Atom? {
        // JNA wraps X11's None (atom 0) in a non-null Atom instance.
        // X11 Bool must be 1 here; JNA's boolean true is passed as -1 (0xff in the X11 request).
        return X11Atoms.Instance.XInternAtom(display, name, 1)?.takeIf { it.toLong() != 0L }
    }

    companion object {
        private val Whitespace = """\s+""".toRegex()
    }
}

private interface X11Atoms : Library {
    fun XInternAtom(display: Display, name: String, onlyIfExists: Int): Atom?

    companion object {
        val Instance: X11Atoms = Native.load("X11", X11Atoms::class.java)
    }
}
