package dev.kigya.headway.core.designSystem.component

import androidx.compose.runtime.Composable
import dev.jordond.connectivity.ConnectivityOptions
import dev.jordond.connectivity.compose.rememberConnectivityState

@Composable
internal actual fun rememberIsOnline(): Boolean {
    val connectivityState = rememberConnectivityState(
        ConnectivityOptions(autoStart = true),
    )
    return connectivityState.isConnected
}
