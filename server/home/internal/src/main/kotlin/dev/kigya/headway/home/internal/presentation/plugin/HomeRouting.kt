package dev.kigya.headway.home.internal.presentation.plugin

import dev.kigya.headway.common.extension.healthzRouting
import dev.kigya.headway.home.api.model.`in`.HomeScreenRequestDto
import dev.kigya.headway.home.api.model.resource.HomeResource
import dev.kigya.headway.home.api.url.homeServiceUrlHolder
import dev.kigya.headway.home.internal.domain.usecase.GetHomeScreenUseCase
import io.ktor.server.application.Application
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

internal fun Application.homeRouting(getHomeScreen: GetHomeScreenUseCase) {
    routing {
        route(homeServiceUrlHolder.baseUrl) {
            healthzRouting()
            post<HomeResource.Screen> {
                val body = call.receive<HomeScreenRequestDto>()
                val trimmedName = body.userName.trim()
                if (trimmedName.isBlank()) {
                    throw BadRequestException("User name is blank")
                }

                call.respond(getHomeScreen(body))
            }
        }
    }
}
