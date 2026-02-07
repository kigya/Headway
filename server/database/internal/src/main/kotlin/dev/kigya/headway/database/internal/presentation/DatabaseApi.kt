package dev.kigya.headway.database.internal.presentation

import dev.kigya.headway.common.extension.handleDefaultExceptions
import dev.kigya.headway.common.extension.healthzRouting
import dev.kigya.headway.common.extension.defaultContentNegotiation
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
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

internal fun Application.installDatabaseApi(
    getUser: GetGoogleUserUseCase,
    createGoogleUser: CreateGoogleUserUseCase,
    upsertGoogleUser: UpsertGoogleUserUseCase,
    createSession: CreateSessionUseCase,
    validateSession: ValidateSessionUseCase,
    inviteUser: InviteUserUseCase,
) {
    defaultContentNegotiation()
    installDatabaseStatusPages()
    databaseRouting(
        getUser = getUser,
        createGoogleUser = createGoogleUser,
        upsertGoogleUser = upsertGoogleUser,
        inviteUser = inviteUser,
        createSession = createSession,
        validateSession = validateSession,
    )
}

private fun Application.installDatabaseStatusPages() {
    install(StatusPages, StatusPagesConfig::handleDefaultExceptions)
}

private fun Application.databaseRouting(
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
