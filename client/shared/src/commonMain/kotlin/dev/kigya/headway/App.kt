package dev.kigya.headway

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.feature.auth.api.AuthNoAccessScreenKey
import dev.kigya.headway.feature.auth.api.AuthNoAccessScreenRouteHolderContract
import dev.kigya.headway.feature.auth.api.AuthScreenKey
import dev.kigya.headway.feature.auth.api.AuthScreenRouteHolderContract
import dev.kigya.headway.feature.home.api.HomeScreenKey
import dev.kigya.headway.feature.home.api.HomeScreenRouteHolderContract
import dev.kigya.headway.feature.learnQuestions.api.LearnQuestionsScreenKey
import dev.kigya.headway.feature.learnQuestions.api.LearnQuestionsScreenRouteHolderContract
import dev.kigya.headway.feature.splash.api.SplashScreenKey
import dev.kigya.headway.feature.splash.api.SplashScreenRouteHolderContract
import dev.kigya.headway.navigation.api.navigator.NavigationIntent
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import org.koin.compose.currentKoinScope
import org.koin.compose.scope.rememberKoinScope

@Composable
fun App() {
    HeadwayKoinHost {
        FreudTheme {
            AppNavigationHost()
        }
    }
}

@Composable
expect fun HeadwayKoinHost(content: @Composable () -> Unit)

@Composable
private fun AppNavigationHost() {
    val koinScope = rememberKoinScope(currentKoinScope())

    val navigator = koinScope.get<NavigatorContract>()
    val splashRoute = koinScope.get<SplashScreenRouteHolderContract>()
    val authRoute = koinScope.get<AuthScreenRouteHolderContract>()
    val authNoAccessRoute = koinScope.get<AuthNoAccessScreenRouteHolderContract>()
    val homeRoute = koinScope.get<HomeScreenRouteHolderContract>()
    val learnQuestionsRoute = koinScope.get<LearnQuestionsScreenRouteHolderContract>()

    NavDisplay(
        backStack = navigator.backStack,
        onBack = { navigator.navigate(NavigationIntent.NavigateBack) },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<SplashScreenKey> { splashRoute.content() }
            entry<AuthScreenKey> { authRoute.content() }
            entry<AuthNoAccessScreenKey> { authNoAccessRoute.content() }
            entry<HomeScreenKey> { homeRoute.content() }
            entry<LearnQuestionsScreenKey> { learnQuestionsRoute.content() }
        },
    )
}
