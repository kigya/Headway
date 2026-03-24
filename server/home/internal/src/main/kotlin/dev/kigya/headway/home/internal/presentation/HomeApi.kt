package dev.kigya.headway.home.internal.presentation

import dev.kigya.headway.common.extension.defaultContentNegotiation
import dev.kigya.headway.common.extension.defaultResources
import dev.kigya.headway.home.internal.domain.usecase.GetHomeScreenUseCase
import dev.kigya.headway.home.internal.presentation.plugin.homeRouting
import dev.kigya.headway.home.internal.presentation.plugin.homeStatusPages
import io.ktor.server.application.Application

internal fun Application.installHomeApi(getHomeScreen: GetHomeScreenUseCase) {
    defaultContentNegotiation()
    defaultResources()
    homeStatusPages()
    homeRouting(getHomeScreen = getHomeScreen)
}
