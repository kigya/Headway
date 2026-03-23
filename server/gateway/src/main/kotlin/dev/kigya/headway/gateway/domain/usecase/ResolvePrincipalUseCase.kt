package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.auth.api.AuthServicePlainText
import dev.kigya.headway.auth.api.model.out.AuthPrincipalType
import dev.kigya.headway.auth.api.model.out.AuthValidateTokenResponse
import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.repository.AuthRepositoryContract
import dev.kigya.headway.gateway.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.gateway.model.GatewayPrincipal

internal class ResolvePrincipalUseCase(
    private val authRepository: AuthRepositoryContract,
    private val databaseRepository: DatabaseRepositoryContract,
) {
    suspend operator fun invoke(authorizationHeader: String?): GatewayPrincipal {
        val rawToken = requireBearerRawToken(authorizationHeader)
        val authResponse = authRepository.validateToken(rawToken)
        return toPrincipal(authResponse)
    }

    private fun requireBearerRawToken(authorizationHeader: String?): String {
        if (authorizationHeader.isNullOrBlank()) {
            throw GatewayException.Unauthorized(
                reason = GatewayErrorReason.MISSING_AUTH_HEADER,
                message = "Authorization header missing",
            )
        }
        val trimmedHeader = authorizationHeader.trim()
        if (!trimmedHeader.startsWith(BEARER_PREFIX, ignoreCase = true)) {
            throw GatewayException.Unauthorized(
                reason = GatewayErrorReason.MALFORMED_BEARER_HEADER,
                message = "Authorization must use Bearer scheme",
            )
        }
        val rawToken = trimmedHeader.substring(BEARER_PREFIX.length).trim()
        if (rawToken.isBlank()) {
            throw GatewayException.Unauthorized(
                reason = GatewayErrorReason.MALFORMED_BEARER_HEADER,
                message = "Bearer token is blank",
            )
        }
        return rawToken
    }

    private suspend fun toPrincipal(authResponse: AuthValidateTokenResponse): GatewayPrincipal =
        when (authResponse.principalType) {
            AuthPrincipalType.USER -> {
                val uuid = authResponse.userUuid
                    ?: throw GatewayException.Unauthorized(
                        reason = GatewayErrorReason.INVALID_ACCESS_TOKEN,
                        message = AuthServicePlainText.INVALID_ACCESS_TOKEN,
                    )
                GatewayPrincipal.User(databaseRepository.getUserById(uuid))
            }

            AuthPrincipalType.GUEST -> {
                val sessionId = authResponse.guestSessionId
                    ?: throw GatewayException.Unauthorized(
                        reason = GatewayErrorReason.INVALID_GUEST_TOKEN,
                        message = AuthServicePlainText.INVALID_GUEST_TOKEN,
                    )
                GatewayPrincipal.Guest(
                    guestSessionId = sessionId,
                    scopes = authResponse.scopes,
                )
            }
        }

    private companion object {
        const val BEARER_PREFIX: String = "Bearer "
    }
}
