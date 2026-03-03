package dev.kigya.headway.auth.internal.data.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.TokenExpiredException
import dev.kigya.headway.auth.internal.domain.repository.JWTRepositoryContract
import java.time.Clock
import java.time.Instant
import java.util.Date
import java.util.UUID

internal class JWTRepository(
    private val config: JwtConfig,
    private val clock: Clock,
) : JWTRepositoryContract {

    private val accessAlgorithm = Algorithm.HMAC256(config.accessSecret)
    private val refreshAlgorithm = Algorithm.HMAC256(config.refreshSecret)

    override fun generateAccessToken(userUUID: UUID): String {
        val now = Instant.now(clock)
        val expiresAt = now.plusSeconds(config.accessTtlSec)

        return JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withClaim(CLAIM_USER_UUID, userUUID.toString())
            .withClaim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(expiresAt))
            .sign(accessAlgorithm)
    }

    override fun generateRefreshToken(
        userUUID: UUID,
        expirationDate: Date,
    ): String {
        val now = Instant.now(clock)

        return JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withClaim(CLAIM_USER_UUID, userUUID.toString())
            .withClaim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(expirationDate)
            .sign(refreshAlgorithm)
    }

    override fun isAccessTokenValid(token: String): Boolean = try {
        JWT.require(accessAlgorithm)
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withClaim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS)
            .build()
            .verify(token)
        true
    } catch (_: TokenExpiredException) {
        false
    } catch (_: RuntimeException) {
        false
    }

    override fun isRefreshTokenValid(token: String): Boolean = try {
        JWT.require(refreshAlgorithm)
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withClaim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH)
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

private const val CLAIM_USER_UUID = "user_uuid"
private const val CLAIM_TOKEN_TYPE = "token_type"
private const val TOKEN_TYPE_ACCESS = "access"
private const val TOKEN_TYPE_REFRESH = "refresh"
