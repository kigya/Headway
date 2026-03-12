package dev.kigya.headway.auth.internal.core

import dev.kigya.headway.common.config.CommonConfigurationValues.ENV
import dev.kigya.headway.common.util.intEnv
import dev.kigya.headway.common.util.longEnv
import dev.kigya.headway.common.util.stringEnv

internal object ConfigurationValues {

    val AUTH_SERVICE_HOST: String
        get() = stringEnv(EnvKeys.AUTH_SERVICE_SELF_HOST)

    val AUTH_SERVICE_PORT: Int
        get() = intEnv(EnvKeys.AUTH_SERVICE_PORT)

    val DATABASE_SERVICE_HOST: String
        get() = stringEnv(EnvKeys.DATABASE_SERVICE_HOST)

    val DATABASE_SERVICE_PORT: Int
        get() = intEnv(EnvKeys.DATABASE_SERVICE_PORT)

    val JWT_ISSUER: String
        get() = stringEnv(EnvKeys.JWT_ISSUER).ifBlank { DefaultValues.JWT_ISSUER }
    val JWT_AUDIENCE: String
        get() = stringEnv(EnvKeys.JWT_AUDIENCE).ifBlank { DefaultValues.JWT_AUDIENCE }

    val JWT_ACCESS_SECRET: String
        get() = stringEnv(EnvKeys.JWT_ACCESS_SECRET).ifBlank { DefaultValues.JWT_ACCESS_SECRET }

    val JWT_REFRESH_SECRET: String
        get() = stringEnv(EnvKeys.JWT_REFRESH_SECRET).ifBlank { DefaultValues.JWT_REFRESH_SECRET }

    val JWT_ACCESS_TTL_SEC: Long
        get() = longEnv(EnvKeys.JWT_ACCESS_TTL_SEC).takeIf { it > 0 } ?: DefaultValues.JWT_ACCESS_TTL_SEC

    val GOOGLE_TOKEN_AUDIENCE: String = stringEnv(EnvKeys.GOOGLE_TOKEN_AUDIENCE)
        .ifBlank { throw IllegalArgumentException("GOOGLE_TOKEN_AUDIENCE must be set") }

    fun validateSecrets() {
        if (ENV == "prod") {
            require(JWT_ACCESS_SECRET != DefaultValues.JWT_ACCESS_SECRET) { "JWT_ACCESS_SECRET must be set in prod" }
            require(JWT_REFRESH_SECRET != DefaultValues.JWT_REFRESH_SECRET) { "JWT_REFRESH_SECRET must be set in prod" }
        }
    }

    object EnvKeys {

        const val AUTH_SERVICE_SELF_HOST = "AUTH_SERVICE_SELF_HOST"

        const val DATABASE_SERVICE_HOST = "DATABASE_SERVICE_HOST"

        const val AUTH_SERVICE_PORT = "AUTH_SERVICE_PORT"

        const val DATABASE_SERVICE_PORT = "DATABASE_SERVICE_PORT"

        const val JWT_ISSUER = "JWT_ISSUER"

        const val JWT_AUDIENCE = "JWT_AUDIENCE"

        const val JWT_ACCESS_SECRET = "JWT_ACCESS_SECRET"

        const val JWT_REFRESH_SECRET = "JWT_REFRESH_SECRET"

        const val JWT_ACCESS_TTL_SEC = "JWT_ACCESS_TTL_SEC"

        const val GOOGLE_TOKEN_AUDIENCE = "GOOGLE_TOKEN_AUDIENCE"
    }

    object DefaultValues {

        const val JWT_ISSUER = "headway-auth"

        const val JWT_AUDIENCE = "headway-gateway"

        const val JWT_ACCESS_SECRET = "dev-access-secret"

        const val JWT_REFRESH_SECRET = "dev-refresh-secret"

        const val JWT_ACCESS_TTL_SEC = 300L
    }
}
