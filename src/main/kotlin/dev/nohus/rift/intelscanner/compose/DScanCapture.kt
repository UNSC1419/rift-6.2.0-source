package dev.nohus.rift.intelscanner.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.RiftSolarSystemChip
import dev.nohus.rift.compose.VerticalStaggeredGrid
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.intelscanner.IntelScannerViewModel.Scan
import dev.nohus.rift.intelscanner.IntelScannerViewModel.UiState
import dev.nohus.rift.intelscanner.LoadDScanIntelUseCase.DScanItem
import dev.nohus.rift.localization.resolve
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun DScanCapture(
    state: UiState,
    scan: Scan.DScan,
    onCaptured: suspend (ImageBitmap?) -> Unit,
) {
    val graphicsLayer = rememberGraphicsLayer()
    var hasDrawn by remember { mutableStateOf(false) }

    LaunchedEffect(hasDrawn) {
        if (hasDrawn) {
            delay(DScanCaptureImageLoadDelay)
            val image = withContext(Dispatchers.Default) {
                try {
                    graphicsLayer.toImageBitmap()
                } catch (_: Exception) {
                    null
                }
            }
            onCaptured(image)
        }
    }

    Layout(
        content = {
            Column(
                modifier = Modifier
                    .width(DScanCaptureWidth)
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        hasDrawn = true
                    }
                    .background(RiftTheme.colors.windowBackground)
                    .padding(Spacing.medium),
            ) {
                scan.dscan.systemChip?.let {
                    RiftSolarSystemChip(it)
                    Spacer(Modifier.height(Spacing.medium))
                }
                DScanCaptureGrid(
                    state = state,
                    items = scan.dscan.items,
                    system = scan.dscan.systemChip?.name,
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
    ) { measurables, constraints ->
        val content = measurables.single().measure(
            Constraints(
                maxWidth = Constraints.Infinity,
                maxHeight = Constraints.Infinity,
            ),
        )
        layout(constraints.maxWidth, constraints.maxHeight) {
            content.place(0, 0)
        }
    }
}

@Composable
private fun DScanCaptureGrid(state: UiState, items: List<DScanItem>, system: String?) {
    val panes = remember(items, state.settings.enabledDScanSections) {
        getDScanPanes(items, state.settings.enabledDScanSections)
    }
    VerticalStaggeredGrid(
        minColumnWidth = 320.dp,
        horizontalSpacing = Spacing.medium,
        verticalSpacing = Spacing.medium,
    ) {
        panes.forEach { pane ->
            DScanPane(
                title = pane.title.resolve(),
                isExpanded = null,
                onClick = null,
            ) {
                DScanPaneContent(
                    pane = pane,
                    state = state,
                    system = system,
                    hoveredShipGroup = null,
                    onShipGroupHover = {},
                )
            }
        }
    }
}

private val DScanCaptureImageLoadDelay = 500.milliseconds
private val DScanCaptureWidth = 1024.dp
