package dev.kigya.headway.feature.auth.internal.ui.theme

import androidx.compose.runtime.Composable
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides

internal object AuthTheme : FreudTheme() {
    val FreudColorScheme.authBackground
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.green40,
            dark = super.color.green90,
        )

    val FreudColorScheme.brandTextColor
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown30,
        )
}
