package dev.kigya.headway.gateway.internal.mapping

import dev.kigya.headway.gateway.api.model.AuthPayload
import dev.kigya.headway.gateway.api.model.PublicUser
import dev.kigya.headway.gateway.api.model.UserRole
import dev.kigya.headway.gateway.internal.model.AuthServiceAuthResponse
import dev.kigya.headway.gateway.internal.model.AuthServiceUserDto
import dev.kigya.headway.gateway.internal.model.AuthServiceUserRoleDto

internal fun AuthServiceAuthResponse.toPublic(): AuthPayload =
    AuthPayload(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toPublic(),
    )

private fun AuthServiceUserDto.toPublic(): PublicUser =
    PublicUser(
        id = id,
        email = email,
        name = name,
        role = role.toPublic(),
        avatarUrl = avatarUrl,
    )

private fun AuthServiceUserRoleDto.toPublic(): UserRole =
    UserRole.entries.firstOrNull { it.name == this.name } ?: UserRole.GUEST
