package dev.nohus.rift.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object LoggingRepository {

    private const val MAX_SIZE = 20_000
    val state: StateFlow<List<ILoggingEvent>>
        field: MutableStateFlow<List<ILoggingEvent>> = MutableStateFlow(emptyList())

    fun append(event: ILoggingEvent) {
        state.value += event

        if (state.value.size > MAX_SIZE) {
            state.value = state.value.drop(MAX_SIZE / 2)
        }
    }
}
