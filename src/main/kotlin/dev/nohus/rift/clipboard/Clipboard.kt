package dev.nohus.rift.clipboard

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okio.IOException
import org.koin.core.annotation.Single
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTimedValue

private val logger = KotlinLogging.logger {}

@Single
class Clipboard {

    private val _state = MutableStateFlow<String?>(null)
    val state = _state.asStateFlow()

    suspend fun start() = coroutineScope {
        launch {
            observeClipboard()
        }
    }

    private suspend fun observeClipboard() = withContext(Dispatchers.IO) {
        while (true) {
            var pollingDelay = 100.milliseconds
            try {
                val result = measureTimedValue {
                    getClipboardContents()
                }
                _state.value = result.value
                pollingDelay = (result.duration.inWholeMilliseconds * 4).coerceIn(250, 1000).milliseconds
            } catch (_: IllegalStateException) {
                // The clipboard is busy; keep the last successful value and retry shortly
            } catch (e: IOException) {
                logger.debug(e) { "Could not read clipboard text, retrying" }
            }
            delay(pollingDelay)
        }
    }

    private fun getClipboardContents(): String? {
        return try {
            clipboard.getData(DataFlavor.stringFlavor) as? String
        } catch (_: UnsupportedFlavorException) {
            null
        }
    }

    companion object {
        private val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        private val copyScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        private val copyMutex = Mutex()

        fun copy(text: String, onCopied: () -> Unit = {}) {
            val selection = StringSelection(text)
            copyContents(selection, onCopied)
        }

        private fun copyContents(selection: Transferable, onCopied: () -> Unit) {
            copyScope.launch {
                val isCopied = copyMutex.withLock {
                    for (attempt in 1..5) {
                        try {
                            clipboard.setContents(selection, null)
                            return@withLock true
                        } catch (e: IllegalStateException) {
                            if (attempt == 5) {
                                logger.warn(e) { "Failed to copy because the clipboard is busy" }
                            } else {
                                delay(100.milliseconds)
                            }
                        } catch (e: Exception) {
                            logger.error(e) { "Failed to copy to clipboard" }
                            return@withLock false
                        }
                    }
                    false
                }
                if (isCopied) onCopied()
            }
        }

        fun copy(image: ImageBitmap) {
            logger.debug { "Copying image: ${image.width}x${image.height}" }
            try {
                val awtImage = image.toAwtImage()
                val selection = object : Transferable {
                    override fun getTransferDataFlavors(): Array<DataFlavor> = arrayOf(DataFlavor.imageFlavor)

                    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean = flavor == DataFlavor.imageFlavor

                    override fun getTransferData(flavor: DataFlavor): Any {
                        if (!isDataFlavorSupported(flavor)) {
                            logger.error { "Cannot copy image because data flavor is not supported: $flavor" }
                            throw UnsupportedFlavorException(flavor)
                        } else {
                            logger.debug { "Data flavor is supported: $flavor" }
                        }
                        return awtImage
                    }
                }
                copyContents(selection) {
                    logger.info { "Image copied to clipboard" }
                }
            } catch (e: Exception) {
                logger.error(e) { "Failed to copy image" }
            }
        }
    }
}
