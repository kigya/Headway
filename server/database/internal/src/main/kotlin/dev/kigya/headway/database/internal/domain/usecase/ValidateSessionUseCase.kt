package dev.kigya.headway.database.internal.domain.usecase

import dev.kigya.headway.database.internal.domain.repository.RefreshSessionsRepositoryContract

internal class ValidateSessionUseCase(
    private val refreshSessionsRepository: RefreshSessionsRepositoryContract,
) {
    suspend operator fun invoke(refreshToken: String, fingerprint: String) {
        refreshSessionsRepository.verifySession(
            rawRefreshToken = refreshToken,
            fingerprint = fingerprint,
        )
    }
}
