package dev.kigya.headway.database.domain.service

import java.time.OffsetDateTime
import java.util.UUID

internal interface RefreshSessionsService {

    suspend fun createSession(
        userId: UUID,
        refreshToken: String,
        expiresIn: OffsetDateTime,
        fingerprint: String,
    )

    suspend fun verifySession(
        rawRefreshToken: String,
        fingerprint: String,
    ): UUID

    suspend fun deleteAllUserSessions(userId: UUID)
}
