package dev.kigya.headway.feature.auth.internal.ui.theme.access

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides

internal object AuthNoAccessTheme : FreudTheme() {
    val FreudColorScheme.screenBackgroundNarrow: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown100,
        )

    val FreudColorScheme.screenBackgroundWide: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown100,
        )

    val FreudColorScheme.cardBackgroundNarrow: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.orange30,
            dark = super.color.brown100,
        )

    val FreudColorScheme.cardBackgroundWide: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown90,
        )

    val FreudColorScheme.arcOverlay: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.white,
            dark = super.color.brown90,
        )

    val FreudColorScheme.title: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown80,
            dark = super.color.white,
        )

    val FreudColorScheme.subtitle: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray70,
            dark = super.color.brown30,
        )

    val FreudColorScheme.buttonContainer: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown80,
            dark = super.color.brown80,
        )

    val FreudColorScheme.buttonContent: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown10,
        )

    val FreudColorScheme.reportIconTint: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.white,
            dark = super.color.white,
        )
}
