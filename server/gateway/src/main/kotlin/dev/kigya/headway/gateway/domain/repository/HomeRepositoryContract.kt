package dev.kigya.headway.gateway.domain.repository

import dev.kigya.headway.home.api.model.`in`.HomeScreenRequestDto
import dev.kigya.headway.home.api.model.out.HomeScreenResponseDto

internal interface HomeRepositoryContract {
    suspend fun getHomeScreen(request: HomeScreenRequestDto): HomeScreenResponseDto
}
