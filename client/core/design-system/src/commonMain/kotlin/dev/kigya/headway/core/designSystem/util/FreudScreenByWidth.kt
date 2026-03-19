package dev.kigya.headway.core.designSystem.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FreudScreenByWidth(
    modifier: Modifier = Modifier,
    narrow: @Composable BoxScope.() -> Unit,
    wide: @Composable BoxScope.() -> Unit,
    overlay: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        if (isWide()) {
            wide()
        } else {
            narrow()
        }
        overlay()
    }
}
