package dev.kigya.headway.common.config

import dev.kigya.headway.common.util.stringEnv

object CommonConfigurationValues {
    val ENV: String get() = stringEnv("ENV").ifBlank { "local" }
}
