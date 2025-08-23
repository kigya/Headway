package dev.kigya.headway.navigation.api.contract

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

interface ScreenRouteHolderContract {
    @Serializable
    val key: ScreenRouteTypeKey
    val content: @Composable () -> Unit
}
