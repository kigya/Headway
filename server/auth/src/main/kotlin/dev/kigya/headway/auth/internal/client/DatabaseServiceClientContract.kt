package dev.kigya.headway.auth.internal.client

import dev.kigya.headway.auth.api.model.User
import java.time.OffsetDateTime
import java.util.UUID

internal interface DatabaseServiceClientContract {
    suspend fun upsertGoogleUser(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
    ): User?

    suspend fun createSession(
        userId: UUID,
        refreshToken: String,
        expiresIn: OffsetDateTime,
        fingerprint: String,
    ): Boolean

    suspend fun validateSession(
        refreshToken: String,
        fingerprint: String,
    ): Boolean
}
