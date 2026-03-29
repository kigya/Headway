package dev.kigya.headway

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

fun main() {
    ComposeViewport(document.body!!) {
        WebRoot()
    }
}

@Composable
private fun WebRoot() {
    WebEmojiFontFallbackEffect()
    App()
}
