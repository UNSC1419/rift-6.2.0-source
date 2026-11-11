package dev.nohus.rift.utils.evewindows

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

private val logger = KotlinLogging.logger {}

@Single
class EveWindowsRepository(
    private val getEveWindowsUseCase: GetEveWindowsUseCase,
) {

    data class State(
        val windows: List<EveWindow> = emptyList(),
        val status: Status = Status.Initializing,
        val updatedAt: Instant? = null,
    )

    sealed interface Status {
        data object Initializing : Status
        data object Available : Status
        data object PermissionRequired : Status
        data class Unsupported(val reason: String) : Status
        data class Failed(val reason: String) : Status
    }

    private val refreshMutex = Mutex()
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    suspend fun start() {
        while (true) {
            refresh()
            delay(POLL_INTERVAL)
        }
    }

    suspend fun refresh() = refreshMutex.withLock {
        val result = withContext(Dispatchers.IO) { getEveWindowsUseCase() }
        _state.value = when (result) {
            is GetEveWindowsResult.Success -> State(
                windows = result.windows,
                status = Status.Available,
                updatedAt = Instant.now(),
            )
            is GetEveWindowsResult.PermissionRequired -> _state.value.copy(
                status = Status.PermissionRequired,
            )
            is GetEveWindowsResult.Unsupported -> _state.value.copy(
                status = Status.Unsupported(result.reason),
            )
            is GetEveWindowsResult.Failure -> {
                if (_state.value.status != Status.Failed(result.reason)) {
                    logger.warn { "Could not inspect EVE windows: ${result.reason}" }
                }
                _state.value.copy(status = Status.Failed(result.reason))
            }
        }
    }

    companion object {
        private val POLL_INTERVAL = 500.milliseconds
    }
}
