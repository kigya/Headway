package dev.kigya.headway.navigation.api.navigator

import androidx.navigation3.runtime.NavKey

sealed interface NavigationIntent {
    data object NavigateBack : NavigationIntent

    data class NavigateTo(
        val screenNavigationKey: NavKey,
    ) : NavigationIntent

    data class ReplaceTopBy(
        val screenNavigationKey: NavKey,
        val asyncRunner: context(NavigatorScope) () -> AsyncRunner,
    ) : NavigationIntent
}
