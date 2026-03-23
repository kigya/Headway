package dev.kigya.headway.auth.internal.data.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.TokenExpiredException
import dev.kigya.headway.auth.internal.domain.repository.GuestAccessTokenPayload
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
    private val guestAlgorithm = Algorithm.HMAC256(config.guestSecret)

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

    override fun generateGuestAccessToken(): Pair<String, Long> {
        val sessionId = UUID.randomUUID()
        val now = Instant.now(clock)
        val expiresAt = now.plusSeconds(config.guestTtlSec)
        val token = JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withClaim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_GUEST_ACCESS)
            .withClaim(CLAIM_PRINCIPAL_TYPE, PRINCIPAL_TYPE_GUEST)
            .withClaim(CLAIM_GUEST_SESSION_ID, sessionId.toString())
            .withClaim(CLAIM_SCOPE, SCOPE_LEARN_GUEST)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(expiresAt))
            .sign(guestAlgorithm)
        return token to expiresAt.toEpochMilli()
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

    override fun validateGuestAccessToken(token: String): GuestAccessTokenPayload? = try {
        JWT.require(guestAlgorithm)
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withClaim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_GUEST_ACCESS)
            .withClaim(CLAIM_PRINCIPAL_TYPE, PRINCIPAL_TYPE_GUEST)
            .build()
            .verify(token)
        val decoded = JWT.decode(token)
        val scope = decoded.claims[CLAIM_SCOPE]?.asString() ?: SCOPE_LEARN_GUEST
        decoded.claims[CLAIM_GUEST_SESSION_ID]?.asString()
            ?.let { UUID.fromString(it) }
            ?.let { sid ->
                GuestAccessTokenPayload(sessionId = sid, scopes = listOf(scope))
            }
    } catch (_: TokenExpiredException) {
        null
    } catch (_: RuntimeException) {
        null
    }

    override fun decodeTokenType(token: String): String? = try {
        JWT.decode(token).getClaim(CLAIM_TOKEN_TYPE).asString()
    } catch (_: JWTDecodeException) {
        null
    } catch (_: RuntimeException) {
        null
    }

    override fun isUserAccessTokenExpiredByClaims(token: String): Boolean =
        isExpiredForType(token, TOKEN_TYPE_ACCESS)

    override fun isGuestAccessTokenExpiredByClaims(token: String): Boolean =
        isExpiredForType(token, TOKEN_TYPE_GUEST_ACCESS)

    override fun getUserUUID(token: String): UUID? = try {
        val uidClaim = JWT.decode(token).claims[CLAIM_USER_UUID]?.asString()
        uidClaim?.let(UUID::fromString)
    } catch (_: JWTDecodeException) {
        null
    } catch (_: Exception) {
        null
    }

    private fun isExpiredForType(
        token: String,
        expectedType: String,
    ): Boolean {
        val decoded = decodeTokenSafe(token) ?: return false
        if (decoded.getClaim(CLAIM_TOKEN_TYPE).asString() != expectedType) {
            return false
        }
        val exp = decoded.expiresAt ?: return false
        return exp.toInstant().isBefore(Instant.now(clock))
    }

    private fun decodeTokenSafe(token: String) = try {
        JWT.decode(token)
    } catch (_: JWTDecodeException) {
        null
    } catch (_: RuntimeException) {
        null
    }
}

private const val CLAIM_USER_UUID = "user_uuid"
private const val CLAIM_TOKEN_TYPE = "token_type"
private const val CLAIM_PRINCIPAL_TYPE = "principal_type"
private const val CLAIM_GUEST_SESSION_ID = "guest_session_id"
private const val CLAIM_SCOPE = "scope"
private const val TOKEN_TYPE_ACCESS = "access"
private const val TOKEN_TYPE_REFRESH = "refresh"
private const val TOKEN_TYPE_GUEST_ACCESS = "guest_access"
private const val PRINCIPAL_TYPE_GUEST = "guest"
private const val SCOPE_LEARN_GUEST = "learn_guest"
