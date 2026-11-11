package dev.nohus.rift.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp

@Composable
fun VerticalStaggeredGrid(
    minColumnWidth: Dp,
    horizontalSpacing: Dp,
    verticalSpacing: Dp,
    content: @Composable () -> Unit,
) {
    Layout(content = content) { measurables, constraints ->
        val availableWidth =
            if (constraints.maxWidth == Constraints.Infinity) constraints.minWidth else constraints.maxWidth
        if (availableWidth == 0 || measurables.isEmpty()) {
            return@Layout layout(availableWidth, constraints.minHeight) {}
        }

        val horizontalSpacingPx = horizontalSpacing.roundToPx()
        val verticalSpacingPx = verticalSpacing.roundToPx()
        val minColumnWidthPx = minColumnWidth.roundToPx()
        val columnCount =
            ((availableWidth + horizontalSpacingPx) / (minColumnWidthPx + horizontalSpacingPx)).coerceAtLeast(1)
        val columnWidth = (availableWidth - horizontalSpacingPx * (columnCount - 1)) / columnCount
        val columnHeights = IntArray(columnCount)
        val itemColumns = IntArray(measurables.size)
        val itemYPositions = IntArray(measurables.size)
        val placeables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(
                Constraints(
                    minWidth = columnWidth,
                    maxWidth = columnWidth,
                    maxHeight = Constraints.Infinity,
                ),
            )
            val column = columnHeights.indices.minBy { columnHeights[it] }
            val y = if (columnHeights[column] == 0) 0 else columnHeights[column] + verticalSpacingPx
            itemColumns[index] = column
            itemYPositions[index] = y
            columnHeights[column] = y + placeable.height
            placeable
        }
        val maxHeight = if (constraints.maxHeight == Constraints.Infinity) Int.MAX_VALUE else constraints.maxHeight
        val height = columnHeights.max().coerceIn(constraints.minHeight, maxHeight)

        layout(availableWidth, height) {
            placeables.forEachIndexed { index, placeable ->
                val column = itemColumns[index]
                placeable.place(
                    x = column * (columnWidth + horizontalSpacingPx),
                    y = itemYPositions[index],
                )
            }
        }
    }
}
