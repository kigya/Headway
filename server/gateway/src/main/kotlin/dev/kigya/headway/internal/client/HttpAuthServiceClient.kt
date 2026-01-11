package dev.kigya.headway.internal.client

import dev.kigya.headway.internal.config.ConfigurationValues
import dev.kigya.headway.internal.model.AuthServiceAuthResponse
import dev.kigya.headway.internal.model.AuthServiceRefreshTokenResponse
import dev.kigya.headway.internal.model.LoginRequest
import ext.successBodyOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class HttpAuthServiceClient(
    private val httpClient: HttpClient,
) : AuthServiceClientContract {

    override suspend fun loginWithGoogle(
        idToken: String,
        fingerprint: String,
    ): AuthServiceAuthResponse {
        val response = httpClient.post("${ConfigurationValues.AUTH_SERVICE_URL}/google") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(idToken, fingerprint))
        }
        return response.successBodyOrThrow()
    }

    override suspend fun refreshToken(
        refreshToken: String,
        fingerprint: String,
    ): AuthServiceRefreshTokenResponse {
        val response = httpClient.post("${ConfigurationValues.AUTH_SERVICE_URL}/refreshToken") {
            parameter("refresh_token", refreshToken)
            parameter("fingerprint", fingerprint)
        }
        return response.successBodyOrThrow()
    }
}
