package dev.kigya.headway.gateway.domain.repository

import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import dev.kigya.headway.gateway.model.GatewaySessionPlatform

internal interface AuthRepositoryContract {

    suspend fun loginWithGoogle(
        idToken: String,
        fingerprint: String,
        platform: GatewaySessionPlatform,
    ): GatewayGoogleLoginResponse

    suspend fun refreshToken(
        refreshToken: String,
        fingerprint: String,
    ): GatewayRefreshAccessTokenResponse
}
