package dev.kigya.headway.core.designSystem.component.preview

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.component.preview.FreudTextPreviewTheme.primaryText
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.PreviewText

private object FreudTextPreviewTheme : FreudTheme() {
    val FreudColorScheme.primaryText: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown30,
            dark = super.color.orange10,
        )
}

private data class FreudTextPreviewCase(
    val isDark: Boolean,
    val maxLines: Int,
)

private class FreudTextPreviewCaseProvider : PreviewParameterProvider<FreudTextPreviewCase> {
    override val values: Sequence<FreudTextPreviewCase> = sequence {
        val themes = listOf(false, true)
        val maxLinesVariants = listOf(1, Int.MAX_VALUE)

        for (isDark in themes) {
            for (maxLines in maxLinesVariants) {
                yield(
                    FreudTextPreviewCase(
                        isDark = isDark,
                        maxLines = maxLines,
                    ),
                )
            }
        }
    }
}

@Preview(
    name = "FreudText – Theme x maxLines",
    showBackground = false
)
@Composable
private fun FreudTextPreview(
    @PreviewParameter(FreudTextPreviewCaseProvider::class) case: FreudTextPreviewCase,
) {
    FreudTheme(isDark = case.isDark) {

        FreudText(
            value = PreviewText.loremForMaxLines(maxLines = case.maxLines),
            color = FreudTextPreviewTheme.colorScheme.primaryText,
            typography = FreudTextPreviewTheme.typography.textMdBold,
            align = TextAlign.Start,
            maxLines = case.maxLines,
            modifier = Modifier
                .fillMaxWidth()
                .padding(FreudTheme.DefaultFreudTheme.dimension.dp16.value),
        )
    }
}
