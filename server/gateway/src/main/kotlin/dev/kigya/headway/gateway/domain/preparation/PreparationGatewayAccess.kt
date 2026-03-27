package dev.kigya.headway.gateway.domain.preparation

import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserRole

internal fun requirePreparationFacilitator(user: GatewayUser): GatewayUser {
    when (user.role) {
        GatewayUserRole.GUEST,
        GatewayUserRole.EMPLOYEE,
        -> throw GatewayException.Forbidden(
            reason = GatewayErrorReason.INSUFFICIENT_ROLE,
            message = "Insufficient role for preparation",
        )

        GatewayUserRole.DEVELOPER,
        GatewayUserRole.MANAGER,
        GatewayUserRole.MENTOR,
        -> Unit
    }
    return user
}
