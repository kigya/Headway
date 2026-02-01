package dev.kigya.headway.auth.internal.config

import dev.kigya.headway.common.config.ConfigurationValues.ENV
import dev.kigya.headway.common.extension.intEnv
import dev.kigya.headway.common.extension.longEnv
import dev.kigya.headway.common.extension.stringEnv

internal object ConfigurationValues {

    val AUTH_SERVICE_HOST: String
        get() = stringEnv("AUTH_SERVICE_HOST")

    val AUTH_SERVICE_PORT: Int
        get() = intEnv("AUTH_SERVICE_PORT")

    val DATABASE_SERVICE_HOST: String
        get() = stringEnv("DATABASE_SERVICE_HOST")

    val DATABASE_SERVICE_PORT: Int
        get() = intEnv("DATABASE_SERVICE_PORT")

    val JWT_ISSUER: String
        get() = stringEnv("JWT_ISSUER").ifBlank { "headway-auth" }
    val JWT_AUDIENCE: String
        get() = stringEnv("JWT_AUDIENCE").ifBlank { "headway-gateway" }

    val JWT_ACCESS_SECRET: String
        get() = stringEnv("JWT_ACCESS_SECRET").ifBlank { "dev-access-secret" }

    val JWT_REFRESH_SECRET: String
        get() = stringEnv("JWT_REFRESH_SECRET").ifBlank { "dev-refresh-secret" }

    val JWT_ACCESS_TTL_SEC: Long get() = longEnv("JWT_ACCESS_TTL_SEC").takeIf { it > 0 } ?: 300L

    val DATABASE_SERVICE_URL: String
        get() = "http://$DATABASE_SERVICE_HOST:$DATABASE_SERVICE_PORT/internal/v1/database"

    fun validateSecrets() {
        if (ENV == "prod") {
            require(JWT_ACCESS_SECRET != "dev-access-secret") { "JWT_ACCESS_SECRET must be set in prod" }
            require(JWT_REFRESH_SECRET != "dev-refresh-secret") { "JWT_REFRESH_SECRET must be set in prod" }
        }
    }
}
