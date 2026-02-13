package dev.kigya.headway.database.internal.domain.repository

import dev.kigya.headway.database.api.model.`in`.DatabaseSessionPlatform
import java.time.OffsetDateTime
import java.util.UUID

internal interface RefreshSessionsRepositoryContract {
    suspend fun createSession(
        userId: UUID,
        refreshToken: String,
        expiresIn: OffsetDateTime,
        fingerprint: String,
        platform: DatabaseSessionPlatform,
    )

    suspend fun verifySession(
        rawRefreshToken: String,
        fingerprint: String,
    ): UUID

    suspend fun deleteAllUserSessions(userId: UUID)
}
