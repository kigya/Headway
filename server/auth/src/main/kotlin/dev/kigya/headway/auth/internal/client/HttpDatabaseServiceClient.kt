package dev.kigya.headway.auth.internal.client

import dev.kigya.headway.auth.api.model.User
import dev.kigya.headway.auth.internal.client.dto.CreateSessionRequestDto
import dev.kigya.headway.auth.internal.client.dto.UpsertGoogleUserRequestDto
import dev.kigya.headway.auth.internal.client.dto.ValidateSessionRequestDto
import dev.kigya.headway.auth.internal.config.ConfigurationValues
import ext.successBodyOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.time.OffsetDateTime
import java.util.UUID

internal class HttpDatabaseServiceClient(
    private val httpClient: HttpClient,
) : DatabaseServiceClientContract {

    override suspend fun upsertGoogleUser(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
    ): User? {
        val requestDto = UpsertGoogleUserRequestDto(
            googleId = googleId,
            email = email,
            name = name,
            avatarUrl = avatarUrl,
        )

        val response = httpClient.post("${ConfigurationValues.DATABASE_SERVICE_URL}/user/google/upsert") {
            contentType(ContentType.parse("application/json"))
            setBody(requestDto)
        }

        return response.successBodyOrThrow { r ->
            when (r.status) {
                HttpStatusCode.OK,
                HttpStatusCode.Created,
                HttpStatusCode.Conflict,
                -> r.body<User>()
                else -> null
            }
        }
    }

    override suspend fun createSession(
        userId: UUID,
        refreshToken: String,
        expiresIn: OffsetDateTime,
        fingerprint: String,
    ): Boolean {
        val response = httpClient.post("${ConfigurationValues.DATABASE_SERVICE_URL}/session") {
            contentType(ContentType.parse("application/json"))
            setBody(
                CreateSessionRequestDto(
                    userId = userId,
                    refreshToken = refreshToken,
                    expiresIn = expiresIn,
                    fingerprint = fingerprint,
                )
            )
        }

        return response.successBodyOrThrow { r ->
            when (r.status) {
                HttpStatusCode.Created -> true
                else -> false
            }
        }
    }

    override suspend fun validateSession(
        refreshToken: String,
        fingerprint: String,
    ): Boolean {
        val response = httpClient.post("${ConfigurationValues.DATABASE_SERVICE_URL}/session/validate") {
            contentType(ContentType.parse("application/json"))
            setBody(
                ValidateSessionRequestDto(
                    refreshToken = refreshToken,
                    fingerprint = fingerprint,
                )
            )
        }

        return response.successBodyOrThrow { r ->
            r.status == HttpStatusCode.OK
        }
    }
}
