package dev.kigya.headway.auth.internal.domain.usecase

import dev.kigya.headway.auth.api.model.out.AuthRefreshAccessTokenResponse
import dev.kigya.headway.auth.internal.domain.error.AuthException
import dev.kigya.headway.auth.internal.domain.repository.JWTRepositoryContract
import dev.kigya.headway.auth.internal.domain.repository.DatabaseRepositoryContract

internal class RefreshTokenUseCase(
    private val databaseRepository: DatabaseRepositoryContract,
    private val jwtRepository: JWTRepositoryContract,
) {
    suspend operator fun invoke(
        refreshToken: String,
        fingerprint: String,
    ): AuthRefreshAccessTokenResponse {
        if (!jwtRepository.isRefreshTokenValid(refreshToken))
            throw AuthException.Unauthorized("Invalid refresh token")

        databaseRepository.validateSession(
            refreshToken = refreshToken,
            fingerprint = fingerprint,
        )

        val userUUID = jwtRepository.getUserUUID(refreshToken)
            ?: throw AuthException.Unauthorized("Invalid refresh token")

        val accessToken = jwtRepository.generateAccessToken(userUUID)
        return AuthRefreshAccessTokenResponse(accessToken)
    }
}
