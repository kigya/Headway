package dev.kigya.headway.database.internal.presentation.routing

import dev.kigya.headway.database.api.model.`in`.DatabaseCreateUserPayloadDto
import dev.kigya.headway.database.api.model.`in`.DatabaseInviteUserPayloadDto
import dev.kigya.headway.database.api.model.`in`.DatabaseUpsertGoogleUserPayloadDto
import dev.kigya.headway.database.api.model.resource.DatabaseUsersResource
import dev.kigya.headway.database.internal.domain.usecase.CreateGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.GetGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.InviteUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.UpsertGoogleUserUseCase
import dev.kigya.headway.database.internal.error.BadRequestApiException
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

internal fun Route.usersRouting(
    getGoogleUser: GetGoogleUserUseCase,
    createGoogleUser: CreateGoogleUserUseCase,
    upsertGoogleUser: UpsertGoogleUserUseCase,
    inviteUser: InviteUserUseCase,
) {
    getUser(getGoogleUser)
    createUser(createGoogleUser)
    upsertGoogleUser(upsertGoogleUser)
    inviteUser(inviteUser)
}

private fun Route.getUser(getGoogleUser: GetGoogleUserUseCase) {
    get<DatabaseUsersResource> { params ->
        val googleId = params.googleId?.trim()?.takeIf(String::isNotBlank)
        val userId = params.userId
        if (googleId == null) throw

        val user = getGoogleUser(googleId = googleId, userId = userId)
        if (user == null) call.respond(HttpStatusCode.NotFound) else call.respond(HttpStatusCode.OK, user)
    }
}

private fun Route.createUser(createGoogleUser: CreateGoogleUserUseCase) {
    post<DatabaseUsersResource> {
        val request = runCatching { call.receive<DatabaseCreateUserPayloadDto>() }
            .getOrElse { throw BadRequestApiException() }

        val newUser = createGoogleUser(
            googleId = request.googleId.trim(),
            email = request.email.trim(),
            name = request.name.trim(),
            avatarUrl = request.avatarUrl?.trim(),
            role = request.role,
        )

        call.respond(HttpStatusCode.Created, newUser)
    }
}

private fun Route.upsertGoogleUser(upsertGoogleUser: UpsertGoogleUserUseCase) {
    post<DatabaseUsersResource.Google.Upsert> {
        val request = runCatching { call.receive<DatabaseUpsertGoogleUserPayloadDto>() }
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

private fun Route.inviteUser(inviteUser: InviteUserUseCase) {
    post<DatabaseUsersResource.Invite> {
        val request = runCatching { call.receive<DatabaseInviteUserPayloadDto>() }
            .getOrElse { throw BadRequestApiException() }

        val email = request.email.trim()
        val department = request.department.trim()
        if (email.isBlank() || department.isBlank()) throw BadRequestApiException()

        val user = inviteUser(email = email, department = department)
        call.respond(HttpStatusCode.Created, user)
    }
}
