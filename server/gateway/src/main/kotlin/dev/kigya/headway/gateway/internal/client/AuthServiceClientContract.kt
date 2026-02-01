package dev.kigya.headway.gateway.internal.client

import dev.kigya.headway.gateway.internal.model.AuthServiceAuthResponse
import dev.kigya.headway.gateway.internal.model.AuthServiceRefreshTokenResponse

internal interface AuthServiceClientContract {
    suspend fun loginWithGoogle(
        idToken: String,
        fingerprint: String,
    ): AuthServiceAuthResponse

    suspend fun refreshToken(
        refreshToken: String,
        fingerprint: String,
    ): AuthServiceRefreshTokenResponse
}
