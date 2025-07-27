package dev.kigya.headway.navigation.internal

import dev.kigya.headway.navigation.api.contract.ScreenRouteTypeKey
import dev.kigya.headway.navigation.api.contract.NavigatorContract
import dev.kigya.headway.navigation.api.intent.NavigationIntent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel

class HeadwayNavigator : NavigatorContract {
    override val navigationChannel = Channel<NavigationIntent>(
        capacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_LATEST,
    )

    private var _routeHistory: List<ScreenRouteTypeKey> = emptyList()
    override val routeHistory: List<ScreenRouteTypeKey> get() = _routeHistory

    override suspend fun navigateBack(
        route: ScreenRouteTypeKey?,
        inclusive: Boolean,
    ) {
        val newCurrent = popRouteHistory(route, inclusive)
        navigationChannel.send(
            NavigationIntent.NavigateBack(
                route = newCurrent,
                inclusive = inclusive,
            ),
        )
    }

    override suspend fun navigateTo(
        route: ScreenRouteTypeKey,
        popUpToRoute: ScreenRouteTypeKey?,
        inclusive: Boolean,
        isSingleTop: Boolean,
    ) {
        pushRouteHistory(route, popUpToRoute, inclusive, isSingleTop)
        navigationChannel.send(
            NavigationIntent.NavigateTo(
                route = route,
                popUpToRoute = popUpToRoute,
                inclusive = inclusive,
                isSingleTop = isSingleTop,
            ),
        )
    }

    private fun popRouteHistory(
        targetRoute: ScreenRouteTypeKey?,
        inclusive: Boolean,
    ): ScreenRouteTypeKey? {
        val oldHistory = _routeHistory

        val newHistory = if (targetRoute == null) {
            if (oldHistory.isNotEmpty()) oldHistory.dropLast(1) else oldHistory
        } else {
            val idx = oldHistory.indexOfLast { it == targetRoute }
            if (idx >= 0) {
                oldHistory.take(idx + if (inclusive) 0 else 1)
            } else {
                emptyList()
            }
        }

        _routeHistory = newHistory
        return newHistory.lastOrNull()
    }

    private fun pushRouteHistory(
        newRoute: ScreenRouteTypeKey,
        popUpToRoute: ScreenRouteTypeKey?,
        inclusive: Boolean,
        isSingleTop: Boolean,
    ) {
        var newHistory = _routeHistory

        if (isSingleTop && newHistory.lastOrNull() == newRoute) {
            return
        }

        if (popUpToRoute != null) {
            val idx = newHistory.indexOfLast { it == popUpToRoute }
            newHistory = if (idx >= 0) {
                newHistory.take(idx + if (inclusive) 0 else 1)
            } else {
                emptyList()
            }
        }

        newHistory = newHistory + newRoute
        _routeHistory = newHistory
    }
}
