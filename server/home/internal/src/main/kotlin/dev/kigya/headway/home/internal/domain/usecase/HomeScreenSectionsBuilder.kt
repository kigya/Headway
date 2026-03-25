package dev.kigya.headway.home.internal.domain.usecase

import dev.kigya.headway.home.api.model.`in`.HomeAppLocaleDto
import dev.kigya.headway.home.api.model.`in`.HomeUserRoleDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionIdDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionItemDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionStyleDto

internal class HomeScreenSectionsBuilder(
    private val iconsPublicBaseUrl: String,
) {
    fun sectionsForRole(
        role: HomeUserRoleDto,
        locale: HomeAppLocaleDto,
    ): List<HomeScreenSectionItemDto> {
        val homeSection = section(
            locale = locale,
            id = HomeScreenSectionIdDto.HOME,
            style = HomeScreenSectionStyleDto.DEFAULT,
        )
        val rest = roleSections(role, locale)
        return listOf(homeSection) + rest
    }

    private fun roleSections(
        role: HomeUserRoleDto,
        locale: HomeAppLocaleDto,
    ): List<HomeScreenSectionItemDto> = when (role) {
        HomeUserRoleDto.MANAGER,
        HomeUserRoleDto.DEVELOPER,
        -> managerOrDeveloperSections(locale)

        HomeUserRoleDto.MENTOR -> mentorSections(locale)
        HomeUserRoleDto.EMPLOYEE -> employeeSections(locale)
        HomeUserRoleDto.GUEST -> emptyList()
    }

    private fun managerOrDeveloperSections(
        locale: HomeAppLocaleDto,
    ): List<HomeScreenSectionItemDto> = listOf(
        section(locale, HomeScreenSectionIdDto.START_TRAINING_SESSION, HomeScreenSectionStyleDto.FILLED_PRIMARY),
        section(locale, HomeScreenSectionIdDto.LEARN_QUESTIONS, HomeScreenSectionStyleDto.OUTLINED_ACCENT),
        section(locale, HomeScreenSectionIdDto.EMPLOYEE_PROGRESS_MANAGEMENT, HomeScreenSectionStyleDto.DEFAULT),
        section(locale, HomeScreenSectionIdDto.PEOPLE_MANAGEMENT, HomeScreenSectionStyleDto.DEFAULT),
        section(locale, HomeScreenSectionIdDto.VIEW_STATISTICS, HomeScreenSectionStyleDto.DEFAULT),
        section(locale, HomeScreenSectionIdDto.ABOUT, HomeScreenSectionStyleDto.DEFAULT),
        section(locale, HomeScreenSectionIdDto.SIGN_OUT, HomeScreenSectionStyleDto.DEFAULT),
    )

    private fun mentorSections(locale: HomeAppLocaleDto): List<HomeScreenSectionItemDto> = listOf(
        section(locale, HomeScreenSectionIdDto.START_TRAINING_SESSION, HomeScreenSectionStyleDto.FILLED_PRIMARY),
        section(locale, HomeScreenSectionIdDto.LEARN_QUESTIONS, HomeScreenSectionStyleDto.OUTLINED_ACCENT),
        section(locale, HomeScreenSectionIdDto.EMPLOYEE_PROGRESS_MANAGEMENT, HomeScreenSectionStyleDto.DEFAULT),
        section(locale, HomeScreenSectionIdDto.VIEW_STATISTICS, HomeScreenSectionStyleDto.DEFAULT),
        section(locale, HomeScreenSectionIdDto.ABOUT, HomeScreenSectionStyleDto.DEFAULT),
        section(locale, HomeScreenSectionIdDto.SIGN_OUT, HomeScreenSectionStyleDto.DEFAULT),
    )

    private fun employeeSections(locale: HomeAppLocaleDto): List<HomeScreenSectionItemDto> = listOf(
        section(locale, HomeScreenSectionIdDto.LEARN_QUESTIONS, HomeScreenSectionStyleDto.OUTLINED_ACCENT),
        section(locale, HomeScreenSectionIdDto.VIEW_STATISTICS, HomeScreenSectionStyleDto.DEFAULT),
        section(locale, HomeScreenSectionIdDto.ABOUT, HomeScreenSectionStyleDto.DEFAULT),
        section(locale, HomeScreenSectionIdDto.SIGN_OUT, HomeScreenSectionStyleDto.DEFAULT),
    )

    private fun section(
        locale: HomeAppLocaleDto,
        id: HomeScreenSectionIdDto,
        style: HomeScreenSectionStyleDto,
    ): HomeScreenSectionItemDto = HomeScreenSectionItemDto(
        id = id,
        title = HomeScreenCopy.sectionTitle(id, locale),
        style = style,
        iconUrl = iconUrl(id),
    )

    private fun iconUrl(id: HomeScreenSectionIdDto): String {
        val base = iconsPublicBaseUrl.trimEnd('/')
        val objectName = iconObjectName(id)
        return "$base/$objectName"
    }

    private fun iconObjectName(id: HomeScreenSectionIdDto): String = when (id) {
        HomeScreenSectionIdDto.HOME -> "home_action_home.svg"
        HomeScreenSectionIdDto.START_TRAINING_SESSION -> "home_action_start_session.svg"
        HomeScreenSectionIdDto.LEARN_QUESTIONS -> "home_action_learn_questions.svg"
        HomeScreenSectionIdDto.EMPLOYEE_PROGRESS_MANAGEMENT -> "home_action_employee_progress_management.svg"
        HomeScreenSectionIdDto.PEOPLE_MANAGEMENT -> "home_action_people_management.svg"
        HomeScreenSectionIdDto.VIEW_STATISTICS -> "home_action_view_statistics.svg"
        HomeScreenSectionIdDto.ABOUT -> "home_action_about.svg"
        HomeScreenSectionIdDto.SIGN_OUT -> "home_action_sign_out.svg"
    }
}
