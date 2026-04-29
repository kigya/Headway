package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.domain.repository.HomeRepositoryContract
import dev.kigya.headway.gateway.graphql.GatewayAppLocale
import dev.kigya.headway.gateway.mapping.toGatewayHomeScreenPayload
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserRole
import dev.kigya.headway.home.api.model.`in`.HomeAppLocaleDto
import dev.kigya.headway.home.api.model.`in`.HomeScreenRequestDto
import dev.kigya.headway.home.api.model.`in`.HomeUserRoleDto
import dev.kigya.headway.home.api.model.out.HomeScreenResponseDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionIdDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionItemDto
import dev.kigya.headway.home.api.model.out.HomeScreenSectionStyleDto
import kotlinx.coroutines.runBlocking
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class GatewayGetHomeScreenUseCaseTest {

    @Test
    fun `invokes repository with mapped request and returns mapped payload`() {
        val userId = UUID.fromString("11111111-2222-3333-4444-555555555555")
        val user = GatewayUser(
            id = userId,
            email = "a@b.c",
            name = "Alex",
            role = GatewayUserRole.MENTOR,
        )
        val upstream = HomeScreenResponseDto(
            dateLabel = "dl",
            greeting = "gr",
            displayName = "Alex",
            roleLabel = "Mentor",
            avatarUrl = null,
            accessRole = HomeUserRoleDto.MENTOR,
            readinessPercent = 80,
            nextInterviewType = null,
            nextInterviewTypeLabel = null,
            sections = listOf(
                HomeScreenSectionItemDto(
                    id = HomeScreenSectionIdDto.HOME,
                    title = "Home",
                    style = HomeScreenSectionStyleDto.DEFAULT,
                    iconUrl = "https://x/h.svg",
                ),
            ),
        )
        lateinit var seen: HomeScreenRequestDto
        val repo = object : HomeRepositoryContract {
            override suspend fun getHomeScreen(
                request: HomeScreenRequestDto,
            ): HomeScreenResponseDto {
                seen = request
                return upstream
            }
        }
        val useCase = GetHomeScreenUseCase(homeRepository = repo)
        val payload = runBlocking { useCase(user, GatewayAppLocale.RU) }
        assertEquals(
            HomeScreenRequestDto(
                userId = userId,
                userName = "Alex",
                userEmail = "a@b.c",
                userRole = HomeUserRoleDto.MENTOR,
                locale = HomeAppLocaleDto.RU,
            ),
            seen,
        )
        assertEquals(upstream.toGatewayHomeScreenPayload(), payload)
    }
}
