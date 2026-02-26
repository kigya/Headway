package dev.kigya.headway.database.internal.core.config

import dev.kigya.headway.common.config.CommonConfigurationValues.ENV
import dev.kigya.headway.common.util.intEnv
import dev.kigya.headway.common.util.stringEnv

internal object ConfigurationValues {

    val DATABASE_SERVICE_HOST: String
        get() = stringEnv("DATABASE_SERVICE_HOST")

    val DATABASE_SERVICE_PORT: Int
        get() = intEnv("DATABASE_SERVICE_PORT")

    val DATABASE_HOST_URL: String get() = stringEnv("DATABASE_HOST_URL")
    val DATABASE_PORT: Int get() = intEnv("DATABASE_PORT")
    val DATABASE_NAME: String get() = stringEnv("DATABASE_NAME")
    val DATABASE_USER: String get() = stringEnv("DATABASE_USER")
    val DATABASE_PASSWORD: String get() = stringEnv("DATABASE_PASSWORD")
    val DATABASE_SSL_MODE: String get() = stringEnv("DATABASE_SSL_MODE").ifBlank { "require" }
    val DATABASE_POOL_SIZE: Int get() = intEnv("DATABASE_POOL_SIZE").takeIf { it > 0 } ?: 10

    fun validateSecrets() {
        if (ENV == "prod") {
            require(DATABASE_PASSWORD.isNotBlank()) { "DATABASE_PASSWORD must be set in prod" }
        }
    }
}
