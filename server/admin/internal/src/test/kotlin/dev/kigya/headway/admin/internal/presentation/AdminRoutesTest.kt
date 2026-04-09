package dev.kigya.headway.admin.internal.presentation

import dev.kigya.headway.admin.internal.core.config.AdminSessionConfig
import dev.kigya.headway.admin.internal.core.config.DeveloperSettingsConfig
import dev.kigya.headway.admin.internal.core.session.AdminSession
import dev.kigya.headway.admin.internal.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.admin.internal.domain.repository.GithubOAuthClientContract
import dev.kigya.headway.admin.internal.domain.usecase.InviteUserUseCase
import dev.kigya.headway.admin.internal.model.admin.CollaboratorPermission
import dev.kigya.headway.admin.internal.model.admin.GithubUserInfo
import dev.kigya.headway.common.util.Environment
import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.api.model.out.DatabaseUserDepartment
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdminRoutesTest {
    @Test
    fun `app shell renders and bootstrap is anonymous without session`() = testApplication {
        application {
            installAdminTestApplication(
                databaseRepository = TestDatabaseRepository(),
                githubOAuthClient = TestGithubOAuthClient(),
            )
        }

        val appResponse = client.get("/internal/v1/admin/app")
        val bootstrapResponse = client.get("/internal/v1/admin/bootstrap")
        val bootstrapBody = bootstrapResponse.bodyAsText().compactWhitespace()

        assertEquals(HttpStatusCode.OK, appResponse.status)
        assertTrue(appResponse.bodyAsText().contains("<div id=\"root\"></div>"))
        assertEquals(HttpStatusCode.OK, bootstrapResponse.status)
        assertTrue(bootstrapBody.contains(""""session":null"""))
    }

    @Test
    fun `callback with invalid state redirects back to app with error`() = testApplication {
        val applicationClient = createClient { followRedirects = false }

        application {
            installAdminTestApplication(
                databaseRepository = TestDatabaseRepository(),
                githubOAuthClient = TestGithubOAuthClient(),
            )
        }

        val response = applicationClient.get("/internal/v1/admin/auth/github/callback?code=code&state=wrong")

        assertEquals(HttpStatusCode.Found, response.status)
        assertTrue(response.headers[HttpHeaders.Location].orEmpty().contains("error=invalid_state"))
    }

    @Test
    fun `non collaborator is redirected to denied screen`() = testApplication {
        val applicationClient = createClient { followRedirects = false }

        application {
            installAdminTestApplication(
                databaseRepository = TestDatabaseRepository(),
                githubOAuthClient = TestGithubOAuthClient(collaboratorPermission = null),
            )
        }

        val response = applicationClient.get(
            "/internal/v1/admin/auth/github/callback?code=good-code&state=state-123",
        ) {
            header(HttpHeaders.Cookie, "${testSessionConfig.stateCookieName}=state-123")
        }

        assertEquals(HttpStatusCode.Found, response.status)
        assertTrue(response.headers[HttpHeaders.Location].orEmpty().contains("screen=denied"))
    }

    @Test
    fun `successful bootstrap and invite use selected role`() = testApplication {
        val databaseRepository = TestDatabaseRepository()
        val applicationClient = createClient { followRedirects = false }
        val sessionCookie = AdminSession.sign(
            session = AdminSession(
                githubLogin = "octocat",
                githubAvatarUrl = "https://avatars.githubusercontent.com/u/1",
                hasRepositoryAccess = true,
                createdAt = System.currentTimeMillis(),
            ),
            secret = testSessionConfig.sessionSecret,
        )

        application {
            installAdminTestApplication(
                databaseRepository = databaseRepository,
                githubOAuthClient = TestGithubOAuthClient(),
            )
        }

        val bootstrapResponse = applicationClient.get("/internal/v1/admin/bootstrap") {
            header(HttpHeaders.Cookie, "${testSessionConfig.cookieName}=$sessionCookie")
        }
        val inviteResponse = applicationClient.post("/internal/v1/admin/invite") {
            header(HttpHeaders.Cookie, "${testSessionConfig.cookieName}=$sessionCookie")
            contentType(ContentType.Application.Json)
            setBody(INVITE_REQUEST_BODY)
        }
        val bootstrapBody = bootstrapResponse.bodyAsText().compactWhitespace()

        assertEquals(HttpStatusCode.OK, bootstrapResponse.status)
        assertTrue(bootstrapBody.contains(""""github_login":"octocat""""))
        assertEquals(HttpStatusCode.OK, inviteResponse.status)
        assertTrue(inviteResponse.bodyAsText().contains("Invited new.user@headway.test as Manager in Android"))
        assertEquals(DatabaseUserRole.MANAGER, databaseRepository.lastInvitedRole)
        assertEquals(DatabaseUserDepartment.ANDROID, databaseRepository.lastInvitedDepartment)
    }

    @Test
    fun `invite requires active session`() = testApplication {
        application {
            installAdminTestApplication(
                databaseRepository = TestDatabaseRepository(),
                githubOAuthClient = TestGithubOAuthClient(),
            )
        }

        val response = client.post("/internal/v1/admin/invite") {
            contentType(ContentType.Application.Json)
            setBody(INVITE_REQUEST_BODY)
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertTrue(response.bodyAsText().contains("Session expired"))
    }

    private fun io.ktor.server.application.Application.installAdminTestApplication(
        databaseRepository: TestDatabaseRepository,
        githubOAuthClient: TestGithubOAuthClient,
    ) {
        installAdminApi(
            config = testDeveloperSettingsConfig,
            githubOAuthClient = githubOAuthClient,
            inviteUser = InviteUserUseCase(databaseRepository),
        )
    }
}

private class TestDatabaseRepository : DatabaseRepositoryContract {
    var lastInvitedRole: DatabaseUserRole? = null
        private set
    var lastInvitedDepartment: DatabaseUserDepartment? = null
        private set

    override suspend fun inviteUser(
        email: String,
        role: DatabaseUserRole,
        department: DatabaseUserDepartment,
    ): DatabaseUser {
        lastInvitedRole = role
        lastInvitedDepartment = department
        return invitedUser.copy(
            email = email,
            role = role,
            department = department,
        )
    }
}

private class TestGithubOAuthClient(
    private val collaboratorPermission: CollaboratorPermission? = CollaboratorPermission(permission = "read"),
) : GithubOAuthClientContract {
    override suspend fun exchangeCodeForAccessToken(
        code: String,
        redirectUri: String,
    ): String = "github-access-token"

    override suspend fun getCurrentUser(accessToken: String): GithubUserInfo = GithubUserInfo(
        login = "octocat",
        id = 1,
        avatarUrl = "https://avatars.githubusercontent.com/u/1",
    )

    override suspend fun getCollaboratorPermission(
        accessToken: String,
        owner: String,
        repo: String,
        username: String,
    ): CollaboratorPermission? = collaboratorPermission
}

private val testSessionConfig = AdminSessionConfig(
    sessionSecret = "test-admin-session-secret-that-is-long-enough",
    isSecureCookie = false,
)

private val testDeveloperSettingsConfig = DeveloperSettingsConfig(
    environment = Environment.DEV,
    githubClientId = "client-id",
    oauthPublicOrigin = null,
    repoOwner = "kigya",
    repoName = "Headway",
    sessionConfig = testSessionConfig,
)

private val invitedUser = DatabaseUser(
    id = UUID.fromString("00000000-0000-0000-0000-000000000999"),
    email = "new.user@headway.test",
    googleId = null,
    name = "Invited User",
    role = DatabaseUserRole.EMPLOYEE,
    department = DatabaseUserDepartment.CROSSPLATFORM,
    avatarUrl = null,
    isActive = false,
    createdAt = 1_000L,
    updatedAt = 1_000L,
)

private fun String.compactWhitespace(): String = replace(Regex("\\s+"), "")

private const val INVITE_REQUEST_BODY = """{"email":"new.user@headway.test","role":"MANAGER","department":"ANDROID"}"""
