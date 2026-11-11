package dev.nohus.rift.utils.evewindows.windows

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.POINT
import com.sun.jna.platform.win32.WinDef.RECT
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

class WindowsGetEveWindowsUseCase(
    private val boundsConverter: ScreenBoundsConverter,
    private val eveWindowTitleParser: EveWindowTitleParser,
) : GetEveWindowsUseCase {

    override fun invoke(): GetEveWindowsResult {
        return try {
            getWindows()
        } catch (e: Exception) {
            GetEveWindowsResult.Failure(e.message ?: e.javaClass.simpleName)
        } catch (e: LinkageError) {
            GetEveWindowsResult.Failure(e.message ?: e.javaClass.simpleName)
        }
    }

    private fun getWindows(): GetEveWindowsResult {
        val foregroundWindow = User32.INSTANCE.GetForegroundWindow()
        val windows = mutableListOf<EveWindow>()
        val didEnumerate = User32.INSTANCE.EnumWindows(
            { window, _ ->
                try {
                    getEveWindow(window, foregroundWindow)?.let(windows::add)
                } catch (_: Exception) {
                }
                true
            },
            null,
        )
        return if (didEnumerate) {
            GetEveWindowsResult.Success(windows)
        } else {
            GetEveWindowsResult.Failure("EnumWindows failed with error ${Native.getLastError()}")
        }
    }

    private fun getEveWindow(window: HWND, foregroundWindow: HWND?): EveWindow? {
        if (!User32.INSTANCE.IsWindowVisible(window)) return null
        val title = getWindowTitle(window)
        val characterName = eveWindowTitleParser.getCharacterName(title) ?: return null

        val frame = getFrameBounds(window) ?: return null
        val client = getClientBounds(window)
        val convertedFrame = boundsConverter.convert(frame.bounds, NativeCoordinateUnit.PhysicalPixels)
        val convertedClient = client?.let {
            boundsConverter.convert(it, NativeCoordinateUnit.PhysicalPixels)
        }
        val clientMeasurement = if (client != null && convertedClient != null) {
            WindowBoundsMeasurement(client, convertedClient.bounds, BoundsAccuracy.Exact)
        } else {
            WindowBoundsMeasurement(null, null, BoundsAccuracy.Unavailable)
        }
        return EveWindow(
            id = EveWindowId("windows:${Pointer.nativeValue(window.pointer).toULong().toString(16)}"),
            characterName = characterName,
            title = title,
            geometry = EveWindowGeometry(
                frame = WindowBoundsMeasurement(frame.bounds, convertedFrame.bounds, frame.accuracy),
                client = clientMeasurement,
                displayScale = convertedClient?.scale ?: convertedFrame.scale,
            ),
            state = if (WindowsUser32.Instance.IsIconic(window)) EveWindowState.Minimized else EveWindowState.Normal,
            isActive = window == foregroundWindow,
        )
    }

    private fun getWindowTitle(window: HWND): String {
        val length = User32.INSTANCE.GetWindowTextLength(window)
        if (length <= 0) return ""
        val buffer = CharArray(length + 1)
        val copied = User32.INSTANCE.GetWindowText(window, buffer, buffer.size)
        return if (copied > 0) Native.toString(buffer) else ""
    }

    private fun getFrameBounds(window: HWND): NativeBounds? {
        val rectangle = RECT()
        val result = WindowsDwmApi.Instance.DwmGetWindowAttribute(
            window,
            WindowsDwmApi.ExtendedFrameBounds,
            rectangle,
            rectangle.size(),
        )
        if (result.toInt() == 0) {
            return NativeBounds(rectangle.toScreenRectangle(), BoundsAccuracy.Exact)
        }
        if (User32.INSTANCE.GetWindowRect(window, rectangle)) {
            return NativeBounds(rectangle.toScreenRectangle(), BoundsAccuracy.Estimated)
        }
        return null
    }

    private fun getClientBounds(window: HWND): ScreenRectangle? {
        val rectangle = RECT()
        if (!User32.INSTANCE.GetClientRect(window, rectangle)) return null
        val topLeft = POINT(rectangle.left, rectangle.top)
        val bottomRight = POINT(rectangle.right, rectangle.bottom)
        if (!WindowsUser32.Instance.ClientToScreen(window, topLeft)) return null
        if (!WindowsUser32.Instance.ClientToScreen(window, bottomRight)) return null
        return ScreenRectangle(
            x = topLeft.x,
            y = topLeft.y,
            width = bottomRight.x - topLeft.x,
            height = bottomRight.y - topLeft.y,
        )
    }

    private fun RECT.toScreenRectangle(): ScreenRectangle {
        return ScreenRectangle(left, top, right - left, bottom - top)
    }

    private data class NativeBounds(
        val bounds: ScreenRectangle,
        val accuracy: BoundsAccuracy,
    )
}
