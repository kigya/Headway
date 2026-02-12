package dev.kigya.headway.gateway.data.client

import dev.kigya.headway.auth.api.model.`in`.AuthGoogleLoginPayloadDto
import dev.kigya.headway.auth.api.model.`in`.AuthRefreshTokenPayloadDto
import dev.kigya.headway.auth.api.model.out.AuthGoogleLoginResponse
import dev.kigya.headway.auth.api.model.out.AuthRefreshAccessTokenResponse
import dev.kigya.headway.auth.api.model.resource.AuthGoogleResource
import dev.kigya.headway.auth.api.model.resource.AuthRefreshTokenResource
import dev.kigya.headway.gateway.core.http.upstreamCall
import dev.kigya.headway.gateway.domain.repository.AuthRepositoryContract
import dev.kigya.headway.gateway.mapping.toGateway
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody

internal class AuthRepository(
    private val httpClient: HttpClient,
) : AuthRepositoryContract {

    override suspend fun loginWithGoogle(idToken: String, fingerprint: String): GatewayGoogleLoginResponse =
        upstreamCall(
            dependency = "auth",
            request = {
                httpClient.post(AuthGoogleResource()) {
                    setBody(AuthGoogleLoginPayloadDto(idToken = idToken, fingerprint = fingerprint))
                }
            },
            onSuccess = { it.body<AuthGoogleLoginResponse>().toGateway() },
        )

    override suspend fun refreshToken(refreshToken: String, fingerprint: String): GatewayRefreshAccessTokenResponse =
        upstreamCall(
            dependency = "auth",
            request = {
                httpClient.post(AuthRefreshTokenResource()) {
                    setBody(AuthRefreshTokenPayloadDto(refreshToken = refreshToken, fingerprint = fingerprint))
                }
            },
            onSuccess = { it.body<AuthRefreshAccessTokenResponse>().toGateway() },
        )
}
