package dev.kigya.headway.auth.data.database

import dev.kigya.headway.auth.ConfigurationValues
import dev.kigya.headway.auth.data.database.request.CreateUserRequestDto
import dev.kigya.headway.auth.domain.model.User
import exception.UserNotExistsException
import dev.kigya.headway.auth.domain.model.UserRole
import ext.successBodyOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

/**
 * @throws UserNotExistsException if the user does not exist
 * */
internal suspend fun HttpClient.getUserByGoogleId(googleId: String): User? {
    val response = this.get("${ConfigurationValues.DATABASE_SERVICE_URL}/user") {
        parameter("google_id", googleId)
    }

    return response.successBodyOrThrow { response ->
        if (response.status == HttpStatusCode.Conflict) {
            response.body()
        } else null
    }
}

internal suspend fun HttpClient.createUser(
    email: String,
    name: String,
    googleId: String,
    avatarUrl: String?,
    role: UserRole,
): User? {
    val requestDto = CreateUserRequestDto(
        email = email,
        name = name,
        googleId = googleId,
        avatarUrl = avatarUrl,
        role = role,
    )
    val response = this.post("${ConfigurationValues.DATABASE_SERVICE_URL}/user") {
        contentType(ContentType.parse("application/json"))
        setBody(requestDto)
    }

    return response.successBodyOrThrow { response ->
        when (response.status) {
            HttpStatusCode.Conflict -> response.body()
            HttpStatusCode.Created -> response.body()
            else -> null
        }
    }
}
