package dev.kigya.headway.gateway.client

import dev.kigya.headway.gateway.internal.config.ConfigurationValues
import dev.kigya.headway.gateway.internal.exception.AuthServiceException
import dev.kigya.headway.gateway.internal.exception.GatewayErrorCode
import dev.kigya.headway.gateway.internal.model.AuthServiceAuthResponse
import dev.kigya.headway.gateway.internal.model.AuthServiceErrorResponse
import dev.kigya.headway.gateway.internal.model.AuthServiceRefreshTokenResponse
import dev.kigya.headway.gateway.internal.model.LoginRequest
import dev.kigya.headway.gateway.internal.model.RefreshTokenRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

internal class HttpAuthServiceClient(
    private val httpClient: HttpClient,
) : AuthServiceClientContract {

    override suspend fun loginWithGoogle(idToken: String, fingerprint: String): AuthServiceAuthResponse {
        val response = httpClient.post("${ConfigurationValues.AUTH_SERVICE_URL}/google") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(idToken, fingerprint))
        }

        if (response.status.isSuccess()) return response.body()

        throw response.toAuthServiceException()
    }

    override suspend fun refreshToken(refreshToken: String, fingerprint: String): AuthServiceRefreshTokenResponse {
        val response = httpClient.post("${ConfigurationValues.AUTH_SERVICE_URL}/refreshToken") {
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenRequest(refreshToken, fingerprint))
        }

        if (response.status.isSuccess()) return response.body()

        throw response.toAuthServiceException()
    }
}

private suspend fun HttpResponse.toAuthServiceException(): AuthServiceException {
    val payload = runCatching { body<AuthServiceErrorResponse>() }.getOrNull()

    val code = when (payload?.code) {
        "BAD_REQUEST" -> GatewayErrorCode.BAD_REQUEST
        "UNAUTHORIZED" -> GatewayErrorCode.UNAUTHORIZED
        "FORBIDDEN" -> GatewayErrorCode.FORBIDDEN
        "DEPENDENCY_UNAVAILABLE" -> GatewayErrorCode.DEPENDENCY_UNAVAILABLE
        "INTERNAL" -> GatewayErrorCode.DEPENDENCY_UNAVAILABLE
        else -> GatewayErrorCode.DEPENDENCY_UNAVAILABLE
    }

    return AuthServiceException(code = code)
}
