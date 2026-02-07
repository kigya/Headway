package dev.kigya.headway.gateway.port

import dev.kigya.headway.gateway.model.GatewayUser

interface InviteUserUseCaseContract {
    suspend operator fun invoke(
        email: String,
        department: String,
    ): GatewayUser
}
