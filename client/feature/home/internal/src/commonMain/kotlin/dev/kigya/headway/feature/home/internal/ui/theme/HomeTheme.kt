package dev.kigya.headway.feature.home.internal.ui.theme

import androidx.compose.runtime.Composable
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides

internal object HomeTheme : FreudTheme() {
    val FreudColorScheme.homeBackground
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown100,
        )

    val FreudColorScheme.homePrimaryText
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown90,
            dark = super.color.brown40,
        )

    val FreudColorScheme.homeActionContainer
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown80,
            dark = super.color.brown40,
        )

    val FreudColorScheme.homeActionContent
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown100,
        )

    val FreudColorScheme.homeGridCellSurface
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.gray20,
            dark = super.color.brown80,
        )

    val FreudColorScheme.homeGridCellBorder
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown40,
            dark = super.color.brown50,
        )
}
