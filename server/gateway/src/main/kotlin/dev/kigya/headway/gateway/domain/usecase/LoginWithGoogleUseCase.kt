package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.repository.AuthRepositoryContract
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewaySessionPlatform

internal class LoginWithGoogleUseCase(
    private val authRepository: AuthRepositoryContract,
) {
    suspend operator fun invoke(
        idToken: String,
        fingerprint: String,
        platform: GatewaySessionPlatform,
    ): GatewayGoogleLoginResponse {
        val trimmedIdToken = idToken.trim()
        val trimmedFingerprint = fingerprint.trim()

        if (trimmedIdToken.isBlank()) throw GatewayException.InvalidRequest("ID token is blank")
        if (trimmedFingerprint.isBlank()) throw GatewayException.InvalidRequest("Fingerprint is blank")

        return authRepository.loginWithGoogle(
            idToken = trimmedIdToken,
            fingerprint = trimmedFingerprint,
            platform = platform,
        )
    }
}
