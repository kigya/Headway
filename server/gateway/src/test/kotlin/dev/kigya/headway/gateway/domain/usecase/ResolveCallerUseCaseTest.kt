package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.auth.api.model.out.AuthValidateTokenResponse
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.repository.AuthRepositoryContract
import dev.kigya.headway.gateway.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import dev.kigya.headway.gateway.model.GatewaySessionPlatform
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserDepartment
import dev.kigya.headway.gateway.model.GatewayUserRole
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ResolveCallerUseCaseTest {
    private val callerId = UUID.fromString("00000000-0000-0000-0000-000000000123")
    private val caller = GatewayUser(
        id = callerId,
        email = "developer@headway.test",
        name = "Developer",
        role = GatewayUserRole.DEVELOPER,
        department = GatewayUserDepartment.ANDROID,
    )

    @Test
    fun `returns caller when bearer token is valid`() = runTest {
        val useCase = ResolveCallerUseCase(
            authRepository = FakeAuthRepository(
                validationResponse = AuthValidateTokenResponse(
                    userUuid = callerId,
                    isValid = true,
                ),
            ),
            databaseRepository = FakeDatabaseRepository(userById = caller),
        )

        val result = useCase("Bearer valid-token")

        assertEquals(caller, result)
    }

    @Test
    fun `throws unauthorized when header is missing`() = runTest {
        val useCase = ResolveCallerUseCase(
            authRepository = FakeAuthRepository(),
            databaseRepository = FakeDatabaseRepository(userById = caller),
        )

        assertFailsWith<GatewayException.Unauthorized> {
            useCase(null)
        }
    }

    @Test
    fun `throws unauthorized when token validation fails`() = runTest {
        val useCase = ResolveCallerUseCase(
            authRepository = FakeAuthRepository(validationThrowable = GatewayException.Unauthorized("Unauthorized")),
            databaseRepository = FakeDatabaseRepository(userById = caller),
        )

        assertFailsWith<GatewayException.Unauthorized> {
            useCase("Bearer invalid-token")
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

private fun runTest(block: suspend () -> Unit) = kotlinx.coroutines.runBlocking {
    block()
}
