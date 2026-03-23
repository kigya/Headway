package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.repository.AuthRepositoryContract
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse

internal class RefreshAccessTokenUseCase(
    private val authRepository: AuthRepositoryContract,
) {
    suspend operator fun invoke(
        refreshToken: String,
        fingerprint: String,
    ): GatewayRefreshAccessTokenResponse {
        val trimmedToken = refreshToken.trim()
        val trimmedFingerprint = fingerprint.trim()

        if (trimmedToken.isBlank()) {
            throw GatewayException.InvalidRequest(
                reason = GatewayErrorReason.EMPTY_REFRESH_TOKEN,
                message = "Refresh token is blank",
            )
        }
        if (trimmedFingerprint.isBlank()) {
            throw GatewayException.InvalidRequest(
                reason = GatewayErrorReason.EMPTY_FINGERPRINT,
                message = "Fingerprint is blank",
            )
        }

        return authRepository.refreshToken(refreshToken = trimmedToken, fingerprint = trimmedFingerprint)
    }
}
