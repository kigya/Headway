package dev.kigya.headway.home.internal.domain.usecase

import dev.kigya.headway.home.api.model.`in`.HomeAppLocaleDto
import dev.kigya.headway.home.api.model.`in`.HomeUserRoleDto
import dev.kigya.headway.home.api.model.out.HomeScreenNextInterviewTypeDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionIdDto

internal object HomeScreenCopy {
    fun greetingPrefix(locale: HomeAppLocaleDto): String = when (locale) {
        HomeAppLocaleDto.EN -> "Hi,"
        HomeAppLocaleDto.RU -> "Привет,"
    }

    fun roleLabel(
        role: HomeUserRoleDto,
        locale: HomeAppLocaleDto,
    ): String? = when (role) {
        HomeUserRoleDto.MANAGER -> when (locale) {
            HomeAppLocaleDto.EN -> "Manager"
            HomeAppLocaleDto.RU -> "Менеджер"
        }

        HomeUserRoleDto.MENTOR -> when (locale) {
            HomeAppLocaleDto.EN -> "Mentor"
            HomeAppLocaleDto.RU -> "Ментор"
        }

        HomeUserRoleDto.DEVELOPER -> when (locale) {
            HomeAppLocaleDto.EN -> "Developer"
            HomeAppLocaleDto.RU -> "Разработчик"
        }

        HomeUserRoleDto.EMPLOYEE,
        HomeUserRoleDto.GUEST,
        -> null
    }

    fun sectionTitle(
        id: HomeScreenSectionIdDto,
        locale: HomeAppLocaleDto,
    ): String = when (locale) {
        HomeAppLocaleDto.EN -> sectionTitleEn(id)
        HomeAppLocaleDto.RU -> sectionTitleRu(id)
    }

    fun nextInterviewLabel(
        type: HomeScreenNextInterviewTypeDto,
        locale: HomeAppLocaleDto,
    ): String = when (locale) {
        HomeAppLocaleDto.EN -> nextInterviewEn(type)
        HomeAppLocaleDto.RU -> nextInterviewRu(type)
    }

    private fun sectionTitleEn(id: HomeScreenSectionIdDto): String = when (id) {
        HomeScreenSectionIdDto.HOME -> "Home"
        HomeScreenSectionIdDto.START_TRAINING_SESSION -> "Start training session"
        HomeScreenSectionIdDto.LEARN_QUESTIONS -> "Learn Questions"
        HomeScreenSectionIdDto.EMPLOYEE_PROGRESS_MANAGEMENT -> "Employee Progress Management"
        HomeScreenSectionIdDto.PEOPLE_MANAGEMENT -> "People Management"
        HomeScreenSectionIdDto.VIEW_STATISTICS -> "View Statistics"
        HomeScreenSectionIdDto.ABOUT -> "About"
        HomeScreenSectionIdDto.SIGN_OUT -> "Sign Out"
    }

    private fun sectionTitleRu(id: HomeScreenSectionIdDto): String = when (id) {
        HomeScreenSectionIdDto.HOME -> "Главная"
        HomeScreenSectionIdDto.START_TRAINING_SESSION -> "Начать тренировочную сессию"
        HomeScreenSectionIdDto.LEARN_QUESTIONS -> "Изучить вопросы"
        HomeScreenSectionIdDto.EMPLOYEE_PROGRESS_MANAGEMENT -> "Управление прогрессом сотрудников"
        HomeScreenSectionIdDto.PEOPLE_MANAGEMENT -> "Управление людьми"
        HomeScreenSectionIdDto.VIEW_STATISTICS -> "Статистика"
        HomeScreenSectionIdDto.ABOUT -> "О приложении"
        HomeScreenSectionIdDto.SIGN_OUT -> "Выйти"
    }

    private fun nextInterviewEn(type: HomeScreenNextInterviewTypeDto): String = when (type) {
        HomeScreenNextInterviewTypeDto.CHECK -> "Check"
        HomeScreenNextInterviewTypeDto.SPOT -> "Spot"
        HomeScreenNextInterviewTypeDto.MOCK -> "Mock"
        HomeScreenNextInterviewTypeDto.SOFT_SKILLS -> "Soft Skills"
    }

    private fun nextInterviewRu(type: HomeScreenNextInterviewTypeDto): String = when (type) {
        HomeScreenNextInterviewTypeDto.CHECK -> "Проверка"
        HomeScreenNextInterviewTypeDto.SPOT -> "Спот"
        HomeScreenNextInterviewTypeDto.MOCK -> "Мок"
        HomeScreenNextInterviewTypeDto.SOFT_SKILLS -> "Мягкие навыки"
    }
}
