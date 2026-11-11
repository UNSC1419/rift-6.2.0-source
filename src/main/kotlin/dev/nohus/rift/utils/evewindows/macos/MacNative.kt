package dev.nohus.rift.utils.evewindows.macos

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.platform.mac.CoreFoundation.CFArrayRef
import com.sun.jna.platform.mac.CoreFoundation.CFDictionaryRef
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef
import com.sun.jna.platform.mac.CoreFoundation.CFTypeRef
import com.sun.jna.ptr.PointerByReference

class AXUIElementRef : CFTypeRef {
    constructor() : super()
    constructor(pointer: Pointer) : super(pointer)
}

class AXValueRef : CFTypeRef {
    constructor() : super()
    constructor(pointer: Pointer) : super(pointer)
}

@Structure.FieldOrder("x", "y")
class CGPoint : Structure() {
    @JvmField var x: Double = 0.0
    @JvmField var y: Double = 0.0
}

@Structure.FieldOrder("width", "height")
class CGSize : Structure() {
    @JvmField var width: Double = 0.0
    @JvmField var height: Double = 0.0
}

interface MacApplicationServices : Library {
    fun AXIsProcessTrusted(): Boolean
    fun AXUIElementCreateApplication(processId: Int): AXUIElementRef?
    fun AXUIElementCopyAttributeValue(element: AXUIElementRef, attribute: CFStringRef, value: PointerByReference): Int
    fun AXValueGetValue(value: AXValueRef, type: Int, output: Pointer): Boolean

    companion object {
        val Instance: MacApplicationServices = Native.load("ApplicationServices", MacApplicationServices::class.java)
        const val Success = 0
        const val PointValueType = 1
        const val SizeValueType = 2
    }
}

interface MacCoreGraphics : Library {
    fun CGWindowListCopyWindowInfo(options: Int, relativeToWindow: Int): CFArrayRef?

    companion object {
        val Instance: MacCoreGraphics = Native.load("CoreGraphics", MacCoreGraphics::class.java)
        const val NullWindowId = 0
        const val ExcludeDesktopElements = 1 shl 4
    }
}

object MacAccessibilityAttributes {
    const val Windows = "AXWindows"
    const val Title = "AXTitle"
    const val Position = "AXPosition"
    const val Size = "AXSize"
    const val Minimized = "AXMinimized"
    const val FullScreen = "AXFullScreen"
    const val Children = "AXChildren"
    const val Contents = "AXContents"
    const val CloseButton = "AXCloseButton"
    const val MinimizeButton = "AXMinimizeButton"
    const val ZoomButton = "AXZoomButton"
    const val Frontmost = "AXFrontmost"
    const val FocusedWindow = "AXFocusedWindow"
}

object MacWindowDictionaryKeys {
    const val Number = "kCGWindowNumber"
    const val OwnerPid = "kCGWindowOwnerPID"
    const val Layer = "kCGWindowLayer"
    const val Bounds = "kCGWindowBounds"
    const val IsOnscreen = "kCGWindowIsOnscreen"
    const val X = "X"
    const val Y = "Y"
    const val Width = "Width"
    const val Height = "Height"
}
