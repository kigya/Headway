package dev.kigya.headway.auth.internal.domain.repository

import dev.kigya.headway.database.api.model.`in`.DatabaseSessionPlatform
import dev.kigya.headway.database.api.model.out.DatabaseUser
import java.time.OffsetDateTime
import java.util.UUID

internal interface DatabaseRepositoryContract {
    suspend fun upsertGoogleUser(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
    ): DatabaseUser

    suspend fun createSession(
        userId: UUID,
        refreshToken: String,
        expiresIn: OffsetDateTime,
        fingerprint: String,
        platform: DatabaseSessionPlatform,
    )

    suspend fun validateSession(
        refreshToken: String,
        fingerprint: String,
    )

    suspend fun registerGuestSession(sessionId: UUID)

    suspend fun ensureGuestSessionActive(sessionId: UUID)
}
