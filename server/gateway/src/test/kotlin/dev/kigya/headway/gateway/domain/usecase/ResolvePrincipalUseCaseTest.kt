package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.auth.api.model.out.AuthPrincipalType
import dev.kigya.headway.auth.api.model.out.AuthValidateTokenResponse
import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.repository.AuthRepositoryContract
import dev.kigya.headway.gateway.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayGuestLoginResponse
import dev.kigya.headway.gateway.model.GatewayPrincipal
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import dev.kigya.headway.gateway.model.GatewaySessionPlatform
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserDepartment
import dev.kigya.headway.gateway.model.GatewayUserRole
import kotlinx.coroutines.runBlocking
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ResolvePrincipalUseCaseTest {
    private val callerId = UUID.fromString("00000000-0000-0000-0000-000000000123")
    private val caller = GatewayUser(
        id = callerId,
        email = "developer@headway.test",
        name = "Developer",
        role = GatewayUserRole.DEVELOPER,
        department = GatewayUserDepartment.ANDROID,
    )

    @Test
    fun `returns user principal when bearer token is valid`() = runBlocking {
        val useCase = ResolvePrincipalUseCase(
            authRepository = FakeAuthRepository(
                validationResponse = AuthValidateTokenResponse(
                    principalType = AuthPrincipalType.USER,
                    userUuid = callerId,
                ),
            ),
            databaseRepository = FakeDatabaseRepository(userById = caller),
        )

        val result = useCase("Bearer valid-token")

        assertEquals(GatewayPrincipal.User(caller), result)
    }

    @Test
    fun `returns guest principal without database lookup`() = runBlocking {
        val guestSessionId = UUID.fromString("00000000-0000-0000-0000-00000000abba")
        val useCase = ResolvePrincipalUseCase(
            authRepository = FakeAuthRepository(
                validationResponse = AuthValidateTokenResponse(
                    principalType = AuthPrincipalType.GUEST,
                    guestSessionId = guestSessionId,
                    scopes = listOf("learn_guest"),
                ),
            ),
            databaseRepository = FakeDatabaseRepository(userById = caller),
        )

        val result = useCase("Bearer guest-token")

        val expected = GatewayPrincipal.Guest(
            guestSessionId = guestSessionId,
            scopes = listOf("learn_guest"),
        )
        assertEquals(expected, result)
    }

    @Test
    fun `throws unauthorized when header is missing`() = runBlocking {
        val useCase = ResolvePrincipalUseCase(
            authRepository = FakeAuthRepository(),
            databaseRepository = FakeDatabaseRepository(userById = caller),
        )

        val unauthorized = assertFailsWith<GatewayException.Unauthorized> {
            useCase(null)
        }
        assertEquals(GatewayErrorReason.MISSING_AUTH_HEADER, unauthorized.reason)
    }

    @Test
    fun `throws unauthorized when token validation fails`() {
        runBlocking {
            val useCase = ResolvePrincipalUseCase(
                authRepository = FakeAuthRepository(
                    validationThrowable = GatewayException.Unauthorized(
                        reason = GatewayErrorReason.INVALID_ACCESS_TOKEN,
                        message = "Invalid",
                    ),
                ),
                databaseRepository = FakeDatabaseRepository(userById = caller),
            )

            assertFailsWith<Throwable> {
                useCase("Bearer invalid-token")
            }
        }
    }
}

private class FakeAuthRepository(
    private val validationResponse: AuthValidateTokenResponse? = null,
    private val validationThrowable: Throwable? = null,
) : AuthRepositoryContract {
    override suspend fun loginWithGoogle(
        idToken: String,
        fingerprint: String,
        platform: GatewaySessionPlatform,
    ): GatewayGoogleLoginResponse = GatewayGoogleLoginResponse(
        accessToken = "access",
        refreshToken = "refresh",
        user = GatewayUser(
            id = UUID.randomUUID(),
            email = "unused@headway.test",
            name = "Unused",
            role = GatewayUserRole.GUEST,
        ),
    )

    override suspend fun loginAsGuest(): GatewayGuestLoginResponse = GatewayGuestLoginResponse(
        accessToken = "guest",
        expiresAtEpochMs = 1L,
    )

    override suspend fun refreshToken(
        refreshToken: String,
        fingerprint: String,
    ): GatewayRefreshAccessTokenResponse = GatewayRefreshAccessTokenResponse(accessToken = "access")

    override suspend fun validateToken(accessToken: String): AuthValidateTokenResponse {
        validationThrowable?.let { throw it }
        return requireNotNull(validationResponse)
    }
}

private class FakeDatabaseRepository(
    private val userById: GatewayUser,
) : DatabaseRepositoryContract {
    override suspend fun inviteUser(
        email: String,
        department: String,
        role: GatewayUserRole?,
    ): GatewayUser = userById

    override suspend fun getUserById(userId: UUID): GatewayUser = userById
}
