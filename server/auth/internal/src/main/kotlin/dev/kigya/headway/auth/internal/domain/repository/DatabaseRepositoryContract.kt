package dev.kigya.headway.auth.internal.domain.repository

import dev.kigya.headway.database.api.model.out.DatabaseUser
import java.time.OffsetDateTime
import java.util.UUID

interface DatabaseRepositoryContract {
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
    )

    suspend fun validateSession(
        refreshToken: String,
        fingerprint: String,
    )
}
