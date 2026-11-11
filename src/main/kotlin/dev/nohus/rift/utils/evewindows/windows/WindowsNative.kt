package dev.nohus.rift.utils.evewindows.windows

import com.sun.jna.Native
import com.sun.jna.Structure
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.POINT
import com.sun.jna.platform.win32.WinNT.HRESULT
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions

interface WindowsUser32 : StdCallLibrary {
    fun ClientToScreen(window: HWND, point: POINT): Boolean
    fun IsIconic(window: HWND): Boolean
    fun GetDpiForWindow(window: HWND): Int

    companion object {
        val Instance: WindowsUser32 = Native.load("user32", WindowsUser32::class.java, W32APIOptions.DEFAULT_OPTIONS)
    }
}

interface WindowsDwmApi : StdCallLibrary {
    fun DwmGetWindowAttribute(window: HWND, attribute: Int, value: Structure, valueSize: Int): HRESULT

    companion object {
        val Instance: WindowsDwmApi = Native.load("dwmapi", WindowsDwmApi::class.java, W32APIOptions.DEFAULT_OPTIONS)
        const val ExtendedFrameBounds = 9
    }
}
