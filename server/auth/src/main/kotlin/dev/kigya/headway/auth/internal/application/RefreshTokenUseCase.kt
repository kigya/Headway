package dev.kigya.headway.auth.internal.application

import dev.kigya.headway.auth.api.error.InvalidRefreshTokenException
import dev.kigya.headway.auth.api.model.RefreshTokenResponse
import dev.kigya.headway.auth.api.port.RefreshTokenUseCaseContract
import dev.kigya.headway.auth.internal.client.DatabaseServiceClientContract
import dev.kigya.headway.auth.internal.security.JWTServiceContract

internal class RefreshTokenUseCase(
    private val databaseServiceClient: DatabaseServiceClientContract,
    private val jwtService: JWTServiceContract,
) : RefreshTokenUseCaseContract {
    override suspend fun invoke(
        refreshToken: String,
        fingerprint: String,
    ): RefreshTokenResponse {
        if (jwtService.verifyRefreshToken(refreshToken).not()) {
            throw InvalidRefreshTokenException("Invalid refresh token.")
        }

        val isValidSession = databaseServiceClient.validateSession(
            refreshToken = refreshToken,
            fingerprint = fingerprint,
        )
        if (!isValidSession) {
            throw InvalidRefreshTokenException("Failed to validate session.")
        }

        val userUUID = jwtService.getUserUUID(refreshToken)
            ?: throw InvalidRefreshTokenException("Invalid refresh token. User UUID is null")

        val accessToken = jwtService.generateAccessToken(userUUID)
        return RefreshTokenResponse(accessToken)
    }
}
