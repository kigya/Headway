package dev.kigya.headway.database.api.routing

import dev.kigya.headway.database.api.port.CreateGoogleUserUseCaseContract
import dev.kigya.headway.database.api.port.CreateSessionUseCaseContract
import dev.kigya.headway.database.api.port.GetUserUseCaseContract
import dev.kigya.headway.database.api.port.InviteUserUseCaseContract
import dev.kigya.headway.database.api.port.UpsertGoogleUserUseCaseContract
import dev.kigya.headway.database.api.port.ValidateSessionUseCaseContract
import dev.kigya.headway.database.api.routing.session.sessionRouting
import dev.kigya.headway.database.api.routing.user.usersRouting
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.response.respondText
import io.ktor.http.HttpStatusCode

internal fun Route.databaseRouting(
    getUser: GetUserUseCaseContract,
    createUser: CreateGoogleUserUseCaseContract,
    inviteUser: InviteUserUseCaseContract,
    upsertGoogleUser: UpsertGoogleUserUseCaseContract,
    createSession: CreateSessionUseCaseContract,
    validateSession: ValidateSessionUseCaseContract,
) {
    get("/healthz") { call.respondText(status = HttpStatusCode.OK, text = "OK") }

    usersRouting(
        getUser = getUser,
        createUser = createUser,
        upsertGoogleUser = upsertGoogleUser,
        inviteUser = inviteUser,
    )
    sessionRouting(
        createSession = createSession,
        validateSession = validateSession,
    )
}
