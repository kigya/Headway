package dev.kigya.headway.database.internal.presentation.plugin

import dev.kigya.headway.common.extension.healthzRouting
import dev.kigya.headway.database.api.url.databaseServiceUrlHolder
import dev.kigya.headway.database.internal.presentation.DatabaseApplicationUseCases
import dev.kigya.headway.database.internal.presentation.routing.preparationRouting
import dev.kigya.headway.database.internal.presentation.routing.sessionRouting
import dev.kigya.headway.database.internal.presentation.routing.usersRouting
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

internal fun Application.databaseRouting(useCases: DatabaseApplicationUseCases) {
    routing {
        route(databaseServiceUrlHolder.baseUrl) {
            healthzRouting()

            usersRouting(
                getGoogleUser = useCases.getUser,
                createGoogleUser = useCases.createGoogleUser,
                upsertGoogleUser = useCases.upsertGoogleUser,
                inviteUser = useCases.inviteUser,
            )

            sessionRouting(
                createSession = useCases.createSession,
                validateSession = useCases.validateSession,
            )

            preparationRouting(preparation = useCases.preparation)
        }
    }
}
