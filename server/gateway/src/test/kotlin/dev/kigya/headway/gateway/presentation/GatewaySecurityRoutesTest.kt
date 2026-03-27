package dev.kigya.headway.gateway.presentation

import dev.kigya.headway.auth.api.model.out.AuthPrincipalType
import dev.kigya.headway.auth.api.model.out.AuthValidateTokenResponse
import dev.kigya.headway.common.util.Environment
import dev.kigya.headway.database.api.model.`in`.DatabasePreparationSelectQuestionRequestDto
import dev.kigya.headway.database.api.model.`in`.DatabasePreparationStartSessionRequestDto
import dev.kigya.headway.database.api.model.`in`.DatabasePreparationSubmitOutcomeRequestDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationCatalogResponseDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationEmployeesResponseDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationReadinessResponseDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionStateDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionSummaryDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationStartSessionResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.repository.AuthRepositoryContract
import dev.kigya.headway.gateway.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.gateway.domain.repository.HomeRepositoryContract
import dev.kigya.headway.gateway.domain.repository.PreparationRepositoryContract
import dev.kigya.headway.gateway.domain.usecase.CheckHealthStatusUseCase
import dev.kigya.headway.gateway.domain.usecase.FinishPreparationSessionUseCase
import dev.kigya.headway.gateway.domain.usecase.GetHomeScreenUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationEmployeeReadinessUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationFormatCatalogUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationSessionStateUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationSessionSummaryUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationSetupEmployeesUseCase
import dev.kigya.headway.gateway.domain.usecase.InviteUserUseCase
import dev.kigya.headway.gateway.domain.usecase.LoginAsGuestUseCase
import dev.kigya.headway.gateway.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.gateway.domain.usecase.RefreshAccessTokenUseCase
import dev.kigya.headway.gateway.domain.usecase.ResolvePrincipalUseCase
import dev.kigya.headway.gateway.domain.usecase.SelectPreparationSessionQuestionUseCase
import dev.kigya.headway.gateway.domain.usecase.StartPreparationSessionUseCase
import dev.kigya.headway.gateway.domain.usecase.SubmitPreparationOutcomeUseCase
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayGuestLoginResponse
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import dev.kigya.headway.gateway.model.GatewayServiceStatus
import dev.kigya.headway.gateway.model.GatewaySessionPlatform
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserDepartment
import dev.kigya.headway.gateway.model.GatewayUserRole
import dev.kigya.headway.gateway.presentation.schema.PreparationGraphqlServices
import dev.kigya.headway.home.api.model.`in`.HomeAppLocaleDto
import dev.kigya.headway.home.api.model.`in`.HomeScreenRequestDto
import dev.kigya.headway.home.api.model.`in`.HomeUserRoleDto
import dev.kigya.headway.home.api.model.out.HomeScreenNextInterviewTypeDto
import dev.kigya.headway.home.api.model.out.HomeScreenResponseDto
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

    private val stubPreparationGraphqlServices: PreparationGraphqlServices =
        PreparationGraphqlServices(
            getPreparationSetupEmployees = GetPreparationSetupEmployeesUseCase(
                preparationRepository = StubPreparationRepositoryContract,
            ),
            getPreparationEmployeeReadiness = GetPreparationEmployeeReadinessUseCase(
                preparationRepository = StubPreparationRepositoryContract,
            ),
            getPreparationFormatCatalog = GetPreparationFormatCatalogUseCase(
                preparationRepository = StubPreparationRepositoryContract,
            ),
            startPreparationSession = StartPreparationSessionUseCase(
                preparationRepository = StubPreparationRepositoryContract,
            ),
            getPreparationSessionState = GetPreparationSessionStateUseCase(
                preparationRepository = StubPreparationRepositoryContract,
            ),
            submitPreparationOutcome = SubmitPreparationOutcomeUseCase(
                preparationRepository = StubPreparationRepositoryContract,
            ),
            selectPreparationSessionQuestion = SelectPreparationSessionQuestionUseCase(
                preparationRepository = StubPreparationRepositoryContract,
            ),
            finishPreparationSession = FinishPreparationSessionUseCase(
                preparationRepository = StubPreparationRepositoryContract,
            ),
            getPreparationSessionSummary = GetPreparationSessionSummaryUseCase(
                preparationRepository = StubPreparationRepositoryContract,
            ),
        )

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

    @Test
    fun `graphql homeScreen returns unauthorized without authorization header`() = testApplication {
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
            setBody(GRAPHQL_HOME_QUERY)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val root = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        val error = root["errors"]!!.jsonArray.first().jsonObject
        assertEquals("UNAUTHORIZED", error["extensions"]!!.jsonObject["code"]!!.jsonPrimitive.content)
    }

    @Test
    fun `graphql homeScreen returns guest not allowed for guest principal`() = testApplication {
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
            setBody(GRAPHQL_HOME_QUERY)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val root = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        val error = root["errors"]!!.jsonArray.first().jsonObject
        assertEquals("FORBIDDEN", error["extensions"]!!.jsonObject["code"]!!.jsonPrimitive.content)
        assertEquals("GUEST_NOT_ALLOWED", error["extensions"]!!.jsonObject["reason"]!!.jsonPrimitive.content)
    }

    @Test
    fun `graphql preparationSetupEmployees returns guest not allowed for guest principal`() =
        testApplication {
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
                setBody(GRAPHQL_PREPARATION_SETUP_EMPLOYEES_QUERY)
            }

            assertEquals(HttpStatusCode.OK, response.status)
            val root = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            val error = root["errors"]!!.jsonArray.first().jsonObject
            assertEquals("FORBIDDEN", error["extensions"]!!.jsonObject["code"]!!.jsonPrimitive.content)
            assertEquals("GUEST_NOT_ALLOWED", error["extensions"]!!.jsonObject["reason"]!!.jsonPrimitive.content)
        }

    @Test
    fun `graphql homeScreen returns russian greeting for ru locale header`() = testApplication {
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
            header("X-Headway-Locale", "ru")
            setBody(GRAPHQL_HOME_QUERY)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val root = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        val home = root["data"]!!.jsonObject["homeScreen"]!!.jsonObject
        assertEquals("Привет, Developer!", home["greeting"]!!.jsonPrimitive.content)
    }

    @Test
    fun `graphql homeScreen returns fixed english date label with test clock`() = testApplication {
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
            installGatewayApi(
                GatewayApiBindings(
                    environment = Environment.DEV,
                    checkHealthStatus = CheckHealthStatusUseCase(
                        authProbe = { GatewayServiceStatus.OK },
                        databaseProbe = { GatewayServiceStatus.OK },
                        homeProbe = { GatewayServiceStatus.OK },
                    ),
                    loginWithGoogle = LoginWithGoogleUseCase(authRepository),
                    loginAsGuest = LoginAsGuestUseCase(authRepository),
                    refreshToken = RefreshAccessTokenUseCase(authRepository),
                    inviteUser = InviteUserUseCase(databaseRepository),
                    resolvePrincipal = ResolvePrincipalUseCase(authRepository, databaseRepository),
                    getHomeScreen = GetHomeScreenUseCase(
                        homeRepository = object : HomeRepositoryContract {
                            override suspend fun getHomeScreen(
                                request: HomeScreenRequestDto,
                            ): HomeScreenResponseDto = developerHomeEnStub.copy(
                                dateLabel = "Sat, 25 Jan 2025",
                            )
                        },
                    ),
                    preparation = stubPreparationGraphqlServices,
                ),
            )
        }

        val response = client.post("/api/v1/graphql") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer valid-token")
            header("X-Headway-Locale", "en")
            setBody(GRAPHQL_HOME_QUERY)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val root = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        val home = root["data"]!!.jsonObject["homeScreen"]!!.jsonObject
        assertEquals("Sat, 25 Jan 2025", home["dateLabel"]!!.jsonPrimitive.content)
    }

    @Test
    fun `graphql homeScreen returns readiness and next interview for employee`() = testApplication {
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
            installGatewayApi(
                GatewayApiBindings(
                    environment = Environment.DEV,
                    checkHealthStatus = CheckHealthStatusUseCase(
                        authProbe = { GatewayServiceStatus.OK },
                        databaseProbe = { GatewayServiceStatus.OK },
                        homeProbe = { GatewayServiceStatus.OK },
                    ),
                    loginWithGoogle = LoginWithGoogleUseCase(authRepository),
                    loginAsGuest = LoginAsGuestUseCase(authRepository),
                    refreshToken = RefreshAccessTokenUseCase(authRepository),
                    inviteUser = InviteUserUseCase(databaseRepository),
                    resolvePrincipal = ResolvePrincipalUseCase(authRepository, databaseRepository),
                    getHomeScreen = GetHomeScreenUseCase(
                        homeRepository = EmployeeHomeReadinessTestHomeRepository,
                    ),
                    preparation = stubPreparationGraphqlServices,
                ),
            )
        }

        val response = client.post("/api/v1/graphql") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer valid-token")
            setBody(GRAPHQL_HOME_QUERY)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val root = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        val home = root["data"]!!.jsonObject["homeScreen"]!!.jsonObject
        assertEquals("80", home["readinessPercent"]!!.jsonPrimitive.content)
        assertEquals("MOCK", home["nextInterviewType"]!!.jsonPrimitive.content)
        assertEquals("Mock", home["nextInterviewTypeLabel"]!!.jsonPrimitive.content)
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
                    homeProbe = { GatewayServiceStatus.OK },
                ),
                loginWithGoogle = LoginWithGoogleUseCase(authRepository),
                loginAsGuest = LoginAsGuestUseCase(authRepository),
                refreshToken = RefreshAccessTokenUseCase(authRepository),
                inviteUser = InviteUserUseCase(databaseRepository),
                resolvePrincipal = ResolvePrincipalUseCase(authRepository, databaseRepository),
                getHomeScreen = GetHomeScreenUseCase(
                    homeRepository = LocaleAwareDeveloperTestHomeRepository,
                ),
                preparation = stubPreparationGraphqlServices,
            ),
        )
    }
}

private object StubPreparationRepositoryContract : PreparationRepositoryContract {

    override suspend fun getSetupEmployees(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
    ): DatabasePreparationEmployeesResponseDto = DatabasePreparationEmployeesResponseDto(
        employees = emptyList(),
    )

    override suspend fun getEmployeeReadiness(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        subjectUserId: UUID,
    ): DatabasePreparationReadinessResponseDto = throw AssertionError("stub")

    override suspend fun getFormatCatalog(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        locale: String,
    ): DatabasePreparationCatalogResponseDto = throw AssertionError("stub")

    override suspend fun startSession(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        body: DatabasePreparationStartSessionRequestDto,
    ): DatabasePreparationStartSessionResponseDto = throw AssertionError("stub")

    override suspend fun getSessionState(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
    ): DatabasePreparationSessionStateDto = throw AssertionError("stub")

    override suspend fun submitOutcome(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        body: DatabasePreparationSubmitOutcomeRequestDto,
    ): DatabasePreparationSessionStateDto = throw AssertionError("stub")

    override suspend fun selectQuestion(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        body: DatabasePreparationSelectQuestionRequestDto,
    ): DatabasePreparationSessionStateDto = throw AssertionError("stub")

    override suspend fun finishSession(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
    ): DatabasePreparationSessionStateDto = throw AssertionError("stub")

    override suspend fun getSessionSummary(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        locale: String,
    ): DatabasePreparationSessionSummaryDto = throw AssertionError("stub")
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

private val developerHomeEnStub: HomeScreenResponseDto = HomeScreenResponseDto(
    dateLabel = "Mon, 1 Jan 2024",
    greeting = "Hi, Developer!",
    roleLabel = "Developer",
    readinessPercent = null,
    nextInterviewType = null,
    nextInterviewTypeLabel = null,
    sections = emptyList(),
)

private val developerHomeRuStub: HomeScreenResponseDto = developerHomeEnStub.copy(
    greeting = "Привет, Developer!",
)

private object LocaleAwareDeveloperTestHomeRepository : HomeRepositoryContract {
    override suspend fun getHomeScreen(request: HomeScreenRequestDto): HomeScreenResponseDto =
        when (request.locale) {
            HomeAppLocaleDto.RU -> developerHomeRuStub
            HomeAppLocaleDto.EN -> developerHomeEnStub
        }
}

private object EmployeeHomeReadinessTestHomeRepository : HomeRepositoryContract {
    override suspend fun getHomeScreen(request: HomeScreenRequestDto): HomeScreenResponseDto =
        when (request.userRole) {
            HomeUserRoleDto.EMPLOYEE -> HomeScreenResponseDto(
                dateLabel = "Mon, 1 Jan 2024",
                greeting = "Hi, Employee!",
                roleLabel = null,
                readinessPercent = 80,
                nextInterviewType = HomeScreenNextInterviewTypeDto.MOCK,
                nextInterviewTypeLabel = "Mock",
                sections = emptyList(),
            )

            else -> developerHomeEnStub
        }
}

private const val GRAPHQL_INVITE_MUTATION =
    """{"query":"mutation { inviteUser(email: \"new.user@headway.test\", department: \"ANDROID\") { email role } }"}"""

private const val GRAPHQL_HOME_QUERY: String =
    """{"query":"query { homeScreen { dateLabel greeting roleLabel readinessPercent """ +
        """ nextInterviewType nextInterviewTypeLabel sections { id title style iconUrl } } }"}"""

private const val GRAPHQL_PREPARATION_SETUP_EMPLOYEES_QUERY =
    """{"query":"query { preparationSetupEmployees { id displayName } }"}"""
