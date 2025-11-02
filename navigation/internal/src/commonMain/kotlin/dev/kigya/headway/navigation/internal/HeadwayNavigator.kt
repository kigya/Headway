package dev.kigya.headway.navigation.internal

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey
import dev.kigya.headway.navigation.api.navigator.NavigationIntent
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import dev.kigya.headway.navigation.api.navigator.NavigatorScope
import dev.kigya.headway.navigation.api.navigator.StartKeyProvider
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class HeadwayNavigator(
    startProvider: StartKeyProvider,
) : NavigatorContract, NavigatorScope {

    private val _backStack = mutableStateListOf<NavKey>().apply {
        add(startProvider.start())
    }
    override val backStack: List<NavKey> get() = _backStack

    override fun navigate(intent: NavigationIntent) {
        when (intent) {
            is NavigationIntent.NavigateBack -> _backStack.removeLastOrNull()

            is NavigationIntent.NavigateTo -> {
                val key = intent.screenNavigationKey
                if (backStack.lastOrNull() != key) _backStack.add(key)
            }

            is NavigationIntent.ReplaceTopBy -> {
                with(this as NavigatorScope) {
                    val runner = intent.asyncRunner()
                    runner {
                        if (this@HeadwayNavigator._backStack.lastOrNull() != intent.asyncRunner) {
                            this@HeadwayNavigator._backStack.add(intent.screenNavigationKey)
                        }
                        delay(REPLACE_TOP_DELAY)
                        if (this@HeadwayNavigator._backStack.size > 1) {
                            this@HeadwayNavigator._backStack.removeRange(
                                fromIndex = 0,
                                toIndex = this@HeadwayNavigator._backStack.lastIndex,
                            )
                        }
                    }
                }
            }
        }
    }

    private companion object {
        val REPLACE_TOP_DELAY = 500.milliseconds
    }
}
