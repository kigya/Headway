package dev.kigya.headway

import androidx.compose.runtime.Composable

@Composable
actual fun HeadwayKoinHost(content: @Composable () -> Unit) {
    content()
}
