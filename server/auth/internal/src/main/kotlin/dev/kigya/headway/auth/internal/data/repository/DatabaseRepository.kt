package dev.kigya.headway.auth.internal.data.repository

import dev.kigya.headway.auth.internal.domain.error.AuthException
import dev.kigya.headway.auth.internal.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.database.api.model.`in`.DatabaseCreateSessionPayloadDto
import dev.kigya.headway.database.api.model.`in`.DatabaseUpsertGoogleUserPayloadDto
import dev.kigya.headway.database.api.model.`in`.DatabaseValidateSessionPayloadDto
import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.api.model.resource.DatabaseResource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.time.OffsetDateTime
import java.util.UUID

class DatabaseRepository(
    private val httpClient: HttpClient,
) : DatabaseRepositoryContract {

    override suspend fun upsertGoogleUser(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
    ): DatabaseUser {
        val response = try {
            httpClient.post(DatabaseResource.User.Google.Upsert()) {
                contentType(ContentType.Application.Json)
                setBody(
                    DatabaseUpsertGoogleUserPayloadDto(
                        googleId = googleId,
                        email = email,
                        name = name,
                        avatarUrl = avatarUrl,
                    )
                )
            }
        } catch (t: Throwable) {
            throw AuthException.DependencyUnavailable("database", cause = t)
        }

        return when (val status = response.status) {
            HttpStatusCode.OK, HttpStatusCode.Created, HttpStatusCode.Conflict -> response.body<DatabaseUser>()
            HttpStatusCode.Forbidden -> throw AuthException.UserNotInvited()

            else -> throw AuthException.UpstreamProtocol(
                dependency = "database",
                status = status.value,
            )
        }
    }

    override suspend fun createSession(
        userId: UUID,
        refreshToken: String,
        expiresIn: OffsetDateTime,
        fingerprint: String,
    ) {
        val response = try {
            httpClient.post(DatabaseResource.Session()) {
                contentType(ContentType.Application.Json)
                setBody(
                    DatabaseCreateSessionPayloadDto(
                        userId = userId,
                        refreshToken = refreshToken,
                        expiresIn = expiresIn,
                        fingerprint = fingerprint,
                    )
                )
            }
        } catch (t: Throwable) {
            throw AuthException.DependencyUnavailable("database", cause = t)
        }

        when (val status = response.status) {
            HttpStatusCode.Created -> return
            else -> throw AuthException.UpstreamProtocol(
                dependency = "database",
                status = status.value,
            )
        }
    }

    override suspend fun validateSession(
        refreshToken: String,
        fingerprint: String,
    ) {
        val response = try {
            httpClient.post(DatabaseResource.Session.Validate()) {
                contentType(ContentType.Application.Json)
                setBody(
                    DatabaseValidateSessionPayloadDto(
                        refreshToken = refreshToken,
                        fingerprint = fingerprint,
                    )
                )
            }
        } catch (t: Throwable) {
            throw AuthException.DependencyUnavailable("database", cause = t)
        }

        when (val status = response.status) {
            HttpStatusCode.OK -> return

            HttpStatusCode.Unauthorized,
            HttpStatusCode.NotFound,
            HttpStatusCode.Forbidden,
                -> throw AuthException.Unauthorized("Invalid session")

            else -> throw AuthException.UpstreamProtocol(
                dependency = "database",
                status = status.value,
            )
        }
    }
}
