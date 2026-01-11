package dev.kigya.headway.internal.client

import dev.kigya.headway.internal.model.AuthServiceAuthResponse
import dev.kigya.headway.internal.model.AuthServiceRefreshTokenResponse

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
