package dev.kigya.headway.admin.internal.core.config

import dev.kigya.headway.common.config.CommonConfigurationValues
import dev.kigya.headway.common.util.intEnv
import dev.kigya.headway.common.util.stringEnv

internal object ConfigurationValues {
    val ADMIN_SERVICE_HOST: String
        get() = stringEnv(EnvKeys.ADMIN_SERVICE_HOST)

    val ADMIN_SERVICE_PORT: Int
        get() = intEnv(EnvKeys.ADMIN_SERVICE_PORT)

    val DATABASE_SERVICE_HOST: String
        get() = stringEnv(EnvKeys.DATABASE_SERVICE_HOST)

    val DATABASE_SERVICE_PORT: Int
        get() = intEnv(EnvKeys.DATABASE_SERVICE_PORT)

    val HEADWAY_GITHUB_OAUTH_CLIENT_ID: String
        get() = stringEnv(EnvKeys.HEADWAY_GITHUB_OAUTH_CLIENT_ID)

    val HEADWAY_GITHUB_OAUTH_CLIENT_SECRET: String
        get() = stringEnv(EnvKeys.HEADWAY_GITHUB_OAUTH_CLIENT_SECRET)

    val HEADWAY_REPO_OWNER: String
        get() = stringEnv(EnvKeys.HEADWAY_REPO_OWNER)

    val HEADWAY_REPO_NAME: String
        get() = stringEnv(EnvKeys.HEADWAY_REPO_NAME)

    val HEADWAY_ADMIN_SESSION_SECRET: String
        get() = stringEnv(EnvKeys.HEADWAY_ADMIN_SESSION_SECRET)

    val HEADWAY_ADMIN_OAUTH_PUBLIC_ORIGIN: String?
        get() = System.getenv(EnvKeys.HEADWAY_ADMIN_OAUTH_PUBLIC_ORIGIN)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }

    private const val MIN_ADMIN_SESSION_SECRET_LENGTH = 32

    fun validateSecrets() {
        if (CommonConfigurationValues.environment.isProd) {
            require(HEADWAY_ADMIN_SESSION_SECRET.length >= MIN_ADMIN_SESSION_SECRET_LENGTH) {
                "HEADWAY_ADMIN_SESSION_SECRET must be at least $MIN_ADMIN_SESSION_SECRET_LENGTH characters in prod"
            }
        }
    }

    object EnvKeys {
        const val ADMIN_SERVICE_HOST = "ADMIN_SERVICE_HOST"
        const val ADMIN_SERVICE_PORT = "ADMIN_SERVICE_PORT"
        const val DATABASE_SERVICE_HOST = "DATABASE_SERVICE_HOST"
        const val DATABASE_SERVICE_PORT = "DATABASE_SERVICE_PORT"
        const val HEADWAY_GITHUB_OAUTH_CLIENT_ID = "HEADWAY_GITHUB_OAUTH_CLIENT_ID"
        const val HEADWAY_GITHUB_OAUTH_CLIENT_SECRET = "HEADWAY_GITHUB_OAUTH_CLIENT_SECRET"
        const val HEADWAY_REPO_OWNER = "HEADWAY_REPO_OWNER"
        const val HEADWAY_REPO_NAME = "HEADWAY_REPO_NAME"
        const val HEADWAY_ADMIN_SESSION_SECRET = "HEADWAY_ADMIN_SESSION_SECRET"
        const val HEADWAY_ADMIN_OAUTH_PUBLIC_ORIGIN = "HEADWAY_ADMIN_OAUTH_PUBLIC_ORIGIN"
    }
}
