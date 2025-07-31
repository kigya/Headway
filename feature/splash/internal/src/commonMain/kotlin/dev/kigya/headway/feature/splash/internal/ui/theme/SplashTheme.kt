package dev.kigya.headway.feature.splash.internal.ui.theme

import androidx.compose.runtime.Composable
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides

internal object SplashTheme : FreudTheme() {
    val FreudColorScheme.splashBackground
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown100,
        )

    val FreudColorScheme.brandTextColor
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown80,
            dark = super.color.brown60,
        )
}
