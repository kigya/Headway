package dev.kigya.headway.core.designSystem.component.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.kigya.headway.core.designSystem.component.FreudAnimatedText
import dev.kigya.headway.core.designSystem.component.FreudSpacer
import dev.kigya.headway.core.designSystem.component.preview.FreudAnimatedTextPreviewTheme.accentText
import dev.kigya.headway.core.designSystem.component.preview.FreudAnimatedTextPreviewTheme.background
import dev.kigya.headway.core.designSystem.component.preview.FreudAnimatedTextPreviewTheme.primaryText
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudAnimationTrigger
import dev.kigya.headway.core.designSystem.util.FreudTextAnimation
import dev.kigya.headway.core.designSystem.util.FreudTextValue

private object FreudAnimatedTextPreviewTheme : FreudTheme() {
    val FreudColorScheme.primaryText: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown30,
            dark = super.color.orange10,
        )

    val FreudColorScheme.accentText: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.orange40,
            dark = super.color.orange20,
        )

    val FreudColorScheme.background: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray10,
            dark = super.color.brown90,
        )
}

@Preview(
    name = "FreudAnimatedText – Animation Variants",
    showBackground = true,
)
@Composable
private fun FreudAnimatedTextVariantsPreview() {
    FreudTheme(isDark = false) {
        val ds = FreudTheme.DefaultFreudTheme
        val textColor = FreudAnimatedTextPreviewTheme.colorScheme.primaryText

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(FreudAnimatedTextPreviewTheme.colorScheme.background.value)
                .padding(ds.dimension.dp16.value),
        ) {
            FreudAnimatedText(
                value = FreudTextValue.text(
                    value = "Typewriter animation effect",
                    animation = FreudTextAnimation.Typewriter(durationMs = 1500),
                ),
                color = textColor,
                typography = ds.typography.textLgBold,
                align = TextAlign.Start,
            )

            FreudSpacer(size = ds.dimension.dp20)

            FreudAnimatedText(
                value = FreudTextValue.text(
                    value = "Fade In animation effect",
                    animation = FreudTextAnimation.FadeIn(durationMs = 1000),
                ),
                color = textColor,
                typography = ds.typography.textLgBold,
                align = TextAlign.Start,
            )

            FreudSpacer(size = ds.dimension.dp20)

            FreudAnimatedText(
                value = FreudTextValue.text(
                    value = "Slide In animation effect",
                    animation = FreudTextAnimation.SlideIn(durationMs = 800),
                ),
                color = textColor,
                typography = ds.typography.textLgBold,
                align = TextAlign.Start,
            )
        }
    }
}

@Preview(
    name = "FreudAnimatedText – Chained Animation",
    showBackground = true,
)
@Composable
private fun FreudAnimatedTextChainPreview() {
    FreudTheme(isDark = false) {
        val ds = FreudTheme.DefaultFreudTheme
        val textColor = FreudAnimatedTextPreviewTheme.colorScheme.primaryText
        val accentColor = FreudAnimatedTextPreviewTheme.colorScheme.accentText

        val firstFinished = remember { FreudAnimationTrigger() }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(FreudAnimatedTextPreviewTheme.colorScheme.background.value)
                .padding(ds.dimension.dp16.value),
        ) {
            FreudAnimatedText(
                value = FreudTextValue.rich {
                    append("First segment typing...")
                    colored(" Done!", color = accentColor)
                    animate(
                        animation = FreudTextAnimation.Typewriter(durationMs = 1200),
                        onFinish = firstFinished,
                    )
                },
                color = textColor,
                typography = ds.typography.headingSmExtraBold,
                align = TextAlign.Start,
            )

            FreudSpacer(size = ds.dimension.dp12)

            FreudAnimatedText(
                value = FreudTextValue.text(
                    value = "I wait for the first one to finish before sliding in.",
                    animation = FreudTextAnimation.SlideIn(startTrigger = firstFinished),
                ),
                color = textColor,
                typography = ds.typography.textMdSemiBold,
                align = TextAlign.Start,
            )
        }
    }
}
