package dev.kigya.headway.home.internal.domain.usecase

import dev.kigya.headway.home.api.model.`in`.HomeAppLocaleDto
import dev.kigya.headway.home.api.model.`in`.HomeScreenRequestDto
import dev.kigya.headway.home.api.model.`in`.HomeUserRoleDto
import dev.kigya.headway.home.api.model.out.HomeScreenNextInterviewTypeDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionIdDto
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class GetHomeScreenUseCaseTest {

    @Test
    fun `guest receives only home section with icon url`() {
        val useCase = buildUseCase()
        val result = useCase(
            HomeScreenRequestDto(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
                userName = "Guest",
                userRole = HomeUserRoleDto.GUEST,
                locale = HomeAppLocaleDto.EN,
            ),
        )

        assertEquals(1, result.sections.size)
        assertEquals(HomeScreenSectionIdDto.HOME, result.sections.first().id)
        assertEquals(
            "https://icons.test/base/home_action_home.svg",
            result.sections.first().iconUrl,
        )
    }

    @Test
    fun `developer receives home section first`() {
        val useCase = buildUseCase()
        val result = useCase(
            HomeScreenRequestDto(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000002"),
                userName = "Dev",
                userRole = HomeUserRoleDto.DEVELOPER,
                locale = HomeAppLocaleDto.EN,
            ),
        )

        assertEquals(HomeScreenSectionIdDto.HOME, result.sections.first().id)
        assertEquals(8, result.sections.size)
    }

    @Test
    fun `employee receives readiness and next interview`() {
        val useCase = buildUseCase()
        val result = useCase(
            HomeScreenRequestDto(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000003"),
                userName = "Emp",
                userRole = HomeUserRoleDto.EMPLOYEE,
                locale = HomeAppLocaleDto.EN,
            ),
        )

        assertEquals(80, result.readinessPercent)
        assertEquals(HomeScreenNextInterviewTypeDto.MOCK, result.nextInterviewType)
        assertEquals("Mock", result.nextInterviewTypeLabel)
    }

    private fun buildUseCase(): GetHomeScreenUseCase = GetHomeScreenUseCase(
        clock = Clock.fixed(
            Instant.parse("2025-01-25T12:00:00Z"),
            ZoneOffset.UTC,
        ),
        nextInterviewTypePicker = HomeScreenNextInterviewTypePicker { HomeScreenNextInterviewTypeDto.MOCK },
        sectionsBuilder = HomeScreenSectionsBuilder("https://icons.test/base/"),
    )
}
