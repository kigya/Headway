package dev.kigya.headway.home.internal.domain.usecase

import dev.kigya.headway.home.api.model.`in`.HomeAppLocaleDto
import dev.kigya.headway.home.api.model.`in`.HomeUserRoleDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionIdDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HomeScreenSectionsBuilderTest {

    @Test
    fun `guest has single home section`() {
        val builder = HomeScreenSectionsBuilder("https://icons.test/base")
        val sections = builder.sectionsForRole(HomeUserRoleDto.GUEST, HomeAppLocaleDto.EN)
        assertEquals(1, sections.size)
        assertEquals(HomeScreenSectionIdDto.HOME, sections.first().id)
        assertEquals(
            "https://icons.test/base/home_action_home.svg",
            sections.first().iconUrl,
        )
    }

    @Test
    fun `developer has home first and eight sections total`() {
        val builder = HomeScreenSectionsBuilder("https://icons.test/base/")
        val sections = builder.sectionsForRole(HomeUserRoleDto.DEVELOPER, HomeAppLocaleDto.EN)
        assertEquals(8, sections.size)
        assertEquals(HomeScreenSectionIdDto.HOME, sections.first().id)
        assertEquals(
            "https://icons.test/base/home_action_start_session.svg",
            sections[1].iconUrl,
        )
    }

    @Test
    fun `manager matches developer section count`() {
        val builder = HomeScreenSectionsBuilder("https://x/")
        val dev = builder.sectionsForRole(HomeUserRoleDto.DEVELOPER, HomeAppLocaleDto.EN).map { it.id }
        val mgr = builder.sectionsForRole(HomeUserRoleDto.MANAGER, HomeAppLocaleDto.EN).map { it.id }
        assertEquals(dev, mgr)
    }

    @Test
    fun `mentor has seven sections and no people management`() {
        val builder = HomeScreenSectionsBuilder("https://x/")
        val sections = builder.sectionsForRole(HomeUserRoleDto.MENTOR, HomeAppLocaleDto.EN)
        assertEquals(7, sections.size)
        assertEquals(HomeScreenSectionIdDto.HOME, sections.first().id)
        val ids = sections.map { it.id }.toSet()
        assertFalse(ids.contains(HomeScreenSectionIdDto.PEOPLE_MANAGEMENT))
        assertTrue(ids.contains(HomeScreenSectionIdDto.EMPLOYEE_PROGRESS_MANAGEMENT))
    }

    @Test
    fun `employee has five sections`() {
        val builder = HomeScreenSectionsBuilder("https://x/")
        val sections = builder.sectionsForRole(HomeUserRoleDto.EMPLOYEE, HomeAppLocaleDto.EN)
        assertEquals(5, sections.size)
        assertEquals(HomeScreenSectionIdDto.HOME, sections.first().id)
        assertFalse(sections.drop(1).any { it.id == HomeScreenSectionIdDto.START_TRAINING_SESSION })
    }

    @Test
    fun `icon base url trims trailing slash consistently`() {
        val a = HomeScreenSectionsBuilder("https://host/a/").sectionsForRole(
            HomeUserRoleDto.GUEST,
            HomeAppLocaleDto.EN,
        ).first().iconUrl
        val b = HomeScreenSectionsBuilder("https://host/a").sectionsForRole(
            HomeUserRoleDto.GUEST,
            HomeAppLocaleDto.EN,
        ).first().iconUrl
        assertEquals(a, b)
    }

    @Test
    fun `russian home section title`() {
        val builder = HomeScreenSectionsBuilder("https://x/")
        val home = builder.sectionsForRole(HomeUserRoleDto.GUEST, HomeAppLocaleDto.RU).first()
        assertEquals("Главная", home.title)
    }
}
