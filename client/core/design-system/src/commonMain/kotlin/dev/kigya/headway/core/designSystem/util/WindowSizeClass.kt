package dev.kigya.headway.core.designSystem.util

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize
import androidx.compose.material3.windowsizeclass.WindowSizeClass as ComposeWindowSizeClass

@Composable
fun isWide(): Boolean = rememberWindowSizeClass().widthSizeClass >= WindowWidthSizeClass.Medium

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberWindowSizeClass(): ComposeWindowSizeClass {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current

    val widthDp = with(density) { windowInfo.containerSize.width.toDp() }
    val heightDp = with(density) { windowInfo.containerSize.height.toDp() }

    return remember(widthDp, heightDp) {
        ComposeWindowSizeClass.calculateFromSize(
            size = DpSize(
                width = widthDp,
                height = heightDp,
            ),
        )
    }
}
