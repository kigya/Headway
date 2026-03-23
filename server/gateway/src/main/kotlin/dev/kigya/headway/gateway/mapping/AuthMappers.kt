package dev.kigya.headway.gateway.mapping

import dev.kigya.headway.auth.api.model.out.AuthGoogleLoginResponse
import dev.kigya.headway.auth.api.model.out.AuthGuestLoginResponse
import dev.kigya.headway.auth.api.model.out.AuthRefreshAccessTokenResponse
import dev.kigya.headway.database.api.model.`in`.DatabaseSessionPlatform
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayGuestLoginResponse
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import dev.kigya.headway.gateway.model.GatewaySessionPlatform

internal fun AuthGoogleLoginResponse.toGateway(): GatewayGoogleLoginResponse =
    GatewayGoogleLoginResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toGateway(),
    )

internal fun AuthRefreshAccessTokenResponse.toGateway() = GatewayRefreshAccessTokenResponse(
    accessToken = accessToken,
)

internal fun AuthGuestLoginResponse.toGateway(): GatewayGuestLoginResponse =
    GatewayGuestLoginResponse(
        accessToken = accessToken,
        expiresAtEpochMs = expiresAtEpochMs,
    )

internal fun GatewaySessionPlatform.toDatabase() =
    DatabaseSessionPlatform.entries.firstOrNull { it.name == name } ?: DatabaseSessionPlatform.ANDROID
