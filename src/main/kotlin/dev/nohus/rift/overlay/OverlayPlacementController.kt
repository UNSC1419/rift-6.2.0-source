package dev.nohus.rift.overlay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single
class OverlayPlacementController {
    private val _isPositioning = MutableStateFlow(false)
    val isPositioning = _isPositioning.asStateFlow()

    fun setPositioning(enabled: Boolean) {
        _isPositioning.value = enabled
    }
}
