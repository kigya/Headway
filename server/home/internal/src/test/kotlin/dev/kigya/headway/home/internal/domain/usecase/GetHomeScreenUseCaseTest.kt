package dev.kigya.headway.home.internal.domain.usecase

import dev.kigya.headway.home.api.model.`in`.HomeAppLocaleDto
import dev.kigya.headway.home.api.model.`in`.HomeScreenRequestDto
import dev.kigya.headway.home.api.model.`in`.HomeUserRoleDto
import dev.kigya.headway.home.api.model.out.HomeScreenNextInterviewTypeDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionIdDto
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetHomeScreenUseCaseTest {

    private val fixedClock: Clock = Clock.fixed(
        Instant.parse("2025-01-25T12:00:00Z"),
        ZoneOffset.UTC,
    )

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
        assertNull(result.readinessPercent)
        assertNull(result.nextInterviewType)
        assertNull(result.nextInterviewTypeLabel)
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
        assertEquals(expectedDateLabel(Locale.ENGLISH), result.dateLabel)
        assertEquals("Hi, Dev!", result.greeting)
        assertEquals("Developer", result.roleLabel)
        assertNull(result.readinessPercent)
    }

    @Test
    fun `russian locale uses russian greeting role and date`() {
        val useCase = buildUseCase()
        val result = useCase(
            HomeScreenRequestDto(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000002"),
                userName = "Иван",
                userRole = HomeUserRoleDto.DEVELOPER,
                locale = HomeAppLocaleDto.RU,
            ),
        )

        assertEquals("Привет, Иван!", result.greeting)
        assertEquals("Разработчик", result.roleLabel)
        assertEquals(expectedDateLabel(Locale.forLanguageTag("ru")), result.dateLabel)
    }

    @Test
    fun `mentor receives readiness and next interview`() {
        val useCase = buildUseCase()
        val result = useCase(
            HomeScreenRequestDto(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000004"),
                userName = "Mentor",
                userRole = HomeUserRoleDto.MENTOR,
                locale = HomeAppLocaleDto.EN,
            ),
        )

        assertEquals(80, result.readinessPercent)
        assertEquals(HomeScreenNextInterviewTypeDto.MOCK, result.nextInterviewType)
        assertEquals("Mock", result.nextInterviewTypeLabel)
        assertEquals(7, result.sections.size)
    }

    @Test
    fun `user name is trimmed in greeting`() {
        val useCase = buildUseCase()
        val result = useCase(
            HomeScreenRequestDto(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000005"),
                userName = "  Bob  ",
                userRole = HomeUserRoleDto.DEVELOPER,
                locale = HomeAppLocaleDto.EN,
            ),
        )

        assertEquals("Hi, Bob!", result.greeting)
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

    private fun expectedDateLabel(locale: Locale): String {
        val zoned = fixedClock.instant().atZone(CET_ZONE)
        return DateTimeFormatter.ofPattern(DATE_LABEL_PATTERN, locale).format(zoned)
    }

    private fun buildUseCase(): GetHomeScreenUseCase = GetHomeScreenUseCase(
        clock = fixedClock,
        nextInterviewTypePicker = HomeScreenNextInterviewTypePicker { HomeScreenNextInterviewTypeDto.MOCK },
        sectionsBuilder = HomeScreenSectionsBuilder("https://icons.test/base/"),
    )

    private companion object {
        val CET_ZONE: ZoneId = ZoneId.of("CET")
        const val DATE_LABEL_PATTERN: String = "EEE, d MMM yyyy"
    }
}
