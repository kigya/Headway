package dev.kigya.headway.auth.internal.data.jwt

import dev.kigya.headway.auth.internal.domain.repository.JWTRepositoryContract
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JWTRepositoryTest {

    private val config = JwtConfig(
        issuer = "test-issuer",
        audience = "test-audience",
        accessSecret = "access-secret-test",
        refreshSecret = "refresh-secret-test",
        guestSecret = "guest-secret-test",
        accessTtlSec = 3600L,
        guestTtlSec = 120L,
    )

    @Test
    fun `guest token validates and carries session and scope`() {
        val clock = Clock.systemUTC()
        val jwt: JWTRepositoryContract = JWTRepository(config = config, clock = clock)
        val issued = jwt.generateGuestAccessToken()
        val token = issued.accessToken
        assertTrue(issued.expiresAtEpochMs > clock.instant().toEpochMilli())

        val payload = jwt.validateGuestAccessToken(token)
        assertNotNull(payload)
        assertTrue(payload.scopes.contains("learn_guest"))

        val responseType = jwt.decodeTokenType(token)
        assertEquals("guest_access", responseType)
    }

    @Test
    fun `user access token is not valid as guest`() {
        val clock = Clock.systemUTC()
        val jwt: JWTRepositoryContract = JWTRepository(config = config, clock = clock)
        val userId = UUID.fromString("00000000-0000-0000-0000-0000000000aa")
        val access = jwt.generateAccessToken(userId)
        assertNull(jwt.validateGuestAccessToken(access))
    }

    @Test
    fun `guest token is not valid as user access`() {
        val clock = Clock.systemUTC()
        val jwt: JWTRepositoryContract = JWTRepository(config = config, clock = clock)
        val guestToken = jwt.generateGuestAccessToken().accessToken
        assertFalse(jwt.isAccessTokenValid(guestToken))
    }

    @Test
    fun `expired user access detected by claims`() {
        val pastClock = Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneOffset.UTC)
        val pastJwt = JWTRepository(config = config, clock = pastClock)
        val userId = UUID.fromString("00000000-0000-0000-0000-0000000000bb")
        val oldToken = pastJwt.generateAccessToken(userId)
        val reader = JWTRepository(config = config, clock = Clock.systemUTC())
        assertTrue(reader.isUserAccessTokenExpiredByClaims(oldToken))
    }
}
