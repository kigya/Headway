package dev.kigya.headway.auth.internal.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.TokenExpiredException
import java.util.Date
import java.util.UUID

private const val CLAIM_USER_UUID = "user_uuid"

internal class JWTServiceImpl : JWTServiceContract {
    private val accessSecret = "secret"
    private val refreshSecret = "refresh secret"

    override fun generateAccessToken(userUUID: UUID): String = JWT.create()
        .withClaim(CLAIM_USER_UUID, userUUID.toString())
        .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 5))
        .sign(Algorithm.HMAC256(accessSecret))

    override fun generateRefreshToken(userUUID: UUID, expirationDate: Date): String = JWT.create()
        .withClaim(CLAIM_USER_UUID, userUUID.toString())
        .withExpiresAt(expirationDate)
        .sign(Algorithm.HMAC256(refreshSecret))

    override fun verifyAccessToken(token: String): Boolean = try {
        JWT.require(Algorithm.HMAC256(accessSecret))
            .build()
            .verify(token)
        true
    } catch (_: TokenExpiredException) {
        false
    } catch (_: RuntimeException) {
        false
    }

    override fun verifyRefreshToken(token: String): Boolean = try {
        JWT.require(Algorithm.HMAC256(refreshSecret))
            .build()
            .verify(token)
        true
    } catch (_: TokenExpiredException) {
        false
    } catch (_: RuntimeException) {
        false
    }

    override fun getUserUUID(token: String): UUID? = try {
        val uidClaim = JWT.decode(token).claims[CLAIM_USER_UUID]?.asString()
        uidClaim?.let(UUID::fromString)
    } catch (_: JWTDecodeException) {
        null
    } catch (_: Exception) {
        null
    }
}
