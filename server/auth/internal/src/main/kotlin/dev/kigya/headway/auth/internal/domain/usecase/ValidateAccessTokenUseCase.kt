package dev.kigya.headway.auth.internal.domain.usecase

import dev.kigya.headway.auth.api.AuthServicePlainText
import dev.kigya.headway.auth.api.model.out.AuthPrincipalType
import dev.kigya.headway.auth.api.model.out.AuthValidateTokenResponse
import dev.kigya.headway.auth.internal.domain.error.AuthException
import dev.kigya.headway.auth.internal.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.auth.internal.domain.repository.JWTRepositoryContract

internal class ValidateAccessTokenUseCase(
    private val jwtRepository: JWTRepositoryContract,
    private val databaseRepository: DatabaseRepositoryContract,
) {
    suspend operator fun invoke(accessToken: String): AuthValidateTokenResponse {
        val trimmedToken = accessToken.trim()
        if (trimmedToken.isBlank()) {
            throw AuthException.Unauthorized(AuthServicePlainText.INVALID_ACCESS_TOKEN)
        }
        if (jwtRepository.isAccessTokenValid(trimmedToken)) {
            return userPrincipal(trimmedToken)
        }
        return guestOrReject(token = trimmedToken)
    }

    private fun userPrincipal(token: String): AuthValidateTokenResponse {
        val userUuid = jwtRepository.getUserUUID(token)
            ?: throw AuthException.Unauthorized(AuthServicePlainText.INVALID_ACCESS_TOKEN)
        return AuthValidateTokenResponse(
            principalType = AuthPrincipalType.USER,
            userUuid = userUuid,
        )
    }

    private suspend fun guestOrReject(token: String): AuthValidateTokenResponse {
        val tokenType = jwtRepository.decodeTokenType(token)
        if (tokenType == TOKEN_TYPE_GUEST_ACCESS) {
            return guestPrincipal(token = token)
        }
        if (tokenType == TOKEN_TYPE_ACCESS && jwtRepository.isUserAccessTokenExpiredByClaims(token)) {
            throw AuthException.Unauthorized(AuthServicePlainText.ACCESS_TOKEN_EXPIRED)
        }
        throw AuthException.Unauthorized(AuthServicePlainText.INVALID_ACCESS_TOKEN)
    }

    private suspend fun guestPrincipal(token: String): AuthValidateTokenResponse {
        if (jwtRepository.isGuestAccessTokenExpiredByClaims(token)) {
            throw AuthException.Unauthorized(AuthServicePlainText.GUEST_TOKEN_EXPIRED)
        }
        val guestPayload = jwtRepository.validateGuestAccessToken(token)
            ?: throw AuthException.Unauthorized(AuthServicePlainText.INVALID_GUEST_TOKEN)
        databaseRepository.ensureGuestSessionActive(sessionId = guestPayload.sessionId)
        return AuthValidateTokenResponse(
            principalType = AuthPrincipalType.GUEST,
            guestSessionId = guestPayload.sessionId,
            scopes = guestPayload.scopes,
        )
    }
}

private const val TOKEN_TYPE_ACCESS = "access"

private const val TOKEN_TYPE_GUEST_ACCESS = "guest_access"
