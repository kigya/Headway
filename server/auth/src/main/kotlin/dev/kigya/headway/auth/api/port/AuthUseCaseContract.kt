package dev.kigya.headway.auth.api.port

import dev.kigya.headway.auth.api.model.AuthResponse
import dev.kigya.headway.auth.api.model.RefreshTokenResponse

interface AuthUseCaseContract {
    suspend fun loginWithGoogle(
        idToken: String,
        fingerprint: String,
    ): AuthResponse

    suspend fun refreshToken(
        refreshToken: String,
        fingerprint: String,
    ): RefreshTokenResponse
}
