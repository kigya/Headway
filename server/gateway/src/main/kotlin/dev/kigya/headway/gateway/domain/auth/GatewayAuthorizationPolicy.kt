package dev.kigya.headway.gateway.domain.auth

import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.model.GatewayPrincipal
import dev.kigya.headway.gateway.model.GatewayUserRole

internal object GatewayAuthorizationPolicy {

    fun ensure(
        principal: GatewayPrincipal,
        operation: GatewayOperation,
    ) {
        when (operation) {
            GatewayOperation.HealthCheck -> return
            GatewayOperation.InviteUser -> ensureInviteUser(principal)
        }
    }

    private fun ensureInviteUser(principal: GatewayPrincipal) {
        when (principal) {
            is GatewayPrincipal.Guest -> throw GatewayException.Forbidden(
                reason = GatewayErrorReason.GUEST_NOT_ALLOWED,
                message = "Guests cannot invite users",
            )

            is GatewayPrincipal.User -> {
                val role = principal.user.role
                if (role != GatewayUserRole.DEVELOPER && role != GatewayUserRole.MANAGER) {
                    throw GatewayException.Forbidden(
                        reason = GatewayErrorReason.INSUFFICIENT_ROLE,
                        message = "Insufficient role to invite users",
                    )
                }
            }
        }
    }
}
