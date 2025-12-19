package dev.kigya.headway.auth.data.database

import dev.kigya.headway.auth.ConfigurationValues
import dev.kigya.headway.auth.data.database.request.CreateSessionRequestDto
import dev.kigya.headway.auth.data.database.request.ValidateSessionRequestDto
import ext.successBodyOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.time.OffsetDateTime
import java.util.UUID

/**
 * @see dev.kigya.headway.database.routing.session
 * */
internal suspend fun HttpClient.createSession(
    userId: UUID,
    refreshToken: String,
    expiresIn: OffsetDateTime,
    fingerprint: String,
): Boolean {
    val response = this.post("${ConfigurationValues.DATABASE_SERVICE_URL}/session") {
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

    return response.successBodyOrThrow { response ->
        response.status == HttpStatusCode.Created
    }
}

internal suspend fun HttpClient.validateSession(
    refreshToken: String,
    fingerprint: String,
): Boolean? {
    val response = this.post("${ConfigurationValues.DATABASE_SERVICE_URL}/session/validate") {
        contentType(ContentType.parse("application/json"))
        setBody(
            ValidateSessionRequestDto(
                refreshToken = refreshToken,
                fingerprint = fingerprint,
            )
        )
    }

    return response.successBodyOrThrow { response ->
        response.status == HttpStatusCode.OK
    }
}
