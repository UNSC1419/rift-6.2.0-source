package dev.nohus.rift.loglite

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class LogLiteRepository {

    val flow: SharedFlow<LogMessage>
        field: MutableSharedFlow<LogMessage> = MutableSharedFlow()

    private val scope = CoroutineScope(Job())

    fun add(message: LogMessage) {
        scope.launch {
            flow.emit(message)
        }
    }
}
