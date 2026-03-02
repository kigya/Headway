package dev.kigya.headway.database.internal.presentation.routing

import dev.kigya.headway.database.api.model.`in`.DatabaseCreateUserPayloadDto
import dev.kigya.headway.database.api.model.`in`.DatabaseInviteUserPayloadDto
import dev.kigya.headway.database.api.model.`in`.DatabaseUpsertGoogleUserPayloadDto
import dev.kigya.headway.database.api.model.resource.DatabaseResource
import dev.kigya.headway.database.internal.domain.usecase.CreateGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.GetGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.InviteUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.UpsertGoogleUserUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

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
    get<DatabaseResource.User.Query> { params ->
        val googleId = params.googleId?.trim()?.takeIf(String::isNotBlank)
        val userId = params.userId
        if (googleId == null && userId == null) throw BadRequestException("googleId or userId must be provided")

        val user = getGoogleUser(googleId = googleId, userId = userId)
        if (user == null) call.respond(HttpStatusCode.NotFound) else call.respond(HttpStatusCode.OK, user)
    }
}

private fun Route.createUser(createGoogleUser: CreateGoogleUserUseCase) {
    post<DatabaseResource.User.Google> {
        val body = call.receive<DatabaseCreateUserPayloadDto>()

        val googleId = body.googleId.trim()
        val email = body.email.trim()
        val name = body.name.trim()
        if (googleId.isBlank()) throw BadRequestException("GoogleId is blank")
        if (email.isBlank()) throw BadRequestException("Email is blank")
        if (name.isBlank()) throw BadRequestException("Name is blank")

        val newUser = createGoogleUser(
            googleId = googleId,
            email = email,
            name = name,
            avatarUrl = body.avatarUrl?.trim(),
            role = body.role,
        )

        call.respond(HttpStatusCode.Created, newUser)
    }
}

private fun Route.upsertGoogleUser(upsertGoogleUser: UpsertGoogleUserUseCase) {
    post<DatabaseResource.User.Google.Upsert> {
        val body = call.receive<DatabaseUpsertGoogleUserPayloadDto>()

        val googleId = body.googleId.trim()
        val email = body.email.trim()
        val name = body.name.trim()
        val avatarUrl = body.avatarUrl?.trim()
        if (googleId.isBlank()) throw BadRequestException("GoogleId is blank")
        if (email.isBlank()) throw BadRequestException("Email is blank")
        if (name.isBlank()) throw BadRequestException("Name is blank")

        val user = upsertGoogleUser(
            googleId = googleId,
            email = email,
            name = name,
            avatarUrl = avatarUrl,
        )

        call.respond(HttpStatusCode.OK, user)
    }
}

private fun Route.inviteUser(inviteUser: InviteUserUseCase) {
    post<DatabaseResource.User.Invite> {
        val request = call.receive<DatabaseInviteUserPayloadDto>()

        val email = request.email.trim()
        val department = request.department.trim()
        if (email.isBlank()) throw BadRequestException("Email is blank")
        if (department.isBlank()) throw BadRequestException("Department is blank")

        val user = inviteUser(email = email, department = department)
        call.respond(HttpStatusCode.Created, user)
    }
}
