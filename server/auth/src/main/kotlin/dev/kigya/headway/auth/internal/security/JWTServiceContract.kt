package dev.kigya.headway.auth.internal.security

import java.util.Date
import java.util.UUID

internal interface JWTServiceContract {

    fun generateAccessToken(userUUID: UUID): String

    fun generateRefreshToken(userUUID: UUID, expirationDate: Date): String

    fun verifyAccessToken(token: String): Boolean

    fun verifyRefreshToken(token: String): Boolean

    fun getUserUUID(token: String): UUID?
}
