package dev.kigya.headway.auth.data.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.TokenExpiredException
import dev.kigya.headway.auth.domain.service.JWTService
import java.util.Date
import java.util.UUID

private const val CLAIM_USER_UUID = "user_uuid"

internal class JWTServiceImpl : JWTService {
    private val accessSecret = "secret"

    private val refreshSecret = "refresh secret"

    override fun generateAccessToken(userUUID: UUID): String {
        return JWT.create()
            .withClaim(CLAIM_USER_UUID, userUUID.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 5))
            .sign(Algorithm.HMAC256(accessSecret))
    }

    override fun generateRefreshToken(userUUID: UUID, expirationDate: Date): String {
        return JWT.create()
            .withClaim(CLAIM_USER_UUID, userUUID.toString())
            .withExpiresAt(expirationDate)
            .sign(Algorithm.HMAC256(refreshSecret))
    }

    override fun verifyAccessToken(token: String): Boolean {
        return try {
            JWT.require(Algorithm.HMAC256(accessSecret))
                .build()
                .verify(token)

            true
        } catch (_: TokenExpiredException) {
            false
        } catch (_: RuntimeException) {
            false
        }
    }

    override fun verifyRefreshToken(token: String): Boolean {
        return try {
            JWT.require(Algorithm.HMAC256(refreshSecret))
                .build()
                .verify(token)

            true
        } catch (_: TokenExpiredException) {
            false
        } catch (_: RuntimeException) {
            false
        }
    }

    override fun getUserUUID(token: String): UUID? {
        return try {
            val uidClaim = JWT.decode(token).claims[CLAIM_USER_UUID]?.asString()

            uidClaim?.let(UUID::fromString)
        } catch (_: JWTDecodeException) {
            null
        } catch (_: Exception) {
            null
        }
    }
}
