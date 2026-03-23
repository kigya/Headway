package dev.kigya.headway.gateway.presentation

import dev.kigya.headway.auth.api.model.out.AuthPrincipalType
import dev.kigya.headway.auth.api.model.out.AuthValidateTokenResponse
import dev.kigya.headway.common.util.Environment
import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.data.probe.base.HttpProber
import dev.kigya.headway.gateway.domain.repository.AuthRepositoryContract
import dev.kigya.headway.gateway.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.gateway.domain.usecase.CheckHealthStatusUseCase
import dev.kigya.headway.gateway.domain.usecase.InviteUserUseCase
import dev.kigya.headway.gateway.domain.usecase.LoginAsGuestUseCase
import dev.kigya.headway.gateway.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.gateway.domain.usecase.RefreshAccessTokenUseCase
import dev.kigya.headway.gateway.domain.usecase.ResolvePrincipalUseCase
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayGuestLoginResponse
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import dev.kigya.headway.gateway.model.GatewayServiceStatus
import dev.kigya.headway.gateway.model.GatewaySessionPlatform
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserDepartment
import dev.kigya.headway.gateway.model.GatewayUserRole
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class GatewaySecurityRoutesTest {
    @Test
    fun `graphql inviteUser returns unauthorized without authorization header`() = testApplication {
        val authRepository = TestAuthRepository(
            validationResponse = AuthValidateTokenResponse(
                principalType = AuthPrincipalType.USER,
                userUuid = CALLER_ID,
            ),
        )
        val databaseRepository = TestDatabaseRepository(
            caller = developerCaller,
            invitedUser = invitedUser,
        )

        application {
            installSecurityTestApplication(
                authRepository = authRepository,
                databaseRepository = databaseRepository,
            )
        }

        val response = client.post("/api/v1/graphql") {
            contentType(ContentType.Application.Json)
            setBody(GRAPHQL_INVITE_MUTATION)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val root = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        val error = root["errors"]!!.jsonArray.first().jsonObject
        assertEquals("UNAUTHORIZED", error["extensions"]!!.jsonObject["code"]!!.jsonPrimitive.content)
        assertEquals("MISSING_AUTH_HEADER", error["extensions"]!!.jsonObject["reason"]!!.jsonPrimitive.content)
        assertEquals(0, databaseRepository.inviteCalls)
    }

    @Test
    fun `graphql inviteUser returns forbidden for non admin roles`() = testApplication {
        val authRepository = TestAuthRepository(
            validationResponse = AuthValidateTokenResponse(
                principalType = AuthPrincipalType.USER,
                userUuid = CALLER_ID,
            ),
        )
        val databaseRepository = TestDatabaseRepository(
            caller = employeeCaller,
            invitedUser = invitedUser,
        )

        application {
            installSecurityTestApplication(
                authRepository = authRepository,
                databaseRepository = databaseRepository,
            )
        }

        val response = client.post("/api/v1/graphql") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer valid-token")
            setBody(GRAPHQL_INVITE_MUTATION)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val root = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        val error = root["errors"]!!.jsonArray.first().jsonObject
        assertEquals("FORBIDDEN", error["extensions"]!!.jsonObject["code"]!!.jsonPrimitive.content)
        assertEquals("INSUFFICIENT_ROLE", error["extensions"]!!.jsonObject["reason"]!!.jsonPrimitive.content)
        assertEquals(0, databaseRepository.inviteCalls)
    }

    @Test
    fun `graphql inviteUser succeeds for developer role`() = testApplication {
        val authRepository = TestAuthRepository(
            validationResponse = AuthValidateTokenResponse(
                principalType = AuthPrincipalType.USER,
                userUuid = CALLER_ID,
            ),
        )
        val databaseRepository = TestDatabaseRepository(
            caller = developerCaller,
            invitedUser = invitedUser,
        )

        application {
            installSecurityTestApplication(
                authRepository = authRepository,
                databaseRepository = databaseRepository,
            )
        }

        val response = client.post("/api/v1/graphql") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer valid-token")
            setBody(GRAPHQL_INVITE_MUTATION)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(true, response.bodyAsText().contains(invitedUser.email))
        assertEquals(1, databaseRepository.inviteCalls)
        assertEquals(null, databaseRepository.lastInvitedRole)
    }

    @Test
    fun `graphql inviteUser returns guest not allowed for guest principal`() = testApplication {
        val authRepository = TestAuthRepository(
            validationResponse = AuthValidateTokenResponse(
                principalType = AuthPrincipalType.GUEST,
                guestSessionId = UUID.fromString("00000000-0000-0000-0000-00000000cafe"),
                scopes = listOf("learn_guest"),
            ),
        )
        val databaseRepository = TestDatabaseRepository(
            caller = developerCaller,
            invitedUser = invitedUser,
        )

        application {
            installSecurityTestApplication(
                authRepository = authRepository,
                databaseRepository = databaseRepository,
            )
        }

        val response = client.post("/api/v1/graphql") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer guest-token")
            setBody(GRAPHQL_INVITE_MUTATION)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val root = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        val error = root["errors"]!!.jsonArray.first().jsonObject
        assertEquals("FORBIDDEN", error["extensions"]!!.jsonObject["code"]!!.jsonPrimitive.content)
        assertEquals("GUEST_NOT_ALLOWED", error["extensions"]!!.jsonObject["reason"]!!.jsonPrimitive.content)
        assertEquals(0, databaseRepository.inviteCalls)
    }

    private fun io.ktor.server.application.Application.installSecurityTestApplication(
        authRepository: TestAuthRepository,
        databaseRepository: TestDatabaseRepository,
    ) {
        installGatewayApi(
            GatewayApiBindings(
                environment = Environment.DEV,
                checkHealthStatus = CheckHealthStatusUseCase(
                    authProbe = { GatewayServiceStatus.OK },
                    databaseProbe = { GatewayServiceStatus.OK },
                ),
                loginWithGoogle = LoginWithGoogleUseCase(authRepository),
                loginAsGuest = LoginAsGuestUseCase(authRepository),
                refreshToken = RefreshAccessTokenUseCase(authRepository),
                inviteUser = InviteUserUseCase(databaseRepository),
                resolvePrincipal = ResolvePrincipalUseCase(authRepository, databaseRepository),
            ),
        )
    }
}

private class TestAuthRepository(
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
        user = developerCaller,
    )

    override suspend fun loginAsGuest(): GatewayGuestLoginResponse = GatewayGuestLoginResponse(
        accessToken = "guest-access",
        expiresAtEpochMs = 99L,
    )

    override suspend fun refreshToken(
        refreshToken: String,
        fingerprint: String,
    ): GatewayRefreshAccessTokenResponse = GatewayRefreshAccessTokenResponse(accessToken = "access")

    override suspend fun validateToken(accessToken: String): AuthValidateTokenResponse {
        validationThrowable?.let { throw it }
        return validationResponse ?: throw GatewayException.Unauthorized(
            reason = GatewayErrorReason.INVALID_ACCESS_TOKEN,
            message = "Invalid",
        )
    }
}

private class TestDatabaseRepository(
    private val caller: GatewayUser,
    private val invitedUser: GatewayUser,
) : DatabaseRepositoryContract {
    var inviteCalls: Int = 0
        private set
    var lastInvitedRole: GatewayUserRole? = null
        private set

    override suspend fun inviteUser(
        email: String,
        department: String,
        role: GatewayUserRole?,
    ): GatewayUser {
        inviteCalls += 1
        lastInvitedRole = role
        return invitedUser.copy(email = email)
    }

    override suspend fun getUserById(userId: UUID): GatewayUser = caller
}

private val CALLER_ID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000123")

private val developerCaller = GatewayUser(
    id = CALLER_ID,
    email = "developer@headway.test",
    name = "Developer",
    role = GatewayUserRole.DEVELOPER,
    department = GatewayUserDepartment.ANDROID,
)

private val employeeCaller = GatewayUser(
    id = CALLER_ID,
    email = "employee@headway.test",
    name = "Employee",
    role = GatewayUserRole.EMPLOYEE,
    department = GatewayUserDepartment.ANDROID,
)

private val invitedUser = GatewayUser(
    id = UUID.fromString("00000000-0000-0000-0000-000000000999"),
    email = "new.user@headway.test",
    name = "Invited User",
    role = GatewayUserRole.EMPLOYEE,
    department = GatewayUserDepartment.CROSSPLATFORM,
)

private const val GRAPHQL_INVITE_MUTATION =
    """{"query":"mutation { inviteUser(email: \"new.user@headway.test\", department: \"ANDROID\") { email role } }"}"""
