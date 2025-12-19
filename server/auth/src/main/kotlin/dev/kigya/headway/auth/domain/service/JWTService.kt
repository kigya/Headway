package dev.kigya.headway.auth.domain.service

import java.util.Date
import java.util.UUID

internal interface JWTService {

    fun generateAccessToken(userUUID: UUID): String

    fun generateRefreshToken(userUUID: UUID, expirationDate: Date): String

    fun verifyAccessToken(token: String): Boolean

    fun verifyRefreshToken(token: String): Boolean

    fun getUserUUID(token: String): UUID?
}
