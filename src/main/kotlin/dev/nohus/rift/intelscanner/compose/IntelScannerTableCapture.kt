package dev.nohus.rift.intelscanner.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.intelscanner.IntelScannerViewModel.Scan
import dev.nohus.rift.intelscanner.IntelScannerViewModel.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun IntelScannerTableCapture(
    state: UiState,
    scan: Scan.LocalScan,
    onCaptured: suspend (ImageBitmap?) -> Unit,
) {
    val sortedItems = remember(scan.items, state.settings) {
        sortCharacters(scan.items, state.settings)
    }
    val columnWidths = getColumnsWidths(state.settings, sortedItems)
    val tableWidth = getTableWidth(state.settings, columnWidths)
    val graphicsLayer = rememberGraphicsLayer()
    var hasDrawn by remember { mutableStateOf(false) }

    LaunchedEffect(hasDrawn) {
        if (hasDrawn) {
            delay(ScreenshotImageLoadDelay)
            val image = withContext(Dispatchers.Default) {
                runCatching { graphicsLayer.toImageBitmap() }.getOrNull()
            }
            onCaptured(image)
        }
    }

    Layout(
        content = {
            Column(
                modifier = Modifier
                    .width(tableWidth)
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        hasDrawn = true
                    }
                    .background(RiftTheme.colors.windowBackground),
            ) {
                if (state.settings.isShowingSummary) {
                    IntelScannerSummary(
                        characters = scan.items,
                        settings = state.settings,
                    )
                }
                HeaderRow(
                    settings = state.settings,
                    columnWidths = columnWidths,
                    onSortingColumnChange = {},
                )
                Spacer(Modifier.height(Spacing.verySmall))
                sortedItems.take(MaxCharactersToCapture).forEachIndexed { rowIndex, character ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clipToBounds(),
                    ) {
                        CharacterRow(rowIndex, character, scan.timestamp, state.settings, columnWidths)
                    }
                }
                Spacer(Modifier.height(Spacing.verySmall))
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { measurables, constraints ->
        val table = measurables.single().measure(
            Constraints(
                maxWidth = Constraints.Infinity,
                maxHeight = Constraints.Infinity,
            )
        )
        layout(constraints.maxWidth, constraints.maxHeight) {
            table.place(0, 0)
        }
    }
}

private val ScreenshotImageLoadDelay = 500.milliseconds
private const val MaxCharactersToCapture = 250
