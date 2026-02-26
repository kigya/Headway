package dev.kigya.headway.auth.internal.domain.repository

import java.util.Date
import java.util.UUID

internal interface JWTRepositoryContract {

    fun generateAccessToken(userUUID: UUID): String

    fun generateRefreshToken(userUUID: UUID, expirationDate: Date): String

    fun isAccessTokenValid(token: String): Boolean

    fun isRefreshTokenValid(token: String): Boolean

    fun getUserUUID(token: String): UUID?
}
