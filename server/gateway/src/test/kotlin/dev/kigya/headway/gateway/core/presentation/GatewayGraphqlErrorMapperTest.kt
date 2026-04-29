package dev.kigya.headway.gateway.core.presentation

import dev.kigya.headway.gateway.core.exception.GatewayErrorCategory
import dev.kigya.headway.gateway.core.exception.GatewayErrorCode
import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import kotlinx.serialization.SerializationException
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
        val gatewayException = GatewayException.DependencyUnavailable(dependency = "auth")
        val envelope = GatewayGraphqlErrorMapper.map(gatewayException)

        assertEquals(GatewayErrorCode.DEPENDENCY_UNAVAILABLE, envelope.code)
        assertEquals("dependency", envelope.extensions["category"])
        assertEquals("DEPENDENCY_FAILURE", envelope.extensions["reason"])
        assertEquals(true, envelope.extensions["retryable"])
        assertEquals("auth", envelope.extensions["dependency"])
    }

    @Test
    fun `extensionsFor exposes upstream protocol fields`() {
        val gatewayException = GatewayException.UpstreamProtocol(dependency = "database", status = 418)
        val ext = GatewayGraphqlErrorMapper.extensionsFor(gatewayException)

        assertEquals("UPSTREAM_PROTOCOL", ext["reason"])
        assertEquals(418, ext["upstreamStatus"])
        assertEquals("database", ext["dependency"])
        assertEquals(true, ext["retryable"])
    }

    @Test
    fun `maps guest forbidden`() {
        val gatewayException = GatewayException.Forbidden(
            reason = GatewayErrorReason.GUEST_NOT_ALLOWED,
            message = "Guests cannot invite users",
        )
        val envelope = GatewayGraphqlErrorMapper.map(gatewayException)

        assertEquals(GatewayErrorCode.FORBIDDEN, envelope.code)
        assertEquals(GatewayErrorCategory.AUTHORIZATION.name.lowercase(), envelope.extensions["category"])
        assertEquals("GUEST_NOT_ALLOWED", envelope.extensions["reason"])
    }

    @Test
    fun `maps preparation scope session closed conflict reason`() {
        val gatewayException = GatewayException.Conflict(
            message = "closed",
            reason = GatewayErrorReason.PREPARATION_SCOPE_SESSION_CLOSED,
        )
        val envelope = GatewayGraphqlErrorMapper.map(gatewayException)

        assertEquals(GatewayErrorCode.CONFLICT, envelope.code)
        assertEquals("PREPARATION_SCOPE_SESSION_CLOSED", envelope.extensions["reason"])
    }

    @Test
    fun `maps kotlinx SerializationException to dependency unavailable`() {
        val envelope = GatewayGraphqlErrorMapper.map(
            SerializationException("missing field"),
        )

        assertEquals(GatewayErrorCode.DEPENDENCY_UNAVAILABLE, envelope.code)
        assertEquals("upstream", envelope.extensions["dependency"])
        assertEquals(true, envelope.extensions["retryable"])
        assertEquals(503, envelope.extensions["httpStatus"])
    }
}
