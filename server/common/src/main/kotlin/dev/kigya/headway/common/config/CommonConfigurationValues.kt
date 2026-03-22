package dev.kigya.headway.common.config

import dev.kigya.headway.common.util.Environment
import dev.kigya.headway.common.util.environmentEnv

object CommonConfigurationValues {
    val environment: Environment
        get() = environmentEnv("ENV")

    val ENV: String
        get() = environment.rawValue
}
