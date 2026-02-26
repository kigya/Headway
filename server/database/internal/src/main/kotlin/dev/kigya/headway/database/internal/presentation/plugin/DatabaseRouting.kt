package dev.kigya.headway.database.internal.presentation.plugin

import dev.kigya.headway.common.extension.healthzRouting
import dev.kigya.headway.database.api.url.databaseServiceUrlHolder
import dev.kigya.headway.database.internal.domain.usecase.CreateGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.CreateSessionUseCase
import dev.kigya.headway.database.internal.domain.usecase.GetGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.InviteUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.UpsertGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.ValidateSessionUseCase
import dev.kigya.headway.database.internal.presentation.routing.sessionRouting
import dev.kigya.headway.database.internal.presentation.routing.usersRouting
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

internal fun Application.databaseRouting(
    getUser: GetGoogleUserUseCase,
    createGoogleUser: CreateGoogleUserUseCase,
    upsertGoogleUser: UpsertGoogleUserUseCase,
    inviteUser: InviteUserUseCase,
    createSession: CreateSessionUseCase,
    validateSession: ValidateSessionUseCase,
) {
    routing {
        route(databaseServiceUrlHolder.baseUrl) {
            healthzRouting()

            usersRouting(
                getGoogleUser = getUser,
                createGoogleUser = createGoogleUser,
                upsertGoogleUser = upsertGoogleUser,
                inviteUser = inviteUser,
            )

            sessionRouting(
                createSession = createSession,
                validateSession = validateSession,
            )
        }
    }
}
