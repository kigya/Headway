package dev.kigya.headway.core.designSystem.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import dev.kigya.headway.core.designSystem.theme.LocalTheme
import dev.kigya.headway.core.designSystem.theme.dimension.FreudDimension
import dev.kigya.headway.core.designSystem.theme.color.FreudColor

internal object PreviewGrid {
    private val palette = FreudColor()

    val defaultCellSize: Dp = FreudDimension.dp4.value
    val defaultBorderWidth: Dp = FreudDimension.dp1.value

    @Composable
    fun colors(): Colors = if (LocalTheme.current.isDark) {
        Colors(
            a = palette.brown60.value,
            b = palette.brown50.value,
            border = palette.brown40.value,
        )
    } else {
        Colors(
            a = palette.yellow10.value,
            b = palette.yellow20.value,
            border = palette.yellow30.value,
        )
    }

    data class Colors(
        val a: Color,
        val b: Color,
        val border: Color,
    )
}

@Composable
internal fun Modifier.previewPixelGrid(
    cellSize: Dp = PreviewGrid.defaultCellSize,
    borderWidth: Dp = PreviewGrid.defaultBorderWidth,
): Modifier {
    val c = PreviewGrid.colors()

    return drawWithCache {
        val cellPx = cellSize.toPx().coerceAtLeast(1f)
        val borderPx = borderWidth.toPx().coerceAtLeast(1f)

        onDrawWithContent {
            drawRect(color = c.a)

            val w = size.width
            val h = size.height

            var row = 0
            var y = 0f
            while (y < h) {
                var col = 0
                var x = 0f
                while (x < w) {
                    if (row + col and 1 == 0) {
                        drawRect(
                            color = c.b,
                            topLeft = Offset(x, y),
                            size = Size(
                                width = minOf(cellPx, w - x),
                                height = minOf(cellPx, h - y),
                            ),
                        )
                    }
                    x += cellPx
                    col++
                }
                y += cellPx
                row++
            }

            drawContent()

            drawRect(
                color = c.border,
                style = Stroke(width = borderPx),
            )
        }
    }
}
