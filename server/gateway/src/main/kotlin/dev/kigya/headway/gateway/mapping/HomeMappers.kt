package dev.kigya.headway.gateway.mapping

import dev.kigya.headway.gateway.graphql.GatewayAppLocale
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserRole
import dev.kigya.headway.gateway.model.HomeScreenNextInterviewType
import dev.kigya.headway.gateway.model.HomeScreenPayload
import dev.kigya.headway.gateway.model.HomeScreenSectionId
import dev.kigya.headway.gateway.model.HomeScreenSectionItem
import dev.kigya.headway.gateway.model.HomeScreenSectionStyle
import dev.kigya.headway.home.api.model.`in`.HomeAppLocaleDto
import dev.kigya.headway.home.api.model.`in`.HomeScreenRequestDto
import dev.kigya.headway.home.api.model.`in`.HomeUserRoleDto
import dev.kigya.headway.home.api.model.out.HomeScreenNextInterviewTypeDto
import dev.kigya.headway.home.api.model.out.HomeScreenResponseDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionIdDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionItemDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionStyleDto

internal fun GatewayUser.toHomeScreenRequest(locale: GatewayAppLocale): HomeScreenRequestDto =
    HomeScreenRequestDto(
        userId = id,
        userName = name,
        userRole = role.toHomeUserRoleDto(),
        locale = locale.toHomeAppLocaleDto(),
    )

internal fun HomeScreenResponseDto.toGatewayHomeScreenPayload(): HomeScreenPayload =
    HomeScreenPayload(
        dateLabel = dateLabel,
        greeting = greeting,
        roleLabel = roleLabel,
        readinessPercent = readinessPercent,
        nextInterviewType = nextInterviewType?.toGateway(),
        nextInterviewTypeLabel = nextInterviewTypeLabel,
        sections = sections.map { it.toGateway() },
    )

private fun HomeScreenSectionItemDto.toGateway(): HomeScreenSectionItem = HomeScreenSectionItem(
    id = id.toGateway(),
    title = title,
    style = style.toGateway(),
    iconUrl = iconUrl,
)

private fun HomeScreenSectionIdDto.toGateway(): HomeScreenSectionId = when (this) {
    HomeScreenSectionIdDto.HOME -> HomeScreenSectionId.HOME
    HomeScreenSectionIdDto.START_TRAINING_SESSION -> HomeScreenSectionId.START_TRAINING_SESSION
    HomeScreenSectionIdDto.LEARN_QUESTIONS -> HomeScreenSectionId.LEARN_QUESTIONS
    HomeScreenSectionIdDto.EMPLOYEE_PROGRESS_MANAGEMENT -> HomeScreenSectionId.EMPLOYEE_PROGRESS_MANAGEMENT
    HomeScreenSectionIdDto.PEOPLE_MANAGEMENT -> HomeScreenSectionId.PEOPLE_MANAGEMENT
    HomeScreenSectionIdDto.VIEW_STATISTICS -> HomeScreenSectionId.VIEW_STATISTICS
    HomeScreenSectionIdDto.ABOUT -> HomeScreenSectionId.ABOUT
    HomeScreenSectionIdDto.SIGN_OUT -> HomeScreenSectionId.SIGN_OUT
}

private fun HomeScreenSectionStyleDto.toGateway(): HomeScreenSectionStyle = when (this) {
    HomeScreenSectionStyleDto.FILLED_PRIMARY -> HomeScreenSectionStyle.FILLED_PRIMARY
    HomeScreenSectionStyleDto.OUTLINED_ACCENT -> HomeScreenSectionStyle.OUTLINED_ACCENT
    HomeScreenSectionStyleDto.DEFAULT -> HomeScreenSectionStyle.DEFAULT
}

private fun HomeScreenNextInterviewTypeDto.toGateway(): HomeScreenNextInterviewType = when (this) {
    HomeScreenNextInterviewTypeDto.CHECK -> HomeScreenNextInterviewType.CHECK
    HomeScreenNextInterviewTypeDto.SPOT -> HomeScreenNextInterviewType.SPOT
    HomeScreenNextInterviewTypeDto.MOCK -> HomeScreenNextInterviewType.MOCK
    HomeScreenNextInterviewTypeDto.SOFT_SKILLS -> HomeScreenNextInterviewType.SOFT_SKILLS
}

private fun GatewayAppLocale.toHomeAppLocaleDto(): HomeAppLocaleDto = when (this) {
    GatewayAppLocale.EN -> HomeAppLocaleDto.EN
    GatewayAppLocale.RU -> HomeAppLocaleDto.RU
}

private fun GatewayUserRole.toHomeUserRoleDto(): HomeUserRoleDto = when (this) {
    GatewayUserRole.DEVELOPER -> HomeUserRoleDto.DEVELOPER
    GatewayUserRole.MANAGER -> HomeUserRoleDto.MANAGER
    GatewayUserRole.MENTOR -> HomeUserRoleDto.MENTOR
    GatewayUserRole.EMPLOYEE -> HomeUserRoleDto.EMPLOYEE
    GatewayUserRole.GUEST -> HomeUserRoleDto.GUEST
}
