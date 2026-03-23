package dev.kigya.headway.admin.internal.core.config

internal data class AdminSessionConfig(
    val sessionSecret: String,
    val cookieName: String = "headway_admin_session",
    val stateCookieName: String = "headway_admin_oauth_state",
    val cookiePath: String = "/internal/v1/admin",
    val ttlMs: Long = DEFAULT_TTL_MS,
    val isSecureCookie: Boolean,
) {
    private companion object {
        const val DEFAULT_TTL_MS = 60L * 60L * 1000L
    }
}
