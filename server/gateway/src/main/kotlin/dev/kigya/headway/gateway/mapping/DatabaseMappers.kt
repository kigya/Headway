package dev.kigya.headway.gateway.mapping

import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.gateway.model.GatewayUser

internal fun DatabaseUser.toPublic(): GatewayUser =
    GatewayUser(
        id = id,
        email = email,
        department = department.name,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
