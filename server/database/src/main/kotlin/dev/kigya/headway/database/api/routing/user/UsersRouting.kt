package dev.kigya.headway.database.api.routing.user

import dev.kigya.headway.database.api.error.BadRequestApiException
import dev.kigya.headway.database.api.port.CreateGoogleUserUseCaseContract
import dev.kigya.headway.database.api.port.GetUserUseCaseContract
import dev.kigya.headway.database.api.port.InviteUserUseCaseContract
import dev.kigya.headway.database.api.port.UpsertGoogleUserUseCaseContract
import dev.kigya.headway.database.api.routing.user.request.CreateUserRequestDto
import dev.kigya.headway.database.api.routing.user.request.InviteUserRequestDto
import dev.kigya.headway.database.api.routing.user.request.UpsertGoogleUserRequestDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import java.util.UUID

private const val KEY_USER_ID = "user_id"
private const val KEY_USER_GOOGLE_ID = "google_id"

internal fun Route.usersRouting(
    getUser: GetUserUseCaseContract,
    createUser: CreateGoogleUserUseCaseContract,
    upsertGoogleUser: UpsertGoogleUserUseCaseContract,
    inviteUser: InviteUserUseCaseContract,
) {
    getUser(getUser)
    createUser(createUser)
    upsertGoogleUser(upsertGoogleUser)
    inviteUser(inviteUser)
}

private fun Route.getUser(getUser: GetUserUseCaseContract) {
    get("/user") {
        val googleId = call.request.queryParameters[KEY_USER_GOOGLE_ID]?.trim()?.takeIf { it.isNotBlank() }
        val userIdRaw = call.request.queryParameters[KEY_USER_ID]?.trim()?.takeIf { it.isNotBlank() }
        if (googleId == null && userIdRaw == null) throw BadRequestApiException()

        val userId = userIdRaw?.let {
            runCatching { UUID.fromString(it) }.getOrElse { throw BadRequestApiException() }
        }

        val user = getUser(googleId = googleId, userId = userId)
        if (user == null) call.respond(HttpStatusCode.NotFound) else call.respond(HttpStatusCode.OK, user)
    }
}

private fun Route.createUser(createUser: CreateGoogleUserUseCaseContract) {
    post("/user") {
        val request = runCatching { call.receive<CreateUserRequestDto>() }
            .getOrElse { throw BadRequestApiException() }

        val newUser = createUser(
            googleId = request.googleId.trim(),
            email = request.email.trim(),
            name = request.name.trim(),
            avatarUrl = request.avatarUrl?.trim(),
            role = request.role,
        )

        call.respond(HttpStatusCode.Created, newUser)
    }
}

private fun Route.upsertGoogleUser(upsertGoogleUser: UpsertGoogleUserUseCaseContract) {
    post("/user/google/upsert") {
        val request = runCatching { call.receive<UpsertGoogleUserRequestDto>() }
            .getOrElse { throw BadRequestApiException() }

        val user = upsertGoogleUser(
            googleId = request.googleId.trim(),
            email = request.email.trim(),
            name = request.name.trim(),
            avatarUrl = request.avatarUrl?.trim(),
        )

        call.respond(HttpStatusCode.OK, user)
    }
}

private fun Route.inviteUser(inviteUser: InviteUserUseCaseContract) {
    post("/user/invite") {
        val request = runCatching { call.receive<InviteUserRequestDto>() }
            .getOrElse { throw BadRequestApiException() }

        val email = request.email.trim()
        val department = request.department.trim()
        if (email.isBlank() || department.isBlank()) throw BadRequestApiException()

        val user = inviteUser(email = email, department = department)
        call.respond(HttpStatusCode.Created, user)
    }
}
