package dev.kigya.headway.home.internal.domain.usecase

import dev.kigya.headway.home.api.model.`in`.HomeAppLocaleDto
import dev.kigya.headway.home.api.model.`in`.HomeScreenRequestDto
import dev.kigya.headway.home.api.model.`in`.HomeUserRoleDto
import dev.kigya.headway.home.api.model.out.HomeScreenNextInterviewTypeDto
import dev.kigya.headway.home.api.model.out.HomeScreenResponseDto
import java.time.Clock
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

internal class GetHomeScreenUseCase(
    private val clock: Clock,
    private val nextInterviewTypePicker: HomeScreenNextInterviewTypePicker,
    private val sectionsBuilder: HomeScreenSectionsBuilder,
) {
    operator fun invoke(request: HomeScreenRequestDto): HomeScreenResponseDto {
        val jvmLocale = toJvmLocale(request.locale)
        val dateLabel = formatDateLabel(jvmLocale)
        val trimmedName = request.userName.trim()
        val greeting = "${HomeScreenCopy.greetingPrefix(request.locale)} $trimmedName!"
        val roleLabel = HomeScreenCopy.roleLabel(request.userRole, request.locale)
        val readinessAndNext = readinessAndNextInterview(
            role = request.userRole,
            locale = request.locale,
        )
        val sections = sectionsBuilder.sectionsForRole(
            role = request.userRole,
            locale = request.locale,
        )

        return HomeScreenResponseDto(
            dateLabel = dateLabel,
            greeting = greeting,
            roleLabel = roleLabel,
            readinessPercent = readinessAndNext.first,
            nextInterviewType = readinessAndNext.second,
            nextInterviewTypeLabel = readinessAndNext.third,
            sections = sections,
        )
    }

    private fun toJvmLocale(locale: HomeAppLocaleDto): Locale = when (locale) {
        HomeAppLocaleDto.EN -> Locale.ENGLISH
        HomeAppLocaleDto.RU -> Locale.forLanguageTag("ru")
    }

    private fun formatDateLabel(jvmLocale: Locale): String {
        val zoned = clock.instant().atZone(CET_ZONE)
        return DateTimeFormatter.ofPattern(DATE_LABEL_PATTERN, jvmLocale).format(zoned)
    }

    private fun readinessAndNextInterview(
        role: HomeUserRoleDto,
        locale: HomeAppLocaleDto,
    ): Triple<Int?, HomeScreenNextInterviewTypeDto?, String?> = when (role) {
        HomeUserRoleDto.MENTOR,
        HomeUserRoleDto.EMPLOYEE,
        -> {
            val picked = nextInterviewTypePicker.pick()
            Triple(
                READINESS_STUB_PERCENT,
                picked,
                HomeScreenCopy.nextInterviewLabel(picked, locale),
            )
        }

        HomeUserRoleDto.MANAGER,
        HomeUserRoleDto.DEVELOPER,
        HomeUserRoleDto.GUEST,
        -> Triple(null, null, null)
    }

    private companion object {
        val CET_ZONE: ZoneId = ZoneId.of("CET")
        const val DATE_LABEL_PATTERN: String = "EEE, d MMM yyyy"
        const val READINESS_STUB_PERCENT: Int = 80
    }
}
