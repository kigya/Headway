package dev.kigya.headway.core.designSystem.component

import androidx.compose.runtime.Composable
import dev.jordond.connectivity.ConnectivityOptions
import dev.jordond.connectivity.HttpConnectivityOptions
import dev.jordond.connectivity.compose.rememberConnectivityState

@Composable
internal actual fun rememberIsOnline(): Boolean {
    val connectivityState = rememberConnectivityState(
        options = HttpConnectivityOptions(
            options = ConnectivityOptions(autoStart = true),
            pollingIntervalMs = POLLING_INTERVAL_MS,
            timeoutMs = TIMEOUT_MS,
            urls = listOf("cloudflare.com"),
        ),
    )
    return connectivityState.isConnected
}

private const val POLLING_INTERVAL_MS = 500L
private const val TIMEOUT_MS = 700L
