package dev.nohus.rift.utils.evewindows

data class EveWindow(
    val id: EveWindowId,
    val characterName: String,
    val title: String,
    val geometry: EveWindowGeometry,
    val state: EveWindowState,
    val isActive: Boolean,
    val processId: Int? = null,
    val macWindowLevel: Int? = null,
)

fun EveWindow.canShowOverlay(): Boolean = state == EveWindowState.Normal && geometry.client.overlayBounds != null

@JvmInline
value class EveWindowId(val value: String)

data class EveWindowGeometry(
    val frame: WindowBoundsMeasurement,
    val client: WindowBoundsMeasurement,
    val displayScale: DisplayScale,
)

data class WindowBoundsMeasurement(
    val nativeBounds: ScreenRectangle?,
    val overlayBounds: ScreenRectangle?,
    val accuracy: BoundsAccuracy,
)

data class ScreenRectangle(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
) {
    val right: Int get() = x + width
    val bottom: Int get() = y + height
}

data class DisplayScale(
    val x: Double,
    val y: Double,
) {
    companion object {
        val Identity = DisplayScale(1.0, 1.0)
    }
}

enum class BoundsAccuracy {
    Exact,
    Estimated,
    Unavailable,
}

enum class EveWindowState {
    Normal,
    Minimized,
    Hidden,
}

enum class NativeCoordinateUnit {
    PhysicalPixels,
    LogicalPoints,
}

sealed interface GetEveWindowsResult {
    data class Success(val windows: List<EveWindow>) : GetEveWindowsResult
    data object PermissionRequired : GetEveWindowsResult
    data class Unsupported(val reason: String) : GetEveWindowsResult
    data class Failure(val reason: String) : GetEveWindowsResult
}
