package dev.kigya.headway.core.designSystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import org.w3c.dom.events.Event

@Suppress("EffectKeys")
@Composable
internal actual fun rememberIsOnline(): Boolean {
    var isOnline by remember { mutableStateOf(window.navigator.onLine) }

    DisposableEffect(Unit) {
        val onlineListener = { _: Event -> isOnline = true }
        val offlineListener = { _: Event -> isOnline = false }

        window.addEventListener("online", onlineListener)
        window.addEventListener("offline", offlineListener)

        onDispose {
            window.removeEventListener("online", onlineListener)
            window.removeEventListener("offline", offlineListener)
        }
    }

    return isOnline
}
