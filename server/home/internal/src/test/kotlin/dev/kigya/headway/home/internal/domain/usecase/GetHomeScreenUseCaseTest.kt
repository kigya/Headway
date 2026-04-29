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
                userEmail = "guest@example.com",
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
        assertEquals("Guest", result.displayName)
        assertEquals(HomeUserRoleDto.GUEST, result.accessRole)
    }

    @Test
    fun `developer receives home section first`() {
        val useCase = buildUseCase()
        val result = useCase(
            HomeScreenRequestDto(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000002"),
                userName = "Dev",
                userEmail = "dev@example.com",
                userRole = HomeUserRoleDto.DEVELOPER,
                locale = HomeAppLocaleDto.EN,
            ),
        )

        assertEquals(HomeScreenSectionIdDto.HOME, result.sections.first().id)
        assertEquals(8, result.sections.size)
        assertEquals(expectedDateLabel(Locale.ENGLISH), result.dateLabel)
        assertEquals("Hi, Dev!", result.greeting)
        assertEquals("Dev", result.displayName)
        assertEquals("Developer", result.roleLabel)
        assertEquals(HomeUserRoleDto.DEVELOPER, result.accessRole)
        assertNull(result.readinessPercent)
    }

    @Test
    fun `russian locale uses russian greeting role and date`() {
        val useCase = buildUseCase()
        val result = useCase(
            HomeScreenRequestDto(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000002"),
                userName = "Иван",
                userEmail = "ivan@example.com",
                userRole = HomeUserRoleDto.DEVELOPER,
                locale = HomeAppLocaleDto.RU,
            ),
        )

        assertEquals("Привет, Иван!", result.greeting)
        assertEquals("Иван", result.displayName)
        assertEquals("Разработчик", result.roleLabel)
        assertEquals(HomeUserRoleDto.DEVELOPER, result.accessRole)
        assertEquals(expectedDateLabel(Locale.forLanguageTag("ru")), result.dateLabel)
    }

    @Test
    fun `mentor receives readiness and next interview`() {
        val useCase = buildUseCase()
        val result = useCase(
            HomeScreenRequestDto(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000004"),
                userName = "Mentor",
                userEmail = "mentor@example.com",
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
                userEmail = "bob@example.com",
                userRole = HomeUserRoleDto.DEVELOPER,
                locale = HomeAppLocaleDto.EN,
            ),
        )

        assertEquals("Hi, Bob!", result.greeting)
        assertEquals("Bob", result.displayName)
    }

    @Test
    fun `employee receives readiness and next interview`() {
        val useCase = buildUseCase()
        val result = useCase(
            HomeScreenRequestDto(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000003"),
                userName = "Emp",
                userEmail = "emp@example.com",
                userRole = HomeUserRoleDto.EMPLOYEE,
                locale = HomeAppLocaleDto.EN,
            ),
        )

        assertEquals(80, result.readinessPercent)
        assertEquals(HomeScreenNextInterviewTypeDto.MOCK, result.nextInterviewType)
        assertEquals("Mock", result.nextInterviewTypeLabel)
    }

    @Test
    fun `blank trimmed user name falls back to email local part`() {
        val useCase = buildUseCase()
        val result = useCase(
            HomeScreenRequestDto(
                userId = UUID.fromString("00000000-0000-0000-0000-000000000008"),
                userName = "   ",
                userEmail = "alice@example.org",
                userRole = HomeUserRoleDto.DEVELOPER,
                locale = HomeAppLocaleDto.EN,
            ),
        )

        assertEquals("alice", result.displayName)
        assertEquals("Hi, alice!", result.greeting)
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
