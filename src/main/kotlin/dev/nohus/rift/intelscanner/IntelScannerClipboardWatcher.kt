package dev.nohus.rift.intelscanner

import dev.nohus.rift.clipboard.Clipboard
import dev.nohus.rift.intelscanner.DScanParser.DScanEntity
import dev.nohus.rift.logs.parse.CharacterNameValidator
import dev.nohus.rift.settings.persistence.Settings
import dev.nohus.rift.windowing.WindowManager
import dev.nohus.rift.windowing.WindowManager.RiftWindow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Single

@Single
class IntelScannerClipboardWatcher(
    private val clipboard: Clipboard,
    private val characterNameValidator: CharacterNameValidator,
    private val dScanParser: DScanParser,
    private val settings: Settings,
    private val windowManager: WindowManager,
) {

    sealed interface ClipboardResult {
        data class Characters(val names: List<String>) : ClipboardResult
        data class DScan(val entities: List<DScanEntity>) : ClipboardResult
        data object None : ClipboardResult
    }

    val result: StateFlow<ClipboardResult>
        field = MutableStateFlow<ClipboardResult>(ClipboardResult.None)

    suspend fun start(): Unit = coroutineScope {
        clipboard.state.collect { text ->
            onNewClipboard(text?.lines().orEmpty())
        }
    }

    private fun onNewClipboard(lines: List<String>) {
        val result = parseClipboard(lines)
        this.result.value = result

        val shouldOpenWindow = when (result) {
            is ClipboardResult.Characters -> settings.intelScanner.isOpenOnClipboardCharacterListEnabled
            is ClipboardResult.DScan -> settings.intelScanner.isOpenOnClipboardDScanEnabled
            ClipboardResult.None -> false
        }
        if (shouldOpenWindow) {
            windowManager.onWindowOpen(RiftWindow.IntelScanner, ifClosed = true)
        }
    }

    private fun parseClipboard(lines: List<String>): ClipboardResult {
        if (lines.isEmpty()) return ClipboardResult.None
        return if (dScanParser.isDScanLine(lines.first())) {
            val dscan = dScanParser.parseDScan(lines)
            if (dscan != null) ClipboardResult.DScan(dscan) else ClipboardResult.None
        } else {
            val names = getCharacterNames(lines)
            if (names != null && names.size >= 2) ClipboardResult.Characters(names) else ClipboardResult.None
        }
    }

    private fun getCharacterNames(lines: List<String>): List<String>? {
        return lines
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()
            .takeIf { it.isNotEmpty() && it.all { characterNameValidator.isValid(it) } }
    }
}
