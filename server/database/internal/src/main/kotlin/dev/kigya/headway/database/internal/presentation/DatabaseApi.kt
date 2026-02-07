package dev.kigya.headway.database.internal.presentation

import dev.kigya.headway.common.extension.defaultContentNegotiation
import dev.kigya.headway.database.internal.domain.usecase.CreateGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.CreateSessionUseCase
import dev.kigya.headway.database.internal.domain.usecase.GetGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.InviteUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.UpsertGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.ValidateSessionUseCase
import dev.kigya.headway.database.internal.presentation.plugin.databaseRouting
import dev.kigya.headway.database.internal.presentation.plugin.databaseStatusPages
import io.ktor.server.application.Application

internal fun Application.installDatabaseApi(
    getUser: GetGoogleUserUseCase,
    createGoogleUser: CreateGoogleUserUseCase,
    upsertGoogleUser: UpsertGoogleUserUseCase,
    createSession: CreateSessionUseCase,
    validateSession: ValidateSessionUseCase,
    inviteUser: InviteUserUseCase,
) {
    defaultContentNegotiation()
    databaseStatusPages()
    databaseRouting(
        getUser = getUser,
        createGoogleUser = createGoogleUser,
        upsertGoogleUser = upsertGoogleUser,
        inviteUser = inviteUser,
        createSession = createSession,
        validateSession = validateSession,
    )
}
