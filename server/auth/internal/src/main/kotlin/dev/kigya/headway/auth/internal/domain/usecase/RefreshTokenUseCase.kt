package dev.kigya.headway.auth.internal.domain.usecase

import dev.kigya.headway.auth.api.AuthServicePlainText
import dev.kigya.headway.auth.api.model.out.AuthRefreshAccessTokenResponse
import dev.kigya.headway.auth.internal.domain.error.AuthException
import dev.kigya.headway.auth.internal.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.auth.internal.domain.repository.JWTRepositoryContract

internal class RefreshTokenUseCase(
    private val databaseRepository: DatabaseRepositoryContract,
    private val jwtRepository: JWTRepositoryContract,
) {
    suspend operator fun invoke(
        refreshToken: String,
        fingerprint: String,
    ): AuthRefreshAccessTokenResponse {
        val tokenFamily = jwtRepository.decodeTokenType(refreshToken)
        if (tokenFamily == TOKEN_TYPE_GUEST_ACCESS || tokenFamily == TOKEN_TYPE_ACCESS) {
            throw AuthException.Unauthorized(AuthServicePlainText.INVALID_REFRESH_TOKEN)
        }

        if (!jwtRepository.isRefreshTokenValid(refreshToken)) {
            throw AuthException.Unauthorized(AuthServicePlainText.INVALID_REFRESH_TOKEN)
        }

        databaseRepository.validateSession(
            refreshToken = refreshToken,
            fingerprint = fingerprint,
        )

        val userUUID = jwtRepository.getUserUUID(refreshToken)
            ?: throw AuthException.Unauthorized(AuthServicePlainText.INVALID_REFRESH_TOKEN)

        val accessToken = jwtRepository.generateAccessToken(userUUID)
        return AuthRefreshAccessTokenResponse(accessToken)
    }
}

private const val TOKEN_TYPE_ACCESS = "access"
private const val TOKEN_TYPE_GUEST_ACCESS = "guest_access"
