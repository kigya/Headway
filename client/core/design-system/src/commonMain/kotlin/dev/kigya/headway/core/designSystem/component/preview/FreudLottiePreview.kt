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
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.component.preview.FreudLottiePreviewTheme.block
import dev.kigya.headway.core.designSystem.component.preview.FreudLottiePreviewTheme.surface
import dev.kigya.headway.core.designSystem.component.preview.FreudLottiePreviewTheme.text
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.previewPixelGrid
import headway.core.design_system.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

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
        val themes = listOf(false, true)

        val modes = listOf(
            FreudLottiePreviewCase(isDark = false, iterations = 1, reverseOnRepeat = false, speed = 1f),
            FreudLottiePreviewCase(isDark = false, iterations = Int.MAX_VALUE, reverseOnRepeat = false, speed = 1f),
            FreudLottiePreviewCase(isDark = false, iterations = Int.MAX_VALUE, reverseOnRepeat = true, speed = 1f),
        )

        for (isDark in themes) {
            for (m in modes) {
                yield(m.copy(isDark = isDark))
            }
        }
    }
}

@Preview(
    name = "FreudLottie – Theme x Mode",
    showBackground = false,
)
@Composable
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
        val header = "$themeLabel • $modeLabel • speed=${case.speed}"

        var finished by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(surface)
                .padding(ds.dimension.dp16.value),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                FreudText(
                    value = header,
                    color = text,
                    typography = ds.typography.labelSm,
                    align = TextAlign.Start,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                )

                Box(modifier = Modifier.height(ds.dimension.dp12.value))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(block)
                        .padding(ds.dimension.dp16.value),
                    contentAlignment = Alignment.Center,
                ) {
                    FreudLottie(
                        reader = { readPreviewLottieJson() },
                        iterations = case.iterations,
                        shouldBeReversedOnRepeat = case.reverseOnRepeat,
                        speed = case.speed,
                        isRestartable = true,
                        onFinish = { finished = true },
                        modifier = Modifier
                            .size(ds.dimension.dp128.value)
                            .previewPixelGrid(),
                    )
                }

                if (case.iterations == 1) {
                    Box(modifier = Modifier.height(ds.dimension.dp12.value))
                    FreudText(
                        value = if (finished) "onFinish() fired" else "playing…",
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

@OptIn(ExperimentalResourceApi::class)
private suspend fun readPreviewLottieJson(): ByteArray = Res.readBytes(LOTTIE_JSON_PATH)
private const val LOTTIE_JSON_PATH = "files/lottie_el_baion.json"
