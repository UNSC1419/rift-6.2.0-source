package dev.nohus.rift.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import dev.nohus.rift.compose.theme.Spacing

fun Modifier.unboundedWithFadingRightEdge(): Modifier {
    return this
        .clipToBounds()
        .padding(end = Spacing.small)
        .fadingRightEdge()
        .padding(end = Spacing.large)
        .wrapContentWidth(align = Alignment.Start, unbounded = true)
}

fun Modifier.fadingRightEdge(): Modifier {
    return fadingEdge(rightEdgeMaskBrush)
}

private fun Modifier.fadingEdge(brush: Brush): Modifier {
    return graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            translate(left = size.width - MASK_SIZE) {
                drawRect(
                    size = Size(MASK_SIZE, size.height),
                    brush = brush,
                    blendMode = BlendMode.DstIn,
                )
            }
        }
}

private const val MASK_SIZE = 20f
private val rightEdgeMaskBrush = Brush.horizontalGradient(0f to Color.Black, 1f to Color.Transparent, startX = 0f, endX = MASK_SIZE)
