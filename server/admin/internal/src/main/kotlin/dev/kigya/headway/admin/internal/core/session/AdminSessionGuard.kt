package dev.kigya.headway.admin.internal.core.session

import dev.kigya.headway.admin.internal.core.config.AdminSessionConfig
import dev.kigya.headway.admin.internal.core.exception.AdminException
import io.ktor.http.Cookie
import io.ktor.server.application.ApplicationCall

internal fun ApplicationCall.requireAdminSession(config: AdminSessionConfig): AdminSession {
    val sessionCookie = request.cookies[config.cookieName]
    val session = sessionCookie?.let {
        AdminSession.verify(
            cookieValue = it,
            secret = config.sessionSecret,
            ttlMs = config.ttlMs,
        )
    }
    return session ?: throw AdminException.Unauthorized("Session expired")
}

internal fun ApplicationCall.readAdminSessionOrNull(config: AdminSessionConfig): AdminSession? =
    request.cookies[config.cookieName]?.let {
        AdminSession.verify(
            cookieValue = it,
            secret = config.sessionSecret,
            ttlMs = config.ttlMs,
        )
    }

internal fun ApplicationCall.setAdminSession(
    config: AdminSessionConfig,
    session: AdminSession,
) {
    response.cookies.append(
        Cookie(
            name = config.cookieName,
            value = AdminSession.sign(session, config.sessionSecret),
            maxAge = (config.ttlMs / 1000L).toInt(),
            path = config.cookiePath,
            httpOnly = true,
            secure = config.isSecureCookie,
            extensions = sameSiteExtensions,
        ),
    )
}

internal fun ApplicationCall.setOauthState(
    config: AdminSessionConfig,
    state: String,
) {
    response.cookies.append(
        Cookie(
            name = config.stateCookieName,
            value = state,
            maxAge = STATE_COOKIE_MAX_AGE_SECONDS,
            path = config.cookiePath,
            httpOnly = true,
            secure = config.isSecureCookie,
            extensions = sameSiteExtensions,
        ),
    )
}

internal fun ApplicationCall.readOauthState(config: AdminSessionConfig): String? =
    request.cookies[config.stateCookieName]

internal fun ApplicationCall.clearAdminCookies(config: AdminSessionConfig) {
    response.cookies.append(
        Cookie(
            name = config.cookieName,
            value = "",
            maxAge = 0,
            path = config.cookiePath,
            httpOnly = true,
            secure = config.isSecureCookie,
            extensions = sameSiteExtensions,
        ),
    )
    response.cookies.append(
        Cookie(
            name = config.stateCookieName,
            value = "",
            maxAge = 0,
            path = config.cookiePath,
            httpOnly = true,
            secure = config.isSecureCookie,
            extensions = sameSiteExtensions,
        ),
    )
}

private val sameSiteExtensions = mapOf("SameSite" to "Lax")

private const val STATE_COOKIE_MAX_AGE_SECONDS = 5 * 60
