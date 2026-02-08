package dev.kigya.headway.gateway.domain.repository

import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse

internal interface AuthRepositoryContract {
    suspend fun loginWithGoogle(idToken: String, fingerprint: String): GatewayGoogleLoginResponse
    suspend fun refreshToken(refreshToken: String, fingerprint: String): GatewayRefreshAccessTokenResponse
}
