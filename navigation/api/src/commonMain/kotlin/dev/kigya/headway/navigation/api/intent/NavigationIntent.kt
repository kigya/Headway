package dev.kigya.headway.navigation.api.intent

import dev.kigya.headway.navigation.api.contract.ScreenRouteTypeKey

sealed class NavigationIntent(
    open val route: ScreenRouteTypeKey?,
    open val inclusive: Boolean,
) {
    data class NavigateBack(
        override val route: ScreenRouteTypeKey? = null,
        override val inclusive: Boolean = false,
    ) : NavigationIntent(route, inclusive)

    data class NavigateTo(
        override val route: ScreenRouteTypeKey,
        override val inclusive: Boolean = false,
        val popUpToRoute: ScreenRouteTypeKey? = null,
        val isSingleTop: Boolean = false,
    ) : NavigationIntent(route, inclusive)
}
