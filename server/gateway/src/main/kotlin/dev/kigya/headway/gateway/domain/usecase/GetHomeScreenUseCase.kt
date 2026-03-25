package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.domain.repository.HomeRepositoryContract
import dev.kigya.headway.gateway.graphql.GatewayAppLocale
import dev.kigya.headway.gateway.mapping.toGatewayHomeScreenPayload
import dev.kigya.headway.gateway.mapping.toHomeScreenRequest
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.HomeScreenPayload

internal class GetHomeScreenUseCase(
    private val homeRepository: HomeRepositoryContract,
) {
    suspend operator fun invoke(
        user: GatewayUser,
        locale: GatewayAppLocale,
    ): HomeScreenPayload {
        val request = user.toHomeScreenRequest(locale)
        return homeRepository.getHomeScreen(request).toGatewayHomeScreenPayload()
    }
}
