package dev.kigya.headway.auth.internal.domain.usecase

import dev.kigya.headway.auth.api.model.out.AuthValidateTokenResponse
import dev.kigya.headway.auth.internal.domain.error.AuthException
import dev.kigya.headway.auth.internal.domain.repository.JWTRepositoryContract

internal class ValidateAccessTokenUseCase(
    private val jwtRepository: JWTRepositoryContract,
) {
    operator fun invoke(accessToken: String): AuthValidateTokenResponse {
        val trimmedToken = accessToken.trim()
        if (trimmedToken.isBlank()) {
            throw AuthException.Unauthorized("Invalid access token")
        }
        if (!jwtRepository.isAccessTokenValid(trimmedToken)) {
            throw AuthException.Unauthorized("Invalid access token")
        }

        val userUuid = jwtRepository.getUserUUID(trimmedToken)
            ?: throw AuthException.Unauthorized("Invalid access token")

        return AuthValidateTokenResponse(
            userUuid = userUuid,
            isValid = true,
        )
    }
}
