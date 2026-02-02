package dev.kigya.headway.database.internal.application

import dev.kigya.headway.database.api.port.ValidateSessionUseCaseContract
import dev.kigya.headway.database.internal.data.RefreshSessionsRepositoryContract

internal class ValidateSessionUseCase(
    private val refreshSessionsRepository: RefreshSessionsRepositoryContract,
) : ValidateSessionUseCaseContract {
    override suspend fun invoke(refreshToken: String, fingerprint: String) {
        refreshSessionsRepository.verifySession(
            rawRefreshToken = refreshToken,
            fingerprint = fingerprint,
        )
    }
}
