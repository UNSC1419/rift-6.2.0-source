package dev.nohus.rift.overlay.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.theme.Cursors
import dev.nohus.rift.compose.theme.RiftTheme
import dev.nohus.rift.compose.theme.Spacing
import dev.nohus.rift.utils.Pos
import kotlin.math.roundToInt

private const val DRAG_HANDLE_OFFSET_DP = 46

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MovableOverlayContent(
    neocomWidthPx: Int,
    savedPosition: Pos?,
    isPositioning: Boolean,
    onPositionChanged: (Pos) -> Unit,
    onPress: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    var draggedPosition by remember { mutableStateOf<Pos?>(null) }
    var contentSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .pointerHoverIcon(PointerIcon(Cursors.pointer))
            .onPointerEvent(PointerEventType.Press, pass = PointerEventPass.Initial) { onPress() },
    ) {
        val neocomWidth = with(density) { neocomWidthPx.toDp() }
        val defaultPosition = Pos((neocomWidth + 324.dp).value.roundToInt(), 50)
        val contentWidth = with(density) { contentSize.width.toDp() }
        val contentHeight = with(density) { contentSize.height.toDp() }
        val maxX = (maxWidth - contentWidth).value.coerceAtLeast(0f).roundToInt()
        val maxY = (maxHeight - contentHeight).value.coerceAtLeast(0f).roundToInt()
        val position = (draggedPosition ?: savedPosition ?: defaultPosition).coerceWithin(maxX, maxY)
        val currentPosition by rememberUpdatedState(position)

        if (isPositioning) {
            val handleColor = RiftTheme.colors.textHighlighted
            Canvas(
                modifier = Modifier
                    .offset(x = (position.x - DRAG_HANDLE_OFFSET_DP).dp, y = position.y.dp)
                    .size(24.dp)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .border(1.dp, RiftTheme.colors.borderGreyLight)
                    .pointerHoverIcon(PointerIcon(Cursors.move))
                    .pointerInput(maxX, maxY, density) {
                        var startPosition = Pos(0, 0)
                        var dragX = 0f
                        var dragY = 0f
                        detectDragGestures(
                            onDragStart = {
                                startPosition = currentPosition
                                draggedPosition = startPosition
                                dragX = 0f
                                dragY = 0f
                            },
                            onDragEnd = {
                                draggedPosition?.let(onPositionChanged)
                                draggedPosition = null
                            },
                            onDragCancel = { draggedPosition = null },
                            onDrag = { change, amount ->
                                change.consume()
                                dragX += amount.x
                                dragY += amount.y
                                draggedPosition = Pos(
                                    startPosition.x + (dragX / density.density).roundToInt(),
                                    startPosition.y + (dragY / density.density).roundToInt(),
                                ).coerceWithin(maxX, maxY)
                            },
                        )
                    },
            ) {
                for (x in listOf(0.38f, 0.62f)) {
                    for (y in listOf(0.3f, 0.5f, 0.7f)) {
                        drawCircle(
                            color = handleColor,
                            radius = size.minDimension * 0.055f,
                            center = Offset(size.width * x, size.height * y),
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .offset(x = position.x.dp, y = position.y.dp)
                .onSizeChanged { contentSize = it },
            content = content,
        )
    }
}

private fun Pos.coerceWithin(maxX: Int, maxY: Int) = Pos(
    x = x.coerceIn(minOf(DRAG_HANDLE_OFFSET_DP, maxX), maxX),
    y = y.coerceIn(0, maxY),
)
