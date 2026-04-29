package dev.kigya.headway.gateway.data.repository

import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.home.api.model.`in`.HomeAppLocaleDto
import dev.kigya.headway.home.api.model.`in`.HomeScreenRequestDto
import dev.kigya.headway.home.api.model.`in`.HomeUserRoleDto
import dev.kigya.headway.home.api.model.out.HomeScreenResponseDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionIdDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionItemDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionStyleDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class HomeRepositoryTest {

    private val wireJson = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
    }

    private val sampleRequest = HomeScreenRequestDto(
        userId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
        userName = "U",
        userEmail = "u@example.com",
        userRole = HomeUserRoleDto.GUEST,
        locale = HomeAppLocaleDto.EN,
    )

    private val sampleResponseDto = HomeScreenResponseDto(
        dateLabel = "Sat, 1 Jan 2025",
        greeting = "Hi, U!",
        displayName = "U",
        roleLabel = null,
        avatarUrl = null,
        accessRole = HomeUserRoleDto.GUEST,
        readinessPercent = null,
        nextInterviewType = null,
        nextInterviewTypeLabel = null,
        sections = listOf(
            HomeScreenSectionItemDto(
                id = HomeScreenSectionIdDto.HOME,
                title = "Home",
                style = HomeScreenSectionStyleDto.DEFAULT,
                iconUrl = "https://i/h.svg",
            ),
        ),
    )

    @Test
    fun `returns body on success`() {
        val engine = MockEngine { request ->
            assertScreenPost(request)
            respond(
                content = wireJson.encodeToString(sampleResponseDto),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = homeGatewayHttpClient(engine)
        val repo = HomeRepository(httpClient = client)
        val result = runBlocking { repo.getHomeScreen(sampleRequest) }
        assertEquals(sampleResponseDto, result)
    }

    @Test
    fun `maps bad request to invalid gateway exception`() {
        val engine = MockEngine { request ->
            assertScreenPost(request)
            respondError(HttpStatusCode.BadRequest)
        }
        val repo = HomeRepository(httpClient = homeGatewayHttpClient(engine))
        val invalidRequestException = assertFailsWith<GatewayException.InvalidRequest> {
            runBlocking { repo.getHomeScreen(sampleRequest) }
        }
        assertEquals(GatewayErrorReason.BAD_REQUEST, invalidRequestException.reason)
    }

    @Test
    fun `maps internal error to upstream protocol`() {
        val engine = MockEngine { request ->
            assertScreenPost(request)
            respondError(HttpStatusCode.InternalServerError)
        }
        val repo = HomeRepository(httpClient = homeGatewayHttpClient(engine))
        val upstreamProtocolException = assertFailsWith<GatewayException.UpstreamProtocol> {
            runBlocking { repo.getHomeScreen(sampleRequest) }
        }
        assertEquals("home", upstreamProtocolException.dependency)
        assertEquals(500, upstreamProtocolException.upstreamStatus)
    }

    @Test
    fun `maps transport failure to dependency unavailable`() {
        val engine = MockEngine { throw IOException("network") }
        val repo = HomeRepository(httpClient = homeGatewayHttpClient(engine))
        val dependencyUnavailableException = assertFailsWith<GatewayException.DependencyUnavailable> {
            runBlocking { repo.getHomeScreen(sampleRequest) }
        }
        assertEquals("home", dependencyUnavailableException.dependency)
        assertTrue(dependencyUnavailableException.cause is IOException)
    }

    private fun assertScreenPost(request: HttpRequestData) {
        assertEquals(HttpMethod.Post, request.method)
        assertTrue(request.url.encodedPath.endsWith("/screen"))
    }

    private fun homeGatewayHttpClient(engine: MockEngine): HttpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = false
                    ignoreUnknownKeys = true
                },
            )
        }
        install(Resources)
        install(HttpTimeout)
        defaultRequest {
            url {
                protocol = URLProtocol.HTTP
                host = "home"
                port = 8084
                val basePath = "/internal/v1/home".trim('/')
                val reqPath = encodedPath.ifBlank { "/" }
                val joined = basePath.trimEnd('/') + "/" + reqPath.trimStart('/')
                encodedPath = joined.replace(Regex("/{2,}"), "/")
            }
        }
    }
}
