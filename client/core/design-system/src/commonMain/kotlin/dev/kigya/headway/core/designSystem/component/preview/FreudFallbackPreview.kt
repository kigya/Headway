@file:Suppress("MagicNumber", "LongMethod", "TooManyFunctions")

package dev.kigya.headway.core.designSystem.component.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.kigya.headway.core.designSystem.component.FreudFallbackContent
import dev.kigya.headway.core.designSystem.component.FreudHorizontalButton
import dev.kigya.headway.core.designSystem.component.FreudHorizontalButtonSize
import dev.kigya.headway.core.designSystem.component.FreudSpacer
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.component.preview.FreudFallbackPreviewTheme.block
import dev.kigya.headway.core.designSystem.component.preview.FreudFallbackPreviewTheme.container
import dev.kigya.headway.core.designSystem.component.preview.FreudFallbackPreviewTheme.content
import dev.kigya.headway.core.designSystem.component.preview.FreudFallbackPreviewTheme.surface
import dev.kigya.headway.core.designSystem.component.preview.FreudFallbackPreviewTheme.text
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudTextValue

private object FreudFallbackPreviewTheme : FreudTheme() {
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

    val FreudColorScheme.container
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown80,
            dark = super.color.brown60,
        )

    val FreudColorScheme.content
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown10,
        )
}

private const val SEPARATOR = " • "

private data class FreudFallbackPreviewCase(
    val isDark: Boolean,
    val isWide: Boolean,
    val isError: Boolean,
    val isOnline: Boolean,
) {
    val label: String
        get() = buildString {
            append(if (isDark) "Dark" else "Light")
            append(SEPARATOR)
            append(if (isWide) "Wide" else "Narrow")
            append(SEPARATOR)
            append(
                when {
                    !isError -> "Content"
                    isOnline -> "Error Stub"
                    else -> "Network Stub"
                },
            )
        }
}

private class FreudFallbackPreviewCaseProvider : PreviewParameterProvider<FreudFallbackPreviewCase> {
    override val values: Sequence<FreudFallbackPreviewCase> = sequence {
        val themes = listOf(false, true)
        val widths = listOf(false, true)

        val baseCases = listOf(
            FreudFallbackPreviewCase(isDark = false, isWide = false, isError = false, isOnline = true),
            FreudFallbackPreviewCase(isDark = false, isWide = false, isError = true, isOnline = true),
            FreudFallbackPreviewCase(isDark = false, isWide = false, isError = true, isOnline = false),
        )

        for (isDark in themes) {
            for (isWide in widths) {
                for (base in baseCases) {
                    yield(base.copy(isDark = isDark, isWide = isWide))
                }
            }
        }
    }
}

@Preview(
    name = "FreudFallback – Theme x Layout x State",
    showBackground = false,
)
@Composable
@Suppress("UnusedPrivateMember")
private fun FreudFallbackPreview(
    @PreviewParameter(FreudFallbackPreviewCaseProvider::class) case: FreudFallbackPreviewCase,
) {
    FreudTheme(isDark = case.isDark) {
        val ds = FreudTheme.DefaultFreudTheme

        val surface = FreudFallbackPreviewTheme.colorScheme.surface.value
        val block = FreudFallbackPreviewTheme.colorScheme.block.value
        val text = FreudFallbackPreviewTheme.colorScheme.text
        val container = FreudFallbackPreviewTheme.colorScheme.container
        val contentColor = FreudFallbackPreviewTheme.colorScheme.content

        var isError by remember(case) { mutableStateOf(case.isError) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(surface)
                .padding(ds.dimension.dp16.value),
        ) {
            FreudText(
                value = FreudTextValue.text(case.label),
                color = text,
                typography = ds.typography.labelSm,
                align = TextAlign.Start,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
            )

            FreudSpacer(size = ds.dimension.dp12)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(block, ds.shape.rounding24.value)
                    .padding(ds.dimension.dp16.value),
            ) {
                FreudFallbackContent(
                    isError = isError,
                    isOnline = case.isOnline,
                    onRetry = { isError = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (case.isWide) {
                                Modifier.height(420.dp)
                            } else {
                                Modifier.height(560.dp)
                            },
                        ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(block, ds.shape.rounding24.value)
                            .padding(ds.dimension.dp24.value),
                    ) {
                        FreudText(
                            value = FreudTextValue.text("Screen content is visible here"),
                            color = text,
                            typography = ds.typography.textMdBold,
                            align = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        FreudSpacer(size = ds.dimension.dp16)

                        FreudHorizontalButton(
                            text = FreudTextValue.text("Simulate Error"),
                            onClick = { isError = true },
                            containerColor = container,
                            contentColor = contentColor,
                            size = FreudHorizontalButtonSize.SMALL,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}
