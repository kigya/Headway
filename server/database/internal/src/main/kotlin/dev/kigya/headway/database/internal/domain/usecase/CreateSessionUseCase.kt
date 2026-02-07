package dev.kigya.headway.database.internal.domain.usecase

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
    ) = refreshSessionsRepository.createSession(
        userId = userId,
        refreshToken = refreshToken,
        expiresIn = expiresIn,
        fingerprint = fingerprint,
    )
}
