package dev.kigya.headway.gateway.domain.repository

import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserRole
import java.util.UUID

internal interface DatabaseRepositoryContract {
    suspend fun inviteUser(
        email: String,
        department: String,
        role: GatewayUserRole? = null,
    ): GatewayUser

    suspend fun getUserById(userId: UUID): GatewayUser
}
