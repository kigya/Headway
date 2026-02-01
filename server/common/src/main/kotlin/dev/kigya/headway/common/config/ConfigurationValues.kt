package dev.kigya.headway.common.config

import dev.kigya.headway.common.extension.stringEnv

object ConfigurationValues {
    val ENV: String get() = stringEnv("ENV").ifBlank { "local" }
}
