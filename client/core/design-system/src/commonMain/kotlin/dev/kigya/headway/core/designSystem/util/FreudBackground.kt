package dev.kigya.headway.core.designSystem.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudBackgroundTheme.line

enum class FreudBackgroundPattern {
    None,
    Waves,
}

private enum class FreudBackgroundWaveOrientation {
    Horizontal,
    Vertical,
}

private object FreudBackgroundTheme : FreudTheme() {
    val FreudColorScheme.line
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = FreudDsToken(Color.White),
            dark = super.color.brown80,
        )
}

private object FreudBackgroundGeometry {
    val strokeDp = 1.dp
    val lineSpacingDp = 28.dp
    val waveAmplitudeDp = 20.dp
    val waveLengthDp = 104.dp
}

private data class FreudBackgroundWaveDrawSpec(
    val strokeWidth: Float,
    val lineSpacing: Float,
    val waveAmplitude: Float,
    val waveLength: Float,
    val orientation: FreudBackgroundWaveOrientation,
)

@Composable
fun Modifier.background(
    color: FreudDsToken<Color>,
    pattern: FreudBackgroundPattern = FreudBackgroundPattern.None,
    shape: Shape = RectangleShape,
): Modifier {
    val lineColor = FreudBackgroundTheme.colorScheme.line.value
    val density = LocalDensity.current
    val waveDrawSpec = FreudBackgroundWaveDrawSpec(
        strokeWidth = with(density) { FreudBackgroundGeometry.strokeDp.toPx() },
        lineSpacing = with(density) { FreudBackgroundGeometry.lineSpacingDp.toPx() },
        waveAmplitude = with(density) { FreudBackgroundGeometry.waveAmplitudeDp.toPx() },
        waveLength = with(density) { FreudBackgroundGeometry.waveLengthDp.toPx() },
        orientation = if (isWide()) {
            FreudBackgroundWaveOrientation.Horizontal
        } else {
            FreudBackgroundWaveOrientation.Vertical
        },
    )
    return this.then(
        freudPatternBackground(
            color = color.value,
            pattern = pattern,
            shape = shape,
            lineColor = lineColor,
            waveDrawSpec = waveDrawSpec,
        ),
    )
}

private fun Modifier.freudPatternBackground(
    color: Color,
    pattern: FreudBackgroundPattern,
    shape: Shape,
    lineColor: Color,
    waveDrawSpec: FreudBackgroundWaveDrawSpec,
): Modifier = this
    .clip(shape)
    .drawWithCache {
        onDrawWithContent {
            drawRect(color = color)

            if (pattern == FreudBackgroundPattern.Waves) {
                when (waveDrawSpec.orientation) {
                    FreudBackgroundWaveOrientation.Horizontal ->
                        drawHorizontalWaves(
                            lineColor = lineColor,
                            strokeWidth = waveDrawSpec.strokeWidth,
                            lineSpacing = waveDrawSpec.lineSpacing,
                            waveAmplitude = waveDrawSpec.waveAmplitude,
                            waveLength = waveDrawSpec.waveLength,
                        )

                    FreudBackgroundWaveOrientation.Vertical ->
                        drawVerticalWaves(
                            lineColor = lineColor,
                            strokeWidth = waveDrawSpec.strokeWidth,
                            lineSpacing = waveDrawSpec.lineSpacing,
                            waveAmplitude = waveDrawSpec.waveAmplitude,
                            waveLength = waveDrawSpec.waveLength,
                        )
                }
            }

            drawContent()
        }
    }

private fun ContentDrawScope.drawHorizontalWaves(
    lineColor: Color,
    strokeWidth: Float,
    lineSpacing: Float,
    waveAmplitude: Float,
    waveLength: Float,
) {
    var currentY = -lineSpacing

    while (currentY <= size.height + lineSpacing) {
        drawPath(
            path = createHorizontalWavePath(
                y = currentY,
                waveAmplitude = waveAmplitude,
                waveLength = waveLength,
            ),
            color = lineColor.copy(alpha = WAVES_ALPHA),
            style = Stroke(width = strokeWidth),
        )

        currentY += lineSpacing
    }
}

private fun ContentDrawScope.drawVerticalWaves(
    lineColor: Color,
    strokeWidth: Float,
    lineSpacing: Float,
    waveAmplitude: Float,
    waveLength: Float,
) {
    var currentX = -lineSpacing

    while (currentX <= size.width + lineSpacing) {
        drawPath(
            path = createVerticalWavePath(
                x = currentX,
                waveAmplitude = waveAmplitude,
                waveLength = waveLength,
            ),
            color = lineColor.copy(alpha = WAVES_ALPHA),
            style = Stroke(width = strokeWidth),
        )

        currentX += lineSpacing
    }
}

private fun ContentDrawScope.createHorizontalWavePath(
    y: Float,
    waveAmplitude: Float,
    waveLength: Float,
): Path {
    val path = Path()
    var currentX = -waveLength
    var direction = 1f

    path.moveTo(currentX, y)

    while (currentX <= size.width + waveLength) {
        val nextX = currentX + waveLength
        val controlX = currentX + (waveLength / 2f)

        path.quadraticTo(
            x1 = controlX,
            y1 = y + (waveAmplitude * direction),
            x2 = nextX,
            y2 = y,
        )

        currentX = nextX
        direction *= -1f
    }

    return path
}

private fun ContentDrawScope.createVerticalWavePath(
    x: Float,
    waveAmplitude: Float,
    waveLength: Float,
): Path {
    val path = Path()
    var currentY = -waveLength
    var direction = 1f

    path.moveTo(x, currentY)

    while (currentY <= size.height + waveLength) {
        val nextY = currentY + waveLength
        val controlY = currentY + (waveLength / 2f)

        path.quadraticTo(
            x1 = x + (waveAmplitude * direction),
            y1 = controlY,
            x2 = x,
            y2 = nextY,
        )

        currentY = nextY
        direction *= -1f
    }

    return path
}

private const val WAVES_ALPHA = 0.5f
