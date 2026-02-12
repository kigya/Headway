package dev.kigya.headway.gateway.mapping

import dev.kigya.headway.auth.api.model.out.AuthGoogleLoginResponse
import dev.kigya.headway.auth.api.model.out.AuthRefreshAccessTokenResponse
import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserRole

internal fun AuthGoogleLoginResponse.toGateway(): GatewayGoogleLoginResponse =
    GatewayGoogleLoginResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toGatewayUser(),
    )

internal fun AuthRefreshAccessTokenResponse.toGateway() = GatewayRefreshAccessTokenResponse(
    accessToken = accessToken,
)

private fun DatabaseUser.toGatewayUser(): GatewayUser =
    GatewayUser(
        id = id,
        email = email,
        name = name,
        role = role.toPublicRole(),
        avatarUrl = avatarUrl,
    )

private fun DatabaseUserRole.toPublicRole(): GatewayUserRole =
    GatewayUserRole.entries.firstOrNull { it.name == name } ?: GatewayUserRole.GUEST
