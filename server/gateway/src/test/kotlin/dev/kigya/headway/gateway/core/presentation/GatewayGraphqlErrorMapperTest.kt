package dev.kigya.headway.gateway.core.presentation

import dev.kigya.headway.gateway.core.exception.GatewayErrorCategory
import dev.kigya.headway.gateway.core.exception.GatewayErrorCode
import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import kotlin.test.Test
import kotlin.test.assertEquals

class GatewayGraphqlErrorMapperTest {

    @Test
    fun `maps invitation required exception to envelope`() {
        val envelope = GatewayGraphqlErrorMapper.map(GatewayException.InvitationRequired())

        assertEquals(GatewayErrorCode.INVITATION_REQUIRED, envelope.code)
        assertEquals("This account is not invited yet", envelope.message)
        assertEquals("INVITATION_REQUIRED", envelope.extensions["code"])
        assertEquals("authorization", envelope.extensions["category"])
        assertEquals("USER_NOT_INVITED", envelope.extensions["reason"])
        assertEquals(403, envelope.extensions["httpStatus"])
        assertEquals(false, envelope.extensions["retryable"])
    }

    @Test
    fun `maps dependency unavailable with retryable and dependency`() {
        val ex = GatewayException.DependencyUnavailable(dependency = "auth")
        val envelope = GatewayGraphqlErrorMapper.map(ex)

        assertEquals(GatewayErrorCode.DEPENDENCY_UNAVAILABLE, envelope.code)
        assertEquals("dependency", envelope.extensions["category"])
        assertEquals("DEPENDENCY_FAILURE", envelope.extensions["reason"])
        assertEquals(true, envelope.extensions["retryable"])
        assertEquals("auth", envelope.extensions["dependency"])
    }

    @Test
    fun `extensionsFor exposes upstream protocol fields`() {
        val ex = GatewayException.UpstreamProtocol(dependency = "database", status = 418)
        val ext = GatewayGraphqlErrorMapper.extensionsFor(ex)

        assertEquals("UPSTREAM_PROTOCOL", ext["reason"])
        assertEquals(418, ext["upstreamStatus"])
        assertEquals("database", ext["dependency"])
        assertEquals(true, ext["retryable"])
    }

    @Test
    fun `maps guest forbidden`() {
        val ex = GatewayException.Forbidden(
            reason = GatewayErrorReason.GUEST_NOT_ALLOWED,
            message = "Guests cannot invite users",
        )
        val envelope = GatewayGraphqlErrorMapper.map(ex)

        assertEquals(GatewayErrorCode.FORBIDDEN, envelope.code)
        assertEquals(GatewayErrorCategory.AUTHORIZATION.name.lowercase(), envelope.extensions["category"])
        assertEquals("GUEST_NOT_ALLOWED", envelope.extensions["reason"])
    }
}
