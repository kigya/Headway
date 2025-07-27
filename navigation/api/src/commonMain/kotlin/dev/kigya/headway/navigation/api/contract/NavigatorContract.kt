package dev.kigya.headway.navigation.api.contract

import dev.kigya.headway.navigation.api.intent.NavigationIntent
import kotlinx.coroutines.channels.Channel

interface NavigatorContract {
    val navigationChannel: Channel<NavigationIntent>
    val routeHistory: List<ScreenRouteTypeKey>

    suspend fun navigateBack(
        route: ScreenRouteTypeKey? = null,
        inclusive: Boolean = false,
    )

    suspend fun navigateTo(
        route: ScreenRouteTypeKey,
        popUpToRoute: ScreenRouteTypeKey? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false,
    )
}
