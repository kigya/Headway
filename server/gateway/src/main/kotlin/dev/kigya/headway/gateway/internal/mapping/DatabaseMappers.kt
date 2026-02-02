package dev.kigya.headway.gateway.internal.mapping

import dev.kigya.headway.gateway.api.model.InvitedUserPayload
import dev.kigya.headway.gateway.internal.model.DatabaseServiceUserDto

internal fun DatabaseServiceUserDto.toPublic(): InvitedUserPayload =
    InvitedUserPayload(
        id = id,
        email = email,
        department = department,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
