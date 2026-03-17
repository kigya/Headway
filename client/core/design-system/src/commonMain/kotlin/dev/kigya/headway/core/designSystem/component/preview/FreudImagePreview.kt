package dev.kigya.headway.core.designSystem.component.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.kigya.headway.core.designSystem.component.FreudImage
import dev.kigya.headway.core.designSystem.component.FreudSpacer
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.component.preview.FreudImagePreviewTheme.block
import dev.kigya.headway.core.designSystem.component.preview.FreudImagePreviewTheme.surface
import dev.kigya.headway.core.designSystem.component.preview.FreudImagePreviewTheme.text
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import headway.core.design_system.generated.resources.Res
import headway.core.design_system.generated.resources.freud_robot_dark_theme_img

private object FreudImagePreviewTheme : FreudTheme() {
    val FreudColorScheme.surface
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray10,
            dark = super.color.brown90,
        )

    val FreudColorScheme.block
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray20,
            dark = super.color.brown80,
        )

    val FreudColorScheme.text
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray100,
            dark = super.color.brown10,
        )
}

private data class FreudImagePreviewCase(
    val isDark: Boolean,
    val contentScale: ContentScale,
    val label: String,
)

private class FreudImagePreviewCaseProvider : PreviewParameterProvider<FreudImagePreviewCase> {
    override val values: Sequence<FreudImagePreviewCase> = sequence {
        val scales = listOf(
            ContentScale.Fit to "Fit (Default)",
            ContentScale.Crop to "Crop",
            ContentScale.FillBounds to "Fill Bounds"
        )
        for (isDark in listOf(false, true)) {
            for (scale in scales) {
                yield(FreudImagePreviewCase(isDark, scale.first, scale.second))
            }
        }
    }
}

@Preview(name = "FreudImage – Scale Variants")
@Composable
private fun FreudImagePreview(
    @PreviewParameter(FreudImagePreviewCaseProvider::class) case: FreudImagePreviewCase,
) {
    FreudTheme(isDark = case.isDark) {
        val ds = FreudTheme.DefaultFreudTheme
        val surface = FreudImagePreviewTheme.colorScheme.surface.value
        val block = FreudImagePreviewTheme.colorScheme.block.value
        val text = FreudImagePreviewTheme.colorScheme.text

        val themeLabel = if (case.isDark) "Dark" else "Light"

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(surface)
                .padding(ds.dimension.dp16.value),
        ) {
            FreudText(
                value = "$themeLabel • ${case.label}",
                color = text,
                typography = ds.typography.labelSm,
                align = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )

            FreudSpacer(size = ds.dimension.dp12)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(block, ds.shape.rounding16.value),
                contentAlignment = Alignment.Center,
            ) {
                FreudImage(
                    resource = Res.drawable.freud_robot_dark_theme_img,
                    contentScale = case.contentScale,
                    modifier = Modifier.padding(ds.dimension.dp16.value),
                )
            }
        }
    }
}
