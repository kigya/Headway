package dev.kigya.headway.admin.internal.presentation.plugin

import dev.kigya.headway.admin.api.model.out.AdminBootstrapResponse
import dev.kigya.headway.admin.api.model.out.AdminOptionDto
import dev.kigya.headway.admin.api.model.out.AdminSessionUserDto
import dev.kigya.headway.admin.api.url.adminServiceUrlHolder
import dev.kigya.headway.admin.internal.core.config.DeveloperSettingsConfig
import dev.kigya.headway.admin.internal.core.session.AdminSession
import dev.kigya.headway.database.api.model.out.DatabaseUserDepartment
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import io.ktor.http.ContentType
import io.ktor.http.encodeURLParameter
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.origin
import io.ktor.server.response.respondBytes
import java.security.SecureRandom

internal fun DeveloperSettingsConfig.toBootstrap(session: AdminSession?): AdminBootstrapResponse =
    AdminBootstrapResponse(
        environment = environment.rawValue,
        isProd = environment.isProd,
        repositoryFullName = repositoryFullName,
        session = session?.toDto(),
        roleOptions = DatabaseUserRole.entries.map {
            AdminOptionDto(
                value = it.slug,
                label = it.label(),
            )
        },
        departmentOptions = DatabaseUserDepartment.entries.map {
            AdminOptionDto(
                value = it.slug,
                label = it.label(),
            )
        },
    )

internal suspend fun ApplicationCall.respondClasspathAsset(
    resourcePath: String,
    contentType: ContentType,
) {
    val bytes = Thread.currentThread()
        .contextClassLoader
        .getResourceAsStream(resourcePath)
        ?.use { it.readBytes() }
        ?: throw NotFoundException("Asset not found")
    respondBytes(
        bytes = bytes,
        contentType = contentType,
    )
}

internal fun buildGithubAuthorizeUrl(
    clientId: String,
    redirectUri: String,
    state: String,
): String = buildString {
    append("https://github.com/login/oauth/authorize")
    append("?client_id=").append(clientId.encodeURLParameter())
    append("&redirect_uri=").append(redirectUri.encodeURLParameter())
    append("&scope=repo")
    append("&state=").append(state.encodeURLParameter())
}

internal fun githubCallbackUrl(
    call: ApplicationCall,
    oauthPublicOrigin: String?,
): String {
    val overrideOrigin = oauthPublicOrigin?.trimEnd('/')
    if (!overrideOrigin.isNullOrBlank()) {
        return "$overrideOrigin$githubCallbackPath"
    }

    val headers = call.request.headers
    val forwardedProto = headers["X-Forwarded-Proto"]?.substringBefore(',')?.trim()
    val forwardedHost = headers["X-Forwarded-Host"]?.substringBefore(',')?.trim()
    if (!forwardedProto.isNullOrBlank() && !forwardedHost.isNullOrBlank()) {
        return "$forwardedProto://$forwardedHost$githubCallbackPath"
    }

    val origin = call.request.origin
    val port = origin.serverPort
    val defaultPort = (origin.scheme == "http" && port == HTTP_PORT) ||
        (origin.scheme == "https" && port == HTTPS_PORT)
    val portSuffix = if (defaultPort) "" else ":$port"
    return "${origin.scheme}://${origin.serverHost}$portSuffix$githubCallbackPath"
}

internal fun appRedirect(
    error: String? = null,
    screen: String? = null,
    login: String? = null,
    repo: String? = null,
): String {
    val query = listOf(
        QUERY_ERROR to error,
        QUERY_SCREEN to screen,
        QUERY_LOGIN to login,
        QUERY_REPO to repo,
    ).mapNotNull { (key, value) ->
        value?.takeIf(String::isNotBlank)?.let { "$key=${it.encodeURLParameter()}" }
    }
    return if (query.isEmpty()) {
        appPath
    } else {
        "$appPath?${query.joinToString(separator = "&")}"
    }
}

internal fun randomState(): String {
    val bytes = ByteArray(STATE_BYTE_COUNT)
    secureRandom.nextBytes(bytes)
    return bytes.joinToString(separator = "") { byte -> "%02x".format(byte) }
}

internal fun buildInviteMessage(
    email: String,
    role: DatabaseUserRole,
    department: DatabaseUserDepartment,
): String = buildString {
    append("Invited ")
    append(email)
    append(" as ")
    append(role.label())
    append(" in ")
    append(department.label())
}

internal const val INDEX_RESOURCE_PATH = "admin/index.html"
internal const val APP_JS_RESOURCE_PATH = "admin/app.js"
internal const val STYLES_CSS_RESOURCE_PATH = "admin/styles.css"
internal const val APP_JS_NAME = "app.js"
internal const val STYLES_CSS_NAME = "styles.css"
internal const val INVALID_STATE_ERROR = "invalid_state"
internal const val AUTH_FAILED_ERROR = "auth_failed"
internal const val GITHUB_UNAVAILABLE_ERROR = "github_unavailable"
internal const val ACCESS_DENIED_SCREEN = "denied"
internal val allowedCollaboratorPermissions = setOf("read", "triage", "write", "maintain", "admin")
internal val javascriptContentType = ContentType.parse("text/javascript")

private fun AdminSession.toDto(): AdminSessionUserDto = AdminSessionUserDto(
    githubLogin = githubLogin,
    githubAvatarUrl = githubAvatarUrl,
)

private fun DatabaseUserRole.label(): String = when (this) {
    DatabaseUserRole.DEVELOPER -> "Developer"
    DatabaseUserRole.MANAGER -> "Manager"
    DatabaseUserRole.MENTOR -> "Mentor"
    DatabaseUserRole.EMPLOYEE -> "Employee"
    DatabaseUserRole.GUEST -> "Guest"
}

private fun DatabaseUserDepartment.label(): String = when (this) {
    DatabaseUserDepartment.ANDROID -> "Android"
    DatabaseUserDepartment.IOS -> "iOS"
    DatabaseUserDepartment.CROSSPLATFORM -> "Cross-Platform"
}

private const val HTTP_PORT = 80
private const val HTTPS_PORT = 443
private const val STATE_BYTE_COUNT = 24
private const val QUERY_ERROR = "error"
private const val QUERY_SCREEN = "screen"
private const val QUERY_LOGIN = "login"
private const val QUERY_REPO = "repo"
private val appPath = "${adminServiceUrlHolder.baseUrl}/app"
private val githubCallbackPath = "${adminServiceUrlHolder.baseUrl}/auth/github/callback"
private val secureRandom = SecureRandom()
