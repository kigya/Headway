package dev.kigya.headway.database.internal.core.config

import dev.kigya.headway.common.config.CommonConfigurationValues.ENV
import dev.kigya.headway.common.util.intEnv
import dev.kigya.headway.common.util.stringEnv

internal object ConfigurationValues {

    val DATABASE_SERVICE_HOST: String
        get() = stringEnv(EnvKeys.DATABASE_SELF_SERVICE_HOST)

    val DATABASE_SERVICE_PORT: Int
        get() = intEnv(EnvKeys.DATABASE_SERVICE_PORT)

    val DATABASE_HOST_URL: String get() = stringEnv(EnvKeys.DATABASE_HOST_URL)
    val DATABASE_PORT: Int get() = intEnv(EnvKeys.DATABASE_PORT)
    val DATABASE_NAME: String get() = stringEnv(EnvKeys.DATABASE_NAME)
    val DATABASE_USER: String get() = stringEnv(EnvKeys.DATABASE_USER)
    val DATABASE_PASSWORD: String get() = stringEnv(EnvKeys.DATABASE_PASSWORD)
    val DATABASE_SSL_MODE: String get() = stringEnv(EnvKeys.DATABASE_SSL_MODE).ifBlank { "require" }
    val DATABASE_POOL_SIZE: Int get() = intEnv(EnvKeys.DATABASE_POOL_SIZE).takeIf {
        it > 0
    } ?: DefaultValues.DATABASE_POOL_SIZE

    fun validateSecrets() {
        if (ENV == "prod") {
            require(DATABASE_PASSWORD.isNotBlank()) { "DATABASE_PASSWORD must be set in prod" }
        }
    }

    object EnvKeys {

        const val DATABASE_SELF_SERVICE_HOST = "DATABASE_SELF_SERVICE_HOST"

        const val DATABASE_SERVICE_PORT = "DATABASE_SERVICE_PORT"

        const val DATABASE_HOST_URL = "DATABASE_HOST_URL"

        const val DATABASE_PORT = "DATABASE_PORT"

        const val DATABASE_NAME = "DATABASE_NAME"

        const val DATABASE_USER = "DATABASE_USER"

        const val DATABASE_PASSWORD = "DATABASE_PASSWORD"

        const val DATABASE_SSL_MODE = "DATABASE_SSL_MODE"

        const val DATABASE_POOL_SIZE = "DATABASE_POOL_SIZE"
    }

    object DefaultValues {

        const val DATABASE_POOL_SIZE = 10

        const val DATABASE_SSL_MODE = "require"
    }
}
