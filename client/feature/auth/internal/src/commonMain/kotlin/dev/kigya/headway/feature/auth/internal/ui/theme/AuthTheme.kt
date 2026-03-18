package dev.kigya.headway.feature.auth.internal.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides

internal object AuthTheme : FreudTheme() {
    val FreudColorScheme.authBackground
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown100,
        )

    val FreudColorScheme.brandTextColor
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown30,
        )

    val FreudColorScheme.greetingHighlight @Composable get() = this provides FreudDynamicColor(
        light = super.color.brown60,
        dark = super.color.green50,
    )

    val FreudColorScheme.greetingTextColor @Composable get() = this provides FreudDynamicColor(
        light = super.color.brown80,
        dark = super.color.brown40,
    )

    val FreudColorScheme.greetingSubTextColor @Composable get() = this provides FreudDynamicColor(
        light = super.color.brown100,
        dark = super.color.brown30,
    )

    val FreudColorScheme.learnAsGuestButtonColor @Composable get() = this provides FreudDynamicColor(
        light = super.color.green60,
        dark = super.color.green60,
    )

    val FreudColorScheme.googleSingInButtonColor @Composable get() = this provides FreudDynamicColor(
        light = super.color.brown80,
        dark = super.color.brown80,
    )

    val FreudColorScheme.googleSingInButtonTextColor @Composable get() = this provides FreudDynamicColor(
        light = super.color.brown10,
        dark = super.color.brown10,
    )

    val FreudColorScheme.authUnderButtonTextColor @Composable get() = this provides FreudDynamicColor(
        light = super.color.orange40,
        dark = super.color.orange40,
    )

    val FreudColorScheme.transparent @Composable get() = this provides FreudDynamicColor(
        light = FreudDsToken(Color.Transparent),
        dark = FreudDsToken(Color.Transparent),
    )
}
