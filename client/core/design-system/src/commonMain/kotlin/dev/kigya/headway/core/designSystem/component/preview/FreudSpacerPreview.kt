package dev.kigya.headway.core.designSystem.component.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import dev.kigya.headway.core.designSystem.component.FreudSpacer
import dev.kigya.headway.core.designSystem.component.FreudSpacerOrientation
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.component.preview.FreudSpacerPreviewTheme.block
import dev.kigya.headway.core.designSystem.component.preview.FreudSpacerPreviewTheme.surface
import dev.kigya.headway.core.designSystem.component.preview.FreudSpacerPreviewTheme.text
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.core.designSystem.util.previewPixelGrid

private object FreudSpacerPreviewTheme : FreudTheme() {

    val FreudColorScheme.surface: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray10,
            dark = super.color.brown90,
        )

    val FreudColorScheme.block: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray20,
            dark = super.color.brown80,
        )

    val FreudColorScheme.text: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray100,
            dark = super.color.brown10,
        )
}

private data class FreudSpacerPreviewCase(
    val isDark: Boolean,
    val orientation: FreudSpacerOrientation,
    val size: FreudDsToken<Dp>,
)

private class FreudSpacerPreviewCaseProvider : PreviewParameterProvider<FreudSpacerPreviewCase> {
    override val values: Sequence<FreudSpacerPreviewCase> = sequence {
        val themes = listOf(false, true)
        val orientations = listOf(FreudSpacerOrientation.VERTICAL, FreudSpacerOrientation.HORIZONTAL)
        val sizes = listOf(
            FreudTheme.DefaultFreudTheme.dimension.dp8,
            FreudTheme.DefaultFreudTheme.dimension.dp24,
        )

        for (isDark in themes) {
            for (orientation in orientations) {
                for (size in sizes) {
                    yield(FreudSpacerPreviewCase(isDark, orientation, size))
                }
            }
        }
    }
}

@Preview(
    name = "FreudSpacer – Theme x Orientation x Size",
    showBackground = false,
)
@Composable
@Suppress("LongMethod")
private fun FreudSpacerPreview(
    @PreviewParameter(FreudSpacerPreviewCaseProvider::class) case: FreudSpacerPreviewCase,
) {
    FreudTheme(isDark = case.isDark) {
        val ds = FreudTheme.DefaultFreudTheme

        val surface = FreudSpacerPreviewTheme.colorScheme.surface.value
        val block = FreudSpacerPreviewTheme.colorScheme.block.value
        val text = FreudSpacerPreviewTheme.colorScheme.text

        val themeLabel = if (case.isDark) "Dark" else "Light"
        val orientationLabel = when (case.orientation) {
            FreudSpacerOrientation.VERTICAL -> "Vertical"
            FreudSpacerOrientation.HORIZONTAL -> "Horizontal"
        }
        val header = "$themeLabel • $orientationLabel • size=${case.size.value}"

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(surface)
                .padding(ds.dimension.dp16.value),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                FreudText(
                    value = FreudTextValue.text(header),
                    color = text,
                    typography = ds.typography.labelSm,
                    align = TextAlign.Start,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                )

                Box(modifier = Modifier.height(ds.dimension.dp12.value))

                when (case.orientation) {
                    FreudSpacerOrientation.VERTICAL ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            PreviewBlock(label = "Above", background = block, textColor = text)

                            FreudSpacer(
                                size = case.size,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .previewPixelGrid(),
                            )

                            PreviewBlock(label = "Below", background = block, textColor = text)
                        }

                    FreudSpacerOrientation.HORIZONTAL ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(ds.dimension.dp56.value),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PreviewBlock(
                                label = "Left",
                                background = block,
                                textColor = text,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                            )

                            FreudSpacer(
                                size = case.size,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .previewPixelGrid(),
                            )

                            PreviewBlock(
                                label = "Right",
                                background = block,
                                textColor = text,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                            )
                        }
                }
            }
        }
    }
}

@Composable
private fun PreviewBlock(
    label: String,
    background: Color,
    textColor: FreudDsToken<Color>,
    modifier: Modifier = Modifier,
) {
    val ds = FreudTheme.DefaultFreudTheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .padding(ds.dimension.dp12.value),
        contentAlignment = Alignment.CenterStart,
    ) {
        FreudText(
            value = FreudTextValue.text(label),
            color = textColor,
            typography = ds.typography.labelSm,
            align = TextAlign.Start,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
