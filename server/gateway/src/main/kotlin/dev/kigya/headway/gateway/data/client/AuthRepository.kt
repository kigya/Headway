package dev.kigya.headway.gateway.data.client

import dev.kigya.headway.auth.api.model.`in`.AuthGoogleLoginPayloadDto
import dev.kigya.headway.auth.api.model.`in`.AuthRefreshTokenPayloadDto
import dev.kigya.headway.auth.api.model.out.AuthGoogleLoginResponse
import dev.kigya.headway.auth.api.model.out.AuthRefreshAccessTokenResponse
import dev.kigya.headway.auth.api.model.resource.AuthGoogleResource
import dev.kigya.headway.auth.api.model.resource.AuthRefreshTokenResource
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.repository.AuthRepositoryContract
import dev.kigya.headway.gateway.mapping.toGateway
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

internal class AuthRepository(
    private val httpClient: HttpClient,
) : AuthRepositoryContract {

    override suspend fun loginWithGoogle(idToken: String, fingerprint: String): GatewayGoogleLoginResponse {
        val response = try {
            httpClient.post(AuthGoogleResource()) {
                setBody(AuthGoogleLoginPayloadDto(idToken = idToken, fingerprint = fingerprint))
            }
        } catch (t: Throwable) {
            throw GatewayException.DependencyUnavailable("auth", cause = t)
        }

        if (response.status.isSuccess()) return response.body<AuthGoogleLoginResponse>().toGateway()
        throw response.toGatewayException(dependency = "auth")
    }

    override suspend fun refreshToken(refreshToken: String, fingerprint: String): GatewayRefreshAccessTokenResponse {
        val response = try {
            httpClient.post(AuthRefreshTokenResource()) {
                setBody(AuthRefreshTokenPayloadDto(refreshToken = refreshToken, fingerprint = fingerprint))
            }
        } catch (t: Throwable) {
            throw GatewayException.DependencyUnavailable("auth", cause = t)
        }

        if (response.status.isSuccess()) return response.body<AuthRefreshAccessTokenResponse>().toGateway()
        throw response.toGatewayException(dependency = "auth")
    }
}

private suspend fun HttpResponse.toGatewayException(dependency: String): GatewayException {
    val msg = runCatching { bodyAsText() }.getOrNull()?.trim().orEmpty()
    val safeMsg = msg.takeIf { it.isNotBlank() }

    return when (status) {
        HttpStatusCode.BadRequest ->
            GatewayException.InvalidRequest(safeMsg ?: "Bad request")

        HttpStatusCode.Unauthorized ->
            GatewayException.Unauthorized(safeMsg ?: "Unauthorized")

        HttpStatusCode.Forbidden ->
            GatewayException.Forbidden(safeMsg ?: "Forbidden")

        HttpStatusCode.ServiceUnavailable ->
            GatewayException.DependencyUnavailable(
                dependency,
                message = safeMsg ?: "Dependency unavailable: $dependency"
            )

        else ->
            GatewayException.UpstreamProtocol(
                dependency = dependency,
                status = status.value,
                message = safeMsg ?: "Unexpected response from $dependency: ${status.value}"
            )
    }
}
