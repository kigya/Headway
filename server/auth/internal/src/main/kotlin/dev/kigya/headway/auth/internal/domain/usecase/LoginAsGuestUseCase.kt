package dev.kigya.headway.auth.internal.domain.usecase

import dev.kigya.headway.auth.api.model.out.AuthGuestLoginResponse
import dev.kigya.headway.auth.internal.domain.repository.JWTRepositoryContract

internal class LoginAsGuestUseCase(
    private val jwtRepository: JWTRepositoryContract,
) {
    operator fun invoke(): AuthGuestLoginResponse {
        val (token, expiresAtEpochMs) = jwtRepository.generateGuestAccessToken()
        return AuthGuestLoginResponse(
            accessToken = token,
            expiresAtEpochMs = expiresAtEpochMs,
        )
    }
}
