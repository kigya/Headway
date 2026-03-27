package dev.kigya.headway.auth.internal.domain.usecase

import dev.kigya.headway.auth.api.model.out.AuthGuestLoginResponse
import dev.kigya.headway.auth.internal.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.auth.internal.domain.repository.JWTRepositoryContract

internal class LoginAsGuestUseCase(
    private val jwtRepository: JWTRepositoryContract,
    private val databaseRepository: DatabaseRepositoryContract,
) {
    suspend operator fun invoke(): AuthGuestLoginResponse {
        val issued = jwtRepository.generateGuestAccessToken()
        databaseRepository.registerGuestSession(sessionId = issued.sessionId)
        return AuthGuestLoginResponse(
            accessToken = issued.accessToken,
            expiresAtEpochMs = issued.expiresAtEpochMs,
        )
    }
}
