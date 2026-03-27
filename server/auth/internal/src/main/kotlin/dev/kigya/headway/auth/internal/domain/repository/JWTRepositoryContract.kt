package dev.kigya.headway.auth.internal.domain.repository

import java.util.Date
import java.util.UUID

internal data class GeneratedGuestAccessToken(
    val accessToken: String,
    val expiresAtEpochMs: Long,
    val sessionId: UUID,
)

internal interface JWTRepositoryContract {

    fun generateAccessToken(userUUID: UUID): String

    fun generateRefreshToken(
        userUUID: UUID,
        expirationDate: Date,
    ): String

    fun generateGuestAccessToken(): GeneratedGuestAccessToken

    fun isAccessTokenValid(token: String): Boolean

    fun isRefreshTokenValid(token: String): Boolean

    fun validateGuestAccessToken(token: String): GuestAccessTokenPayload?

    fun decodeTokenType(token: String): String?

    fun isUserAccessTokenExpiredByClaims(token: String): Boolean

    fun isGuestAccessTokenExpiredByClaims(token: String): Boolean

    fun getUserUUID(token: String): UUID?
}
