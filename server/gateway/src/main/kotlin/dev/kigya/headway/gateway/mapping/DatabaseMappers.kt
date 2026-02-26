package dev.kigya.headway.gateway.mapping

import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.api.model.out.DatabaseUserDepartment
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserDepartment
import dev.kigya.headway.gateway.model.GatewayUserRole

internal fun DatabaseUser.toGateway(): GatewayUser =
    GatewayUser(
        id = id,
        email = email,
        name = name,
        role = role.toGateway(),
        avatarUrl = avatarUrl,
        department = department.toGateway(),
    )

internal fun DatabaseUserRole.toGateway(): GatewayUserRole =
    GatewayUserRole.entries.firstOrNull { it.name == name } ?: GatewayUserRole.GUEST

internal fun DatabaseUserDepartment.toGateway(): GatewayUserDepartment =
    GatewayUserDepartment.entries.firstOrNull { it.name == name } ?: GatewayUserDepartment.ANDROID

