package dev.kigya.headway.gateway.core.http

import dev.kigya.headway.database.api.model.DatabasePreparationErrorCodes
import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpstreamHttpErrorMapperTest {

    @Test
    fun `conflict body scope closed maps preparation reason`() = runBlocking {
        val response = mockClient(
            status = HttpStatusCode.Conflict,
            body = DatabasePreparationErrorCodes.SCOPE_SESSION_CLOSED,
        ).get("http://test/")
        val ex = response.toGatewayException("database")
        assertTrue(ex is GatewayException.Conflict)
        assertEquals(GatewayErrorReason.PREPARATION_SCOPE_SESSION_CLOSED, ex.reason)
    }

    @Test
    fun `forbidden summary requires completed maps preparation reason`() = runBlocking {
        val response = mockClient(
            status = HttpStatusCode.Forbidden,
            body = DatabasePreparationErrorCodes.SUMMARY_REQUIRES_COMPLETED_SESSION,
        ).get("http://test/")
        val ex = response.toGatewayException("database")
        assertTrue(ex is GatewayException.Forbidden)
        assertEquals(GatewayErrorReason.PREPARATION_SUMMARY_REQUIRES_COMPLETED_SESSION, ex.reason)
    }

    @Test
    fun `conflict unknown body keeps identity conflict`() = runBlocking {
        val response = mockClient(
            status = HttpStatusCode.Conflict,
            body = "other",
        ).get("http://test/")
        val ex = response.toGatewayException("database")
        assertTrue(ex is GatewayException.Conflict)
        assertEquals(GatewayErrorReason.IDENTITY_CONFLICT, ex.reason)
    }

    @Test
    fun `forbidden unknown body keeps insufficient role`() = runBlocking {
        val response = mockClient(
            status = HttpStatusCode.Forbidden,
            body = "nope",
        ).get("http://test/")
        val ex = response.toGatewayException("database")
        assertTrue(ex is GatewayException.Forbidden)
        assertEquals(GatewayErrorReason.INSUFFICIENT_ROLE, ex.reason)
    }

    private fun mockClient(
        status: HttpStatusCode,
        body: String,
    ): HttpClient = HttpClient(
        engine = MockEngine { _ ->
            respond(
                content = body,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "text/plain"),
            )
        },
    ) {
        expectSuccess = false
    }
}
