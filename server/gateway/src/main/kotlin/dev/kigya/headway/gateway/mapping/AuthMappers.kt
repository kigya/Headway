package dev.kigya.headway.gateway.mapping

import dev.kigya.headway.auth.api.model.out.AuthGoogleLoginResponse
import dev.kigya.headway.auth.api.model.out.AuthRefreshAccessTokenResponse
import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import dev.kigya.headway.gateway.model.PublicUser
import dev.kigya.headway.gateway.model.UserRole

internal fun AuthGoogleLoginResponse.toGateway(): GatewayGoogleLoginResponse =
    GatewayGoogleLoginResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toPublicUser(),
    )

internal fun AuthRefreshAccessTokenResponse.toGateway() = GatewayRefreshAccessTokenResponse(
    accessToken = accessToken,
)

private fun DatabaseUser.toPublicUser(): PublicUser =
    PublicUser(
        id = id,
        email = email,
        name = name,
        role = role.toPublicRole(),
        avatarUrl = avatarUrl,
    )

private fun DatabaseUserRole.toPublicRole(): UserRole =
    UserRole.entries.firstOrNull { it.name == name } ?: UserRole.GUEST
