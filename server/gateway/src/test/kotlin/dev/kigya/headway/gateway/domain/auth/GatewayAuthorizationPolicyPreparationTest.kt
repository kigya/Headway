package dev.kigya.headway.gateway.domain.auth

import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.model.GatewayPrincipal
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserRole
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GatewayAuthorizationPolicyPreparationTest {

    @Test
    fun `preparation rejects guest principal`() {
        val forbiddenException = assertFailsWith<GatewayException.Forbidden> {
            GatewayAuthorizationPolicy.ensure(
                principal = GatewayPrincipal.Guest(
                    guestSessionId = UUID.fromString("00000000-0000-0000-0000-00000000abba"),
                    scopes = listOf("learn_guest"),
                ),
                operation = GatewayOperation.Preparation,
            )
        }
        assertEquals(GatewayErrorReason.GUEST_NOT_ALLOWED, forbiddenException.reason)
    }

    @Test
    fun `preparation rejects employee user`() {
        val forbiddenException = assertFailsWith<GatewayException.Forbidden> {
            GatewayAuthorizationPolicy.ensure(
                principal = GatewayPrincipal.User(
                    user = GatewayUser(
                        id = UUID.fromString("00000000-0000-0000-0000-000000000099"),
                        email = "e@example.com",
                        name = "Employee",
                        role = GatewayUserRole.EMPLOYEE,
                    ),
                ),
                operation = GatewayOperation.Preparation,
            )
        }
        assertEquals(GatewayErrorReason.INSUFFICIENT_ROLE, forbiddenException.reason)
    }
}
