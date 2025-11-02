package dev.kigya.headway.navigation.api.navigator

import androidx.navigation3.runtime.NavKey

interface NavigatorContract {
    val backStack: List<NavKey>

    fun navigate(intent: NavigationIntent)
}
