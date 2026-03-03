package dev.kigya.headway.gateway.domain.repository

import dev.kigya.headway.gateway.model.GatewayUser

internal interface DatabaseRepositoryContract {
    suspend fun inviteUser(
        email: String,
        department: String,
    ): GatewayUser
}
