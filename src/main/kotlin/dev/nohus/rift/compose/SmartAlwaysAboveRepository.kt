package dev.nohus.rift.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalWindowInfo
import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.utils.evewindows.EveWindowsRepository
import dev.nohus.rift.windowing.WindowManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

/**
 * Controls whether "always above" property of windows should take effect
 */
@Single
class SmartAlwaysAboveRepository(
    private val eveWindowsRepository: EveWindowsRepository,
    private val settings: Settings,
) {

    private var focusedRiftWindow: UUID? = null
    private val _isActive = MutableStateFlow(false)
    val isActive = _isActive.asStateFlow()

    suspend fun start() = coroutineScope {
        launch {
            eveWindowsRepository.state.map { state ->
                state.status == EveWindowsRepository.Status.Available && state.windows.any { it.isActive }
            }.distinctUntilChanged().collect {
                updateState()
            }
        }
    }

    @Composable
    fun registerWindow(state: WindowManager.RiftWindowState) {
        val isFocused = LocalWindowInfo.current.isWindowFocused
        val windowUuid = state.uuid
        LaunchedEffect(isFocused, windowUuid) {
            if (isFocused) {
                focusedRiftWindow = windowUuid
                updateState()
            } else {
                if (focusedRiftWindow == windowUuid) {
                    focusedRiftWindow = null
                    if (settings.isSmartAlwaysAbove) {
                        eveWindowsRepository.refresh()
                    }
                    updateState()
                }
            }
        }
    }

    private fun updateState() {
        val isSmartAboveDisabled = !settings.isSmartAlwaysAbove
        val isRiftFocused = focusedRiftWindow != null
        val state = eveWindowsRepository.state.value
        val isEveFocused = state.status == EveWindowsRepository.Status.Available && state.windows.any { it.isActive }
        val isActive = isSmartAboveDisabled || isRiftFocused || isEveFocused
        _isActive.value = isActive
    }
}
