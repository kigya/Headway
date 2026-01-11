package dev.kigya.headway.database.api.port

import java.time.OffsetDateTime
import java.util.UUID

interface RefreshSessionsServiceContract {

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
