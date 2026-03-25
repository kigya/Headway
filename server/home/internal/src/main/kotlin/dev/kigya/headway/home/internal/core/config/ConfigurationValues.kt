package dev.kigya.headway.home.internal.core.config

import dev.kigya.headway.common.config.CommonConfigurationValues
import dev.kigya.headway.common.util.intEnv
import dev.kigya.headway.common.util.stringEnv

internal object ConfigurationValues {

    val HOME_SERVICE_HOST: String
        get() = stringEnv(EnvKeys.HOME_SERVICE_SELF_HOST)

    val HOME_SERVICE_PORT: Int
        get() = intEnv(EnvKeys.HOME_SERVICE_PORT)

    val HOME_ICONS_PUBLIC_BASE_URL: String
        get() = stringEnv(EnvKeys.HOME_ICONS_PUBLIC_BASE_URL).trim()

    fun validateSecrets() {
        if (CommonConfigurationValues.environment.isProd) {
            require(HOME_ICONS_PUBLIC_BASE_URL.isNotBlank()) {
                "HOME_ICONS_PUBLIC_BASE_URL must be set in prod"
            }
        }
    }

    object EnvKeys {

        const val HOME_SERVICE_SELF_HOST = "HOME_SERVICE_SELF_HOST"

        const val HOME_SERVICE_PORT = "HOME_SERVICE_PORT"

        const val HOME_ICONS_PUBLIC_BASE_URL = "HOME_ICONS_PUBLIC_BASE_URL"
    }
}
