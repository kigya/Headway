package dev.kigya.headway.gateway.data.repository

import dev.kigya.headway.gateway.core.http.upstreamCall
import dev.kigya.headway.gateway.domain.repository.HomeRepositoryContract
import dev.kigya.headway.home.api.model.`in`.HomeScreenRequestDto
import dev.kigya.headway.home.api.model.out.HomeScreenResponseDto
import dev.kigya.headway.home.api.model.resource.HomeResource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class HomeRepository(
    private val httpClient: HttpClient,
) : HomeRepositoryContract {

    override suspend fun getHomeScreen(request: HomeScreenRequestDto): HomeScreenResponseDto =
        upstreamCall(
            dependency = "home",
            request = {
                httpClient.post(HomeResource.Screen()) {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }
            },
            onSuccess = { it.body<HomeScreenResponseDto>() },
        )
}
