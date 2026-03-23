package dev.kigya.headway.gateway.model

import java.util.UUID

internal sealed class GatewayPrincipal {
    data class User(
        val user: GatewayUser,
    ) : GatewayPrincipal()

    data class Guest(
        val guestSessionId: UUID,
        val scopes: List<String>,
    ) : GatewayPrincipal()
}
