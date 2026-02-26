package dev.kigya.headway.navigation.api.route

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

interface ScreenRouteHolderContract {
    @Serializable
    val screenNavigationKey: NavKey
    val content: @Composable () -> Unit
}
