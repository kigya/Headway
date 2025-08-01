package dev.kigya.headway.core.designSystem.util

import androidx.compose.runtime.Composable

enum class SystemBarsColor {
    LIGHT,
    DARK,
    AUTO,
}

@Composable
expect fun SystemBarsColor(color: SystemBarsColor)
