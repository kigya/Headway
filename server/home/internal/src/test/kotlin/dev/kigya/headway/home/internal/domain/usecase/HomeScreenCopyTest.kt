package dev.kigya.headway.home.internal.domain.usecase

import dev.kigya.headway.home.api.model.`in`.HomeAppLocaleDto
import dev.kigya.headway.home.api.model.`in`.HomeUserRoleDto
import dev.kigya.headway.home.api.model.out.HomeScreenNextInterviewTypeDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionIdDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HomeScreenCopyTest {

    @Test
    fun `greeting prefix locales`() {
        assertEquals("Hi,", HomeScreenCopy.greetingPrefix(HomeAppLocaleDto.EN))
        assertEquals("Привет,", HomeScreenCopy.greetingPrefix(HomeAppLocaleDto.RU))
    }

    @Test
    fun `role labels for manager mentor developer`() {
        assertEquals("Manager", HomeScreenCopy.roleLabel(HomeUserRoleDto.MANAGER, HomeAppLocaleDto.EN))
        assertEquals("Менеджер", HomeScreenCopy.roleLabel(HomeUserRoleDto.MANAGER, HomeAppLocaleDto.RU))
        assertEquals("Mentor", HomeScreenCopy.roleLabel(HomeUserRoleDto.MENTOR, HomeAppLocaleDto.EN))
        assertEquals("Ментор", HomeScreenCopy.roleLabel(HomeUserRoleDto.MENTOR, HomeAppLocaleDto.RU))
        assertEquals("Developer", HomeScreenCopy.roleLabel(HomeUserRoleDto.DEVELOPER, HomeAppLocaleDto.EN))
        assertEquals("Разработчик", HomeScreenCopy.roleLabel(HomeUserRoleDto.DEVELOPER, HomeAppLocaleDto.RU))
    }

    @Test
    fun `role label null for employee and guest`() {
        assertNull(HomeScreenCopy.roleLabel(HomeUserRoleDto.EMPLOYEE, HomeAppLocaleDto.EN))
        assertNull(HomeScreenCopy.roleLabel(HomeUserRoleDto.GUEST, HomeAppLocaleDto.RU))
    }

    @Test
    fun `section titles cover all ids in english`() {
        val expected = mapOf(
            HomeScreenSectionIdDto.HOME to "Home",
            HomeScreenSectionIdDto.START_TRAINING_SESSION to "Start training session",
            HomeScreenSectionIdDto.LEARN_QUESTIONS to "Learn Questions",
            HomeScreenSectionIdDto.EMPLOYEE_PROGRESS_MANAGEMENT to "Employee Progress Management",
            HomeScreenSectionIdDto.PEOPLE_MANAGEMENT to "People Management",
            HomeScreenSectionIdDto.VIEW_STATISTICS to "View Statistics",
            HomeScreenSectionIdDto.ABOUT to "About",
            HomeScreenSectionIdDto.SIGN_OUT to "Sign Out",
        )
        HomeScreenSectionIdDto.entries.forEach { id ->
            assertEquals(
                expected.getValue(id),
                HomeScreenCopy.sectionTitle(id, HomeAppLocaleDto.EN),
                "id=$id",
            )
        }
    }

    @Test
    fun `section titles cover all ids in russian`() {
        val expected = mapOf(
            HomeScreenSectionIdDto.HOME to "Главная",
            HomeScreenSectionIdDto.START_TRAINING_SESSION to "Начать тренировочную сессию",
            HomeScreenSectionIdDto.LEARN_QUESTIONS to "Изучить вопросы",
            HomeScreenSectionIdDto.EMPLOYEE_PROGRESS_MANAGEMENT to "Управление прогрессом сотрудников",
            HomeScreenSectionIdDto.PEOPLE_MANAGEMENT to "Управление людьми",
            HomeScreenSectionIdDto.VIEW_STATISTICS to "Статистика",
            HomeScreenSectionIdDto.ABOUT to "О приложении",
            HomeScreenSectionIdDto.SIGN_OUT to "Выйти",
        )
        HomeScreenSectionIdDto.entries.forEach { id ->
            assertEquals(
                expected.getValue(id),
                HomeScreenCopy.sectionTitle(id, HomeAppLocaleDto.RU),
                "id=$id",
            )
        }
    }

    @Test
    fun `next interview labels cover all types`() {
        val en = mapOf(
            HomeScreenNextInterviewTypeDto.CHECK to "Check",
            HomeScreenNextInterviewTypeDto.SPOT to "Spot",
            HomeScreenNextInterviewTypeDto.MOCK to "Mock",
            HomeScreenNextInterviewTypeDto.SOFT_SKILLS to "Soft Skills",
        )
        val ru = mapOf(
            HomeScreenNextInterviewTypeDto.CHECK to "Проверка",
            HomeScreenNextInterviewTypeDto.SPOT to "Спот",
            HomeScreenNextInterviewTypeDto.MOCK to "Мок",
            HomeScreenNextInterviewTypeDto.SOFT_SKILLS to "Мягкие навыки",
        )
        HomeScreenNextInterviewTypeDto.entries.forEach { type ->
            assertEquals(en.getValue(type), HomeScreenCopy.nextInterviewLabel(type, HomeAppLocaleDto.EN))
            assertEquals(ru.getValue(type), HomeScreenCopy.nextInterviewLabel(type, HomeAppLocaleDto.RU))
        }
    }
}
