package dev.nohus.rift.utils.evewindows.macos

import com.sun.jna.Pointer
import com.sun.jna.platform.mac.CoreFoundation
import com.sun.jna.platform.mac.CoreFoundation.CFArrayRef
import com.sun.jna.platform.mac.CoreFoundation.CFBooleanRef
import com.sun.jna.platform.mac.CoreFoundation.CFDictionaryRef
import com.sun.jna.platform.mac.CoreFoundation.CFNumberRef
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef
import com.sun.jna.platform.mac.CoreFoundation.CFTypeRef
import com.sun.jna.ptr.PointerByReference
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
import kotlin.math.abs
import kotlin.math.roundToInt

class MacGetEveWindowsUseCase(
    private val boundsConverter: ScreenBoundsConverter,
    private val eveWindowTitleParser: EveWindowTitleParser,
) : GetEveWindowsUseCase {

    private val accessibility: MacApplicationServices by lazy { MacApplicationServices.Instance }
    private val coreGraphics: MacCoreGraphics by lazy { MacCoreGraphics.Instance }
    private val coreFoundation: CoreFoundation by lazy { CoreFoundation.INSTANCE }
    private val ownProcessId = ProcessHandle.current().pid().toInt()

    override fun invoke(): GetEveWindowsResult {
        return try {
            if (!accessibility.AXIsProcessTrusted()) {
                GetEveWindowsResult.PermissionRequired
            } else {
                getWindows()
            }
        } catch (e: Exception) {
            GetEveWindowsResult.Failure(e.message ?: e.javaClass.simpleName)
        } catch (e: LinkageError) {
            GetEveWindowsResult.Unsupported(e.message ?: e.javaClass.simpleName)
        }
    }

    private fun getWindows(): GetEveWindowsResult {
        val coreGraphicsWindows = getCoreGraphicsWindows()
        val matchedWindowIds = mutableSetOf<Int>()
        val eveWindows = coreGraphicsWindows
            .groupBy { it.processId }
            .flatMap { (processId, processWindows) ->
                if (processId == ownProcessId) {
                    emptyList()
                } else {
                    getEveWindowsForProcess(processId, processWindows, matchedWindowIds)
                }
            }
        return GetEveWindowsResult.Success(eveWindows)
    }

    private fun getEveWindowsForProcess(
        processId: Int,
        coreGraphicsWindows: List<CoreGraphicsWindow>,
        matchedWindowIds: MutableSet<Int>,
    ): List<EveWindow> {
        val application = accessibility.AXUIElementCreateApplication(processId) ?: return emptyList()
        try {
            val windows = try {
                getAccessibilityWindows(application)
            } catch (_: Exception) {
                return emptyList()
            }
            val focusedWindow = try {
                if (getBooleanAttribute(application, MacAccessibilityAttributes.Frontmost) == true) {
                    copyAttribute(application, MacAccessibilityAttributes.FocusedWindow)?.let(::AXUIElementRef)
                } else {
                    null
                }
            } catch (_: Exception) {
                null
            }
            try {
                return windows.mapNotNull { window ->
                    try {
                        getEveWindow(window, processId, coreGraphicsWindows, matchedWindowIds, focusedWindow)
                    } catch (_: Exception) {
                        null
                    } finally {
                        window.release()
                    }
                }
            } finally {
                focusedWindow?.release()
            }
        } finally {
            application.release()
        }
    }

    private fun getEveWindow(
        window: AXUIElementRef,
        processId: Int,
        coreGraphicsWindows: List<CoreGraphicsWindow>,
        matchedWindowIds: MutableSet<Int>,
        focusedWindow: AXUIElementRef?,
    ): EveWindow? {
        val title = getStringAttribute(window, MacAccessibilityAttributes.Title) ?: return null
        val characterName = eveWindowTitleParser.getCharacterName(title) ?: return null
        val frameBounds = getBounds(window) ?: return null
        val coreGraphicsWindow = coreGraphicsWindows
            .asSequence()
            .filter { it.id !in matchedWindowIds }
            .minByOrNull { getBoundsDifference(frameBounds, it.bounds) }
            ?.takeIf { getBoundsDifference(frameBounds, it.bounds) <= BOUNDS_MATCH_TOLERANCE }
        coreGraphicsWindow?.let { matchedWindowIds += it.id }

        val convertedFrame = boundsConverter.convert(frameBounds, NativeCoordinateUnit.LogicalPoints)
        val clientBounds = getClientBounds(window, frameBounds)
        val convertedClient = clientBounds.bounds?.let {
            boundsConverter.convert(it, NativeCoordinateUnit.LogicalPoints)
        }
        val isFocused = focusedWindow != null && coreFoundation.CFEqual(window, focusedWindow)
        val isMinimized = getBooleanAttribute(window, MacAccessibilityAttributes.Minimized) == true
        val isOnscreen = coreGraphicsWindow?.isOnscreen != false

        return EveWindow(
            id = EveWindowId(coreGraphicsWindow?.let { "macos:${it.id}" } ?: "macos:$processId:${title.hashCode()}"),
            characterName = characterName,
            title = title,
            geometry = EveWindowGeometry(
                frame = WindowBoundsMeasurement(frameBounds, convertedFrame.bounds, BoundsAccuracy.Exact),
                client = WindowBoundsMeasurement(
                    nativeBounds = clientBounds.bounds,
                    overlayBounds = convertedClient?.bounds,
                    accuracy = clientBounds.accuracy,
                ),
                displayScale = convertedClient?.scale ?: convertedFrame.scale,
            ),
            state = when {
                isMinimized -> EveWindowState.Minimized
                !isOnscreen -> EveWindowState.Hidden
                else -> EveWindowState.Normal
            },
            isActive = isFocused,
            processId = processId,
            macWindowLevel = coreGraphicsWindow?.layer,
        )
    }

    private fun getCoreGraphicsWindows(): List<CoreGraphicsWindow> {
        val windowInfo = coreGraphics.CGWindowListCopyWindowInfo(
            MacCoreGraphics.ExcludeDesktopElements,
            MacCoreGraphics.NullWindowId,
        ) ?: return emptyList()
        return try {
            buildList {
                repeat(windowInfo.count) { index ->
                    val dictionary = CFDictionaryRef(windowInfo.getValueAtIndex(index))
                    val layer = getNumber(dictionary, MacWindowDictionaryKeys.Layer)?.toInt() ?: return@repeat
                    val id = getNumber(dictionary, MacWindowDictionaryKeys.Number)?.toInt() ?: return@repeat
                    val processId = getNumber(dictionary, MacWindowDictionaryKeys.OwnerPid)?.toInt() ?: return@repeat
                    val boundsDictionary = getDictionary(dictionary, MacWindowDictionaryKeys.Bounds) ?: return@repeat
                    val bounds = ScreenRectangle(
                        x = getNumber(boundsDictionary, MacWindowDictionaryKeys.X)?.roundToInt() ?: return@repeat,
                        y = getNumber(boundsDictionary, MacWindowDictionaryKeys.Y)?.roundToInt() ?: return@repeat,
                        width = getNumber(boundsDictionary, MacWindowDictionaryKeys.Width)?.roundToInt()
                            ?: return@repeat,
                        height = getNumber(boundsDictionary, MacWindowDictionaryKeys.Height)?.roundToInt()
                            ?: return@repeat,
                    )
                    add(
                        CoreGraphicsWindow(
                            id = id,
                            processId = processId,
                            layer = layer,
                            bounds = bounds,
                            isOnscreen = getBoolean(dictionary, MacWindowDictionaryKeys.IsOnscreen) ?: false,
                        ),
                    )
                }
            }
        } finally {
            windowInfo.release()
        }
    }

    private fun getAccessibilityWindows(application: AXUIElementRef): List<AXUIElementRef> {
        val pointer = copyAttribute(application, MacAccessibilityAttributes.Windows) ?: return emptyList()
        val array = CFArrayRef(pointer)
        try {
            return List(array.count) { index ->
                val window = AXUIElementRef(array.getValueAtIndex(index))
                window.retain()
                window
            }
        } finally {
            array.release()
        }
    }

    private fun getBounds(element: AXUIElementRef): ScreenRectangle? {
        val positionPointer = copyAttribute(element, MacAccessibilityAttributes.Position) ?: return null
        val sizePointer = copyAttribute(element, MacAccessibilityAttributes.Size) ?: run {
            CFTypeRef(positionPointer).release()
            return null
        }
        return try {
            val point = CGPoint()
            val size = CGSize()
            val hasPoint = accessibility.AXValueGetValue(
                AXValueRef(positionPointer),
                MacApplicationServices.PointValueType,
                point.pointer,
            )
            val hasSize = accessibility.AXValueGetValue(
                AXValueRef(sizePointer),
                MacApplicationServices.SizeValueType,
                size.pointer,
            )
            if (!hasPoint || !hasSize) return null
            point.read()
            size.read()
            ScreenRectangle(
                x = point.x.roundToInt(),
                y = point.y.roundToInt(),
                width = size.width.roundToInt(),
                height = size.height.roundToInt(),
            )
        } finally {
            CFTypeRef(positionPointer).release()
            CFTypeRef(sizePointer).release()
        }
    }

    private fun getClientBounds(window: AXUIElementRef, frame: ScreenRectangle): ClientBounds {
        if (getBooleanAttribute(window, MacAccessibilityAttributes.FullScreen) == true) {
            return ClientBounds(frame, BoundsAccuracy.Exact)
        }
        for (attribute in listOf(MacAccessibilityAttributes.Contents, MacAccessibilityAttributes.Children)) {
            getContentBounds(window, frame, attribute)?.let { return ClientBounds(it, BoundsAccuracy.Estimated) }
        }
        val titleBarButtons = listOf(
            MacAccessibilityAttributes.CloseButton,
            MacAccessibilityAttributes.MinimizeButton,
            MacAccessibilityAttributes.ZoomButton,
        ).mapNotNull { attribute ->
            val pointer = copyAttribute(window, attribute) ?: return@mapNotNull null
            try {
                getBounds(AXUIElementRef(pointer))
            } finally {
                CFTypeRef(pointer).release()
            }
        }
        val estimated = estimateMacContentBoundsFromTitleBar(frame, titleBarButtons)
        if (estimated != null) return ClientBounds(estimated, BoundsAccuracy.Estimated)
        // Fixed window mode has no title bar or separate accessibility content rectangle.
        // Its window frame is the game content, so it can still host the overlay.
        if (titleBarButtons.isEmpty() && frame.width > 0 && frame.height > 0) {
            return ClientBounds(frame, BoundsAccuracy.Estimated)
        }
        return ClientBounds(null, BoundsAccuracy.Unavailable)
    }

    private fun getContentBounds(window: AXUIElementRef, frame: ScreenRectangle, attribute: String): ScreenRectangle? {
        val childrenPointer = copyAttribute(window, attribute)
        if (childrenPointer != null) {
            val children = CFArrayRef(childrenPointer)
            try {
                val descendantBounds = (0 until children.count)
                    .asSequence()
                    .flatMap { index ->
                        getDescendantBounds(AXUIElementRef(children.getValueAtIndex(index)), CLIENT_SEARCH_DEPTH)
                    }
                val candidate = selectMacContentBounds(frame, descendantBounds)
                if (candidate != null) return candidate
            } finally {
                children.release()
            }
        }
        return null
    }

    private fun getDescendantBounds(element: AXUIElementRef, depth: Int): Sequence<ScreenRectangle> = sequence {
        getBounds(element)?.let { yield(it) }
        if (depth == 0) return@sequence
        val childrenPointer = copyAttribute(element, MacAccessibilityAttributes.Children) ?: return@sequence
        val children = CFArrayRef(childrenPointer)
        try {
            repeat(children.count.coerceAtMost(MAXIMUM_CHILDREN_PER_ELEMENT)) { index ->
                yieldAll(
                    getDescendantBounds(
                        AXUIElementRef(children.getValueAtIndex(index)),
                        depth - 1,
                    ),
                )
            }
        } finally {
            children.release()
        }
    }

    private fun getStringAttribute(element: AXUIElementRef, attribute: String): String? {
        val pointer = copyAttribute(element, attribute) ?: return null
        return try {
            CFStringRef(pointer).stringValue()
        } finally {
            CFTypeRef(pointer).release()
        }
    }

    private fun getBooleanAttribute(element: AXUIElementRef, attribute: String): Boolean? {
        val pointer = copyAttribute(element, attribute) ?: return null
        return try {
            CFBooleanRef(pointer).booleanValue()
        } finally {
            CFTypeRef(pointer).release()
        }
    }

    private fun copyAttribute(element: AXUIElementRef, attribute: String): Pointer? {
        val attributeName = CFStringRef.createCFString(attribute)
        return try {
            val value = PointerByReference()
            if (accessibility.AXUIElementCopyAttributeValue(element, attributeName, value) == MacApplicationServices.Success) {
                value.value
            } else {
                null
            }
        } finally {
            attributeName.release()
        }
    }

    private fun getNumber(dictionary: CFDictionaryRef, key: String): Double? {
        val pointer = getValue(dictionary, key) ?: return null
        return CFNumberRef(pointer).doubleValue()
    }

    private fun getBoolean(dictionary: CFDictionaryRef, key: String): Boolean? {
        val pointer = getValue(dictionary, key) ?: return null
        return CFBooleanRef(pointer).booleanValue()
    }

    private fun getDictionary(dictionary: CFDictionaryRef, key: String): CFDictionaryRef? {
        return getValue(dictionary, key)?.let(::CFDictionaryRef)
    }

    private fun getValue(dictionary: CFDictionaryRef, key: String): Pointer? {
        val keyRef = CFStringRef.createCFString(key)
        return try {
            dictionary.getValue(keyRef)
        } finally {
            keyRef.release()
        }
    }

    private fun getBoundsDifference(first: ScreenRectangle, second: ScreenRectangle): Int {
        return abs(first.x - second.x) + abs(first.y - second.y) +
            abs(first.width - second.width) + abs(first.height - second.height)
    }

    private fun ScreenRectangle.contains(other: ScreenRectangle): Boolean {
        return other.x >= x && other.y >= y && other.right <= right && other.bottom <= bottom
    }

    private data class CoreGraphicsWindow(
        val id: Int,
        val processId: Int,
        val layer: Int,
        val bounds: ScreenRectangle,
        val isOnscreen: Boolean,
    )

    private data class ClientBounds(
        val bounds: ScreenRectangle?,
        val accuracy: BoundsAccuracy,
    )

    companion object {
        private const val BOUNDS_MATCH_TOLERANCE = 12
        private const val CLIENT_SEARCH_DEPTH = 4
        private const val MAXIMUM_CHILDREN_PER_ELEMENT = 100
    }
}

internal fun selectMacContentBounds(
    frame: ScreenRectangle,
    descendants: Sequence<ScreenRectangle>,
): ScreenRectangle? = descendants
    .filter { it != frame && it.x >= frame.x && it.y >= frame.y && it.right <= frame.right && it.bottom <= frame.bottom }
    .filter { it.width >= frame.width - 40 && it.bottom >= frame.bottom - 40 && it.y - frame.y <= 100 }
    .filter { it.width.toLong() * it.height >= frame.width.toLong() * frame.height / 2 }
    .maxByOrNull { it.width.toLong() * it.height }

internal fun estimateMacContentBoundsFromTitleBar(
    frame: ScreenRectangle,
    buttons: List<ScreenRectangle>,
): ScreenRectangle? {
    // macOS centers window controls vertically in the title bar, so mirror their top inset.
    val topInset = buttons.asSequence()
        .filter { it.width > 0 && it.height > 0 && it.x >= frame.x && it.right <= frame.right }
        .filter { it.y >= frame.y && it.bottom <= frame.bottom }
        .map { (it.y - frame.y) * 2 + it.height }
        .firstOrNull { it in 16..80 && it < frame.height / 4 }
        ?: return null
    return ScreenRectangle(frame.x, frame.y + topInset, frame.width, frame.height - topInset)
}
