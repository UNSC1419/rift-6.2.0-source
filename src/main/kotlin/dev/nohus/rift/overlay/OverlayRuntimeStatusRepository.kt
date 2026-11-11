package dev.nohus.rift.overlay

import dev.nohus.rift.utils.evewindows.DisplayScale
import dev.nohus.rift.utils.evewindows.EveWindow
import dev.nohus.rift.utils.evewindows.EveWindowId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single

internal data class OverlayWindowGeometryKey(
    val width: Int,
    val height: Int,
    val displayScale: DisplayScale,
    val processId: Int?,
)

internal fun EveWindow.getOverlayWindowGeometryKey(): OverlayWindowGeometryKey? = geometry.client.overlayBounds?.let { bounds ->
    OverlayWindowGeometryKey(bounds.width, bounds.height, geometry.displayScale, processId)
}

internal sealed interface OverlayWindowFailure {
    val geometry: OverlayWindowGeometryKey

    data class TooLarge(override val geometry: OverlayWindowGeometryKey) : OverlayWindowFailure
    data class RetryLater(
        override val geometry: OverlayWindowGeometryKey,
        val retryAfterNanos: Long,
    ) : OverlayWindowFailure
}

@Single
internal class OverlayRuntimeStatusRepository {
    private val _failures = MutableStateFlow<Map<EveWindowId, OverlayWindowFailure>>(emptyMap())
    val failures = _failures.asStateFlow()

    fun reportFailure(windowId: EveWindowId, failure: OverlayWindowFailure) {
        _failures.update { it + (windowId to failure) }
    }

    fun clearFailure(windowId: EveWindowId) {
        _failures.update { it - windowId }
    }

    fun clearExpiredRetries() {
        val now = System.nanoTime()
        _failures.update { failures ->
            failures.filterValues { it !is OverlayWindowFailure.RetryLater || it.retryAfterNanos - now > 0 }
        }
    }

    fun retainWindows(windowIds: Set<EveWindowId>) {
        _failures.update { failures -> failures.filterKeys { it in windowIds } }
    }
}
