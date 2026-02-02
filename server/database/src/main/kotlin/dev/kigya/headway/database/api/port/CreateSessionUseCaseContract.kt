package dev.kigya.headway.database.api.port

import java.time.OffsetDateTime
import java.util.UUID

interface CreateSessionUseCaseContract {
    suspend operator fun invoke(
        userId: UUID,
        refreshToken: String,
        expiresIn: OffsetDateTime,
        fingerprint: String,
    )
}
