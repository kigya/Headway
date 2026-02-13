package dev.kigya.headway.database.internal.domain.usecase

import dev.kigya.headway.database.api.model.`in`.DatabaseSessionPlatform
import dev.kigya.headway.database.internal.domain.repository.RefreshSessionsRepositoryContract
import java.time.OffsetDateTime
import java.util.UUID

internal class CreateSessionUseCase(
    private val refreshSessionsRepository: RefreshSessionsRepositoryContract,
) {
    suspend operator fun invoke(
        userId: UUID,
        refreshToken: String,
        expiresIn: OffsetDateTime,
        fingerprint: String,
        platform: DatabaseSessionPlatform,
    ) = refreshSessionsRepository.createSession(
        userId = userId,
        refreshToken = refreshToken,
        expiresIn = expiresIn,
        fingerprint = fingerprint,
        platform = platform,
    )
}
