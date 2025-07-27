package dev.kigya.headway

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.kigya.headway.di.api.appModules
import dev.kigya.headway.feature.splash.api.SplashScreenRouteHolderContract
import dev.kigya.headway.navigation.api.extension.animatedComposable
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.currentKoinScope
import org.koin.compose.scope.rememberKoinScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App() {
    KoinMultiplatformApplication(
        config = KoinConfiguration { modules(appModules) }) {
        AppNavigationHost()
    }
}

@OptIn(KoinExperimentalAPI::class)
@Composable
private fun AppNavigationHost() {
    val koinScope = rememberKoinScope(currentKoinScope())

    val splashRoute = koinScope.get<SplashScreenRouteHolderContract>()

    NavHost(
        navController = rememberNavController(),
        startDestination = splashRoute.screenRouteTypeKey,
    ) {
        animatedComposable(splashRoute) { content() }
    }
}
