package dev.kigya.headway.gateway.data.repository

import dev.kigya.headway.auth.api.AuthServicePlainText
import dev.kigya.headway.auth.api.model.`in`.AuthGoogleLoginPayloadDto
import dev.kigya.headway.auth.api.model.`in`.AuthRefreshTokenPayloadDto
import dev.kigya.headway.auth.api.model.`in`.AuthValidateTokenPayloadDto
import dev.kigya.headway.auth.api.model.out.AuthGoogleLoginResponse
import dev.kigya.headway.auth.api.model.out.AuthGuestLoginResponse
import dev.kigya.headway.auth.api.model.out.AuthRefreshAccessTokenResponse
import dev.kigya.headway.auth.api.model.out.AuthValidateTokenResponse
import dev.kigya.headway.auth.api.model.resource.AuthGoogleResource
import dev.kigya.headway.auth.api.model.resource.AuthGuestResource
import dev.kigya.headway.auth.api.model.resource.AuthRefreshTokenResource
import dev.kigya.headway.auth.api.model.resource.AuthValidateTokenResource
import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.core.http.toGatewayException
import dev.kigya.headway.gateway.core.http.upstreamCall
import dev.kigya.headway.gateway.domain.repository.AuthRepositoryContract
import dev.kigya.headway.gateway.mapping.toDatabase
import dev.kigya.headway.gateway.mapping.toGateway
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayGuestLoginResponse
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import dev.kigya.headway.gateway.model.GatewaySessionPlatform
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess

internal class AuthRepository(
    private val httpClient: HttpClient,
) : AuthRepositoryContract {

    private val dependencyName = "auth"

    override suspend fun loginWithGoogle(
        idToken: String,
        fingerprint: String,
        platform: GatewaySessionPlatform,
    ): GatewayGoogleLoginResponse {
        val response = try {
            httpClient.post(AuthGoogleResource()) {
                contentType(ContentType.Application.Json)
                setBody(
                    AuthGoogleLoginPayloadDto(
                        idToken = idToken,
                        fingerprint = fingerprint,
                        platform = platform.toDatabase(),
                    ),
                )
            }
        } catch (t: Throwable) {
            throw GatewayException.DependencyUnavailable(dependency = dependencyName, cause = t)
        }

        if (response.status.isSuccess()) {
            return response.body<AuthGoogleLoginResponse>().toGateway()
        }

        if (response.status == HttpStatusCode.Forbidden) {
            val body = response.bodyAsText().trim()
            when {
                body.contains("not invited", ignoreCase = true) -> throw GatewayException.InvitationRequired()
                body.contains("not verified", ignoreCase = true) ->
                    throw GatewayException.Forbidden(
                        reason = GatewayErrorReason.GOOGLE_EMAIL_NOT_VERIFIED,
                        message = "Google email is not verified",
                    )

                else -> throw GatewayException.Forbidden(
                    reason = GatewayErrorReason.INSUFFICIENT_ROLE,
                    message = body.ifBlank { "Forbidden" },
                )
            }
        }

        if (response.status == HttpStatusCode.Conflict) {
            throw GatewayException.Conflict(
                message = "Identity could not be linked to this account",
            )
        }

        throw response.toGatewayException(dependency = dependencyName)
    }

    override suspend fun loginAsGuest(): GatewayGuestLoginResponse = upstreamCall(
        dependency = dependencyName,
        request = {
            httpClient.post(AuthGuestResource()) {
                contentType(ContentType.Application.Json)
            }
        },
        onSuccess = { it.body<AuthGuestLoginResponse>().toGateway() },
    )

    override suspend fun refreshToken(
        refreshToken: String,
        fingerprint: String,
    ): GatewayRefreshAccessTokenResponse {
        val response = try {
            httpClient.post(AuthRefreshTokenResource()) {
                contentType(ContentType.Application.Json)
                setBody(AuthRefreshTokenPayloadDto(refreshToken = refreshToken, fingerprint = fingerprint))
            }
        } catch (t: Throwable) {
            throw GatewayException.DependencyUnavailable(dependency = dependencyName, cause = t)
        }

        if (response.status.isSuccess()) {
            return response.body<AuthRefreshAccessTokenResponse>().toGateway()
        }

        if (response.status == HttpStatusCode.Unauthorized) {
            throw GatewayException.Unauthorized(
                reason = GatewayErrorReason.INVALID_REFRESH_TOKEN,
                message = response.bodyAsText().trim().ifBlank { AuthServicePlainText.INVALID_REFRESH_TOKEN },
            )
        }

        throw response.toGatewayException(dependency = dependencyName)
    }

    override suspend fun validateToken(accessToken: String): AuthValidateTokenResponse {
        val response = try {
            httpClient.post(AuthValidateTokenResource()) {
                contentType(ContentType.Application.Json)
                setBody(AuthValidateTokenPayloadDto(accessToken = accessToken))
            }
        } catch (t: Throwable) {
            throw GatewayException.DependencyUnavailable(dependency = dependencyName, cause = t)
        }

        if (response.status.isSuccess()) {
            return response.body<AuthValidateTokenResponse>()
        }

        if (response.status == HttpStatusCode.Unauthorized) {
            throw unauthorizedFromAuthBody(response.bodyAsText().trim())
        }

        throw response.toGatewayException(dependency = dependencyName)
    }

    private fun unauthorizedFromAuthBody(body: String): GatewayException.Unauthorized =
        when (body) {
            AuthServicePlainText.GUEST_TOKEN_EXPIRED -> GatewayException.Unauthorized(
                reason = GatewayErrorReason.GUEST_TOKEN_EXPIRED,
                message = "Guest session expired. Request a new guest token.",
            )

            AuthServicePlainText.ACCESS_TOKEN_EXPIRED -> GatewayException.Unauthorized(
                reason = GatewayErrorReason.ACCESS_TOKEN_EXPIRED,
                message = "Session expired. Refresh your access token.",
            )

            AuthServicePlainText.INVALID_GUEST_TOKEN -> GatewayException.Unauthorized(
                reason = GatewayErrorReason.INVALID_GUEST_TOKEN,
                message = "Guest token is invalid.",
            )

            else -> GatewayException.Unauthorized(
                reason = GatewayErrorReason.INVALID_ACCESS_TOKEN,
                message = body.ifBlank { AuthServicePlainText.INVALID_ACCESS_TOKEN },
            )
        }
}
