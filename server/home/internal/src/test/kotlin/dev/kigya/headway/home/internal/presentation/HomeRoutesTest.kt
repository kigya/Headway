package dev.kigya.headway.home.internal.presentation

import dev.kigya.headway.home.api.model.`in`.HomeAppLocaleDto
import dev.kigya.headway.home.api.model.`in`.HomeScreenRequestDto
import dev.kigya.headway.home.api.model.`in`.HomeUserRoleDto
import dev.kigya.headway.home.api.model.out.HomeScreenNextInterviewTypeDto
import dev.kigya.headway.home.internal.domain.usecase.GetHomeScreenUseCase
import dev.kigya.headway.home.internal.domain.usecase.HomeScreenNextInterviewTypePicker
import dev.kigya.headway.home.internal.domain.usecase.HomeScreenSectionsBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HomeRoutesTest {

    private val json = Json { encodeDefaults = true }

    @Test
    fun `healthz returns ok`() = testApplication {
        application {
            installHomeApi(getHomeScreen = testGetHomeScreenUseCase())
        }

        val response = client.get("/internal/v1/home/healthz")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("OK", response.bodyAsText())
    }

    @Test
    fun `post screen returns payload`() = testApplication {
        application {
            installHomeApi(getHomeScreen = testGetHomeScreenUseCase())
        }

        val body = HomeScreenRequestDto(
            userId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
            userName = "Tester",
            userRole = HomeUserRoleDto.GUEST,
            locale = HomeAppLocaleDto.EN,
        )
        val response = client.post("/internal/v1/home/screen") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(body))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val text = response.bodyAsText()
        assertTrue(text.contains("date_label"))
        assertTrue(text.contains("Hi, Tester!"))
    }

    @Test
    fun `post screen rejects blank user name`() = testApplication {
        application {
            installHomeApi(getHomeScreen = testGetHomeScreenUseCase())
        }

        val body = HomeScreenRequestDto(
            userId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
            userName = "   ",
            userRole = HomeUserRoleDto.GUEST,
            locale = HomeAppLocaleDto.EN,
        )
        val response = client.post("/internal/v1/home/screen") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(body))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    private fun testGetHomeScreenUseCase(): GetHomeScreenUseCase = GetHomeScreenUseCase(
        clock = Clock.fixed(
            Instant.parse("2025-01-25T12:00:00Z"),
            ZoneOffset.UTC,
        ),
        nextInterviewTypePicker = HomeScreenNextInterviewTypePicker { HomeScreenNextInterviewTypeDto.MOCK },
        sectionsBuilder = HomeScreenSectionsBuilder("https://icons.test/base/"),
    )
}
