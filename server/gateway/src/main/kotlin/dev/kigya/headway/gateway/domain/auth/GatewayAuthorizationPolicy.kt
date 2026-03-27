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
            GatewayOperation.HomeScreen -> ensureHomeScreen(principal)
            GatewayOperation.Preparation -> ensurePreparation(principal)
            GatewayOperation.LearningRead -> ensureLearningRead(principal)
            GatewayOperation.LearningRemarkWrite -> ensureLearningRemarkWrite(principal)
            GatewayOperation.LogoutGuest -> ensureLogoutGuest(principal)
        }
    }

    private fun ensureHomeScreen(principal: GatewayPrincipal) {
        when (principal) {
            is GatewayPrincipal.Guest -> throw GatewayException.Forbidden(
                reason = GatewayErrorReason.GUEST_NOT_ALLOWED,
                message = "Guests cannot access home screen",
            )

            is GatewayPrincipal.User -> Unit
        }
    }

    private fun ensurePreparation(principal: GatewayPrincipal) {
        when (principal) {
            is GatewayPrincipal.Guest -> throw GatewayException.Forbidden(
                reason = GatewayErrorReason.GUEST_NOT_ALLOWED,
                message = "Guests cannot access preparation",
            )

            is GatewayPrincipal.User -> {
                val role = principal.user.role
                if (role == GatewayUserRole.GUEST || role == GatewayUserRole.EMPLOYEE) {
                    throw GatewayException.Forbidden(
                        reason = GatewayErrorReason.INSUFFICIENT_ROLE,
                        message = "Insufficient role for preparation",
                    )
                }
            }
        }
    }

    private fun ensureLearningRead(principal: GatewayPrincipal) {
        when (principal) {
            is GatewayPrincipal.Guest ->
                if (!principal.scopes.contains(GUEST_LEARN_SCOPE)) {
                    throw GatewayException.Forbidden(
                        reason = GatewayErrorReason.GUEST_NOT_ALLOWED,
                        message = "Guest scope does not allow learning reads",
                    )
                }

            is GatewayPrincipal.User -> Unit
        }
    }

    private fun ensureLearningRemarkWrite(principal: GatewayPrincipal) {
        when (principal) {
            is GatewayPrincipal.Guest -> throw GatewayException.Forbidden(
                reason = GatewayErrorReason.GUEST_NOT_ALLOWED,
                message = "Guests cannot author learning remarks",
            )

            is GatewayPrincipal.User -> {
                val role = principal.user.role
                if (role != GatewayUserRole.MENTOR &&
                    role != GatewayUserRole.DEVELOPER &&
                    role != GatewayUserRole.MANAGER
                ) {
                    throw GatewayException.Forbidden(
                        reason = GatewayErrorReason.INSUFFICIENT_ROLE,
                        message = "Insufficient role to author learning remarks",
                    )
                }
            }
        }
    }

    private fun ensureLogoutGuest(principal: GatewayPrincipal) {
        when (principal) {
            is GatewayPrincipal.Guest ->
                if (!principal.scopes.contains(GUEST_LEARN_SCOPE)) {
                    throw GatewayException.Forbidden(
                        reason = GatewayErrorReason.GUEST_NOT_ALLOWED,
                        message = "Guest scope does not allow logout",
                    )
                }

            is GatewayPrincipal.User -> throw GatewayException.Forbidden(
                reason = GatewayErrorReason.INSUFFICIENT_ROLE,
                message = "Only guest sessions may call logoutGuest",
            )
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

private const val GUEST_LEARN_SCOPE: String = "learn_guest"
