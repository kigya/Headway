package dev.kigya.headway

import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

fun main() {
    ComposeViewport(document.body!!) {
        App()
    }
}
