package dev.kigya.headway.core.designSystem.component.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.kigya.headway.core.designSystem.component.FreudLottie
import dev.kigya.headway.core.designSystem.component.FreudLottieSource
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.component.preview.FreudLottiePreviewTheme.block
import dev.kigya.headway.core.designSystem.component.preview.FreudLottiePreviewTheme.surface
import dev.kigya.headway.core.designSystem.component.preview.FreudLottiePreviewTheme.text
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.core.designSystem.util.previewPixelGrid
import headway.core.design_system.generated.resources.Res

private object FreudLottiePreviewTheme : FreudTheme() {

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

private data class FreudLottiePreviewCase(
    val isDark: Boolean,
    val iterations: Int,
    val reverseOnRepeat: Boolean,
    val speed: Float,
)

private class FreudLottiePreviewCaseProvider : PreviewParameterProvider<FreudLottiePreviewCase> {
    override val values: Sequence<FreudLottiePreviewCase> = sequence {
        val baseModes = listOf(
            FreudLottiePreviewCase(
                isDark = false,
                iterations = 1,
                reverseOnRepeat = false,
                speed = 1f,
            ),
            FreudLottiePreviewCase(
                isDark = false,
                iterations = Int.MAX_VALUE,
                reverseOnRepeat = false,
                speed = 1f,
            ),
            FreudLottiePreviewCase(
                isDark = false,
                iterations = Int.MAX_VALUE,
                reverseOnRepeat = true,
                speed = 1f,
            ),
        )

        listOf(false, true).forEach { isDark ->
            baseModes.forEach { mode ->
                yield(mode.copy(isDark = isDark))
            }
        }
    }
}

@Preview(
    name = "FreudLottie – Theme x Mode x Source",
    showBackground = false,
)
@Composable
@Suppress("LongMethod")
private fun FreudLottiePreview(
    @PreviewParameter(FreudLottiePreviewCaseProvider::class) case: FreudLottiePreviewCase,
) {
    FreudTheme(isDark = case.isDark) {
        val ds = FreudTheme.DefaultFreudTheme

        val surface = FreudLottiePreviewTheme.colorScheme.surface.value
        val block = FreudLottiePreviewTheme.colorScheme.block.value
        val text = FreudLottiePreviewTheme.colorScheme.text

        val themeLabel = if (case.isDark) "Dark" else "Light"
        val modeLabel = when {
            case.iterations == 1 -> "Once"
            case.reverseOnRepeat -> "Loop • Reverse"
            else -> "Loop"
        }
        val dotLottieThemeLabel = if (case.isDark) "dark" else "light"
        val header = "$themeLabel • $modeLabel • speed=${case.speed}"

        var dotLottieFinished by remember(case) { mutableStateOf(false) }
        var jsonFinished by remember(case) { mutableStateOf(false) }

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
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                )

                Box(modifier = Modifier.height(ds.dimension.dp12.value))

                FreudText(
                    value = FreudTextValue.text(".lottie • auto theme=$dotLottieThemeLabel"),
                    color = text,
                    typography = ds.typography.textSmSemiBold,
                    align = TextAlign.Start,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                )

                Box(modifier = Modifier.height(ds.dimension.dp8.value))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(block)
                        .padding(ds.dimension.dp16.value),
                    contentAlignment = Alignment.Center,
                ) {
                    FreudLottie(
                        reader = { readPreviewDotLottie() },
                        source = FreudLottieSource.DotLottie,
                        iterations = case.iterations,
                        shouldBeReversedOnRepeat = case.reverseOnRepeat,
                        speed = case.speed,
                        isRestartable = true,
                        onFinish = { dotLottieFinished = true },
                        modifier = Modifier
                            .size(ds.dimension.dp128.value)
                            .previewPixelGrid(),
                    )
                }

                if (case.iterations == 1) {
                    Box(modifier = Modifier.height(ds.dimension.dp8.value))
                    FreudText(
                        value = FreudTextValue.text(
                            if (dotLottieFinished) ".lottie onFinish() fired" else ".lottie playing…",
                        ),
                        color = text,
                        typography = ds.typography.textSmSemiBold,
                        align = TextAlign.Start,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Box(modifier = Modifier.height(ds.dimension.dp16.value))

                FreudText(
                    value = FreudTextValue.text(".json • no theme"),
                    color = text,
                    typography = ds.typography.textSmSemiBold,
                    align = TextAlign.Start,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                )

                Box(modifier = Modifier.height(ds.dimension.dp8.value))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(block)
                        .padding(ds.dimension.dp16.value),
                    contentAlignment = Alignment.Center,
                ) {
                    FreudLottie(
                        reader = { readPreviewJsonLottie() },
                        source = FreudLottieSource.Json,
                        iterations = case.iterations,
                        shouldBeReversedOnRepeat = case.reverseOnRepeat,
                        speed = case.speed,
                        isRestartable = true,
                        onFinish = { jsonFinished = true },
                        modifier = Modifier
                            .size(ds.dimension.dp128.value)
                            .previewPixelGrid(),
                    )
                }

                if (case.iterations == 1) {
                    Box(modifier = Modifier.height(ds.dimension.dp8.value))
                    FreudText(
                        value = FreudTextValue.text(
                            if (jsonFinished) ".json onFinish() fired" else ".json playing…",
                        ),
                        color = text,
                        typography = ds.typography.textSmSemiBold,
                        align = TextAlign.Start,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

private suspend fun readPreviewJsonLottie(): ByteArray = Res.readBytes(LOTTIE_JSON_PATH)
private suspend fun readPreviewDotLottie(): ByteArray = Res.readBytes(LOTTIE_DOT_LOTTIE_PATH)

private const val LOTTIE_JSON_PATH = "files/lottie_el_baion.json"
private const val LOTTIE_DOT_LOTTIE_PATH = "files/lottie_stub_robot.lottie"
