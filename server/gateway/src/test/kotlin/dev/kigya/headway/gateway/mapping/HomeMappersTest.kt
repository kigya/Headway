package dev.kigya.headway.gateway.mapping

import dev.kigya.headway.gateway.graphql.GatewayAppLocale
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserRole
import dev.kigya.headway.home.api.model.out.HomeScreenNextInterviewTypeDto
import dev.kigya.headway.home.api.model.out.HomeScreenResponseDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionIdDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionItemDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionStyleDto
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeMappersTest {

    private val sampleUserId: UUID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")

    @Test
    fun `maps gateway user and locale to home request for all roles and locales`() {
        GatewayUserRole.entries.forEach { role ->
            GatewayAppLocale.entries.forEach { locale ->
                val user = GatewayUser(
                    id = sampleUserId,
                    email = "e@x.com",
                    name = "N",
                    role = role,
                )
                val request = user.toHomeScreenRequest(locale)
                assertEquals(sampleUserId, request.userId)
                assertEquals("N", request.userName)
                assertEquals(role.name, request.userRole.name)
                assertEquals(locale.name, request.locale.name)
            }
        }
    }

    @Test
    fun `maps home response to gateway payload preserving enum names and icon urls`() {
        val styles = listOf(
            HomeScreenSectionStyleDto.FILLED_PRIMARY,
            HomeScreenSectionStyleDto.OUTLINED_ACCENT,
            HomeScreenSectionStyleDto.DEFAULT,
        )
        val sections = HomeScreenSectionIdDto.entries.mapIndexed { index, id ->
            HomeScreenSectionItemDto(
                id = id,
                title = "title-$id",
                style = styles[index % styles.size],
                iconUrl = "https://icons.example/$id.svg",
            )
        }
        val dto = HomeScreenResponseDto(
            dateLabel = "d",
            greeting = "g",
            roleLabel = "r",
            readinessPercent = 1,
            nextInterviewType = HomeScreenNextInterviewTypeDto.SPOT,
            nextInterviewTypeLabel = "Spot",
            sections = sections,
        )
        val payload = dto.toGatewayHomeScreenPayload()
        assertEquals("d", payload.dateLabel)
        assertEquals("g", payload.greeting)
        assertEquals("r", payload.roleLabel)
        assertEquals(1, payload.readinessPercent)
        assertEquals("SPOT", payload.nextInterviewType?.name)
        assertEquals("Spot", payload.nextInterviewTypeLabel)
        assertEquals(sections.size, payload.sections.size)
        sections.zip(payload.sections).forEach { (inItem, outItem) ->
            assertEquals(inItem.title, outItem.title)
            assertEquals(inItem.iconUrl, outItem.iconUrl)
            assertEquals(inItem.id.name, outItem.id.name)
            assertEquals(inItem.style.name, outItem.style.name)
        }
    }

    @Test
    fun `maps all next interview types by name`() {
        HomeScreenNextInterviewTypeDto.entries.forEach { dtoType ->
            val dto = HomeScreenResponseDto(
                dateLabel = "",
                greeting = "",
                roleLabel = null,
                readinessPercent = null,
                nextInterviewType = dtoType,
                nextInterviewTypeLabel = "L",
                sections = emptyList(),
            )
            assertEquals(dtoType.name, dto.toGatewayHomeScreenPayload().nextInterviewType?.name)
        }
    }
}
