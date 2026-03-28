package dev.kigya.headway.database.internal.presentation.routing

import dev.kigya.headway.database.api.model.`in`.DatabaseGuestSessionRegisterRequestDto
import dev.kigya.headway.database.api.model.resource.DatabaseResource
import dev.kigya.headway.database.internal.data.repository.GuestSessionsRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

internal fun Route.guestSessionsRouting(guestSessions: GuestSessionsRepository) {
    post<DatabaseResource.GuestSessions.Register> {
        val body = call.receive<DatabaseGuestSessionRegisterRequestDto>()
        guestSessions.register(sessionId = body.id)
        call.respond(HttpStatusCode.Created)
    }

    post<DatabaseResource.GuestSessions.Revoke> { params ->
        guestSessions.revoke(sessionId = params.sessionId)
        call.respond(HttpStatusCode.NoContent)
    }

    get<DatabaseResource.GuestSessions.Validate> { params ->
        guestSessions.ensureActive(sessionId = params.sessionId)
        call.respond(HttpStatusCode.OK)
    }
}
