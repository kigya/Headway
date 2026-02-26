package dev.kigya.headway.gateway.core.config

import dev.kigya.headway.common.util.intEnv
import dev.kigya.headway.common.util.stringEnv

internal object ConfigurationValues {

    val GATEWAY_SERVICE_HOST: String
        get() = stringEnv("GATEWAY_SERVICE_HOST")

    val GATEWAY_SERVICE_PORT: Int
        get() = intEnv("GATEWAY_SERVICE_PORT")

    val AUTH_SERVICE_HOST: String
        get() = stringEnv("AUTH_SERVICE_HOST")

    val AUTH_SERVICE_PORT: Int
        get() = intEnv("AUTH_SERVICE_PORT")

    val DATABASE_SERVICE_HOST: String
        get() = stringEnv("DATABASE_SERVICE_HOST")

    val DATABASE_SERVICE_PORT: Int
        get() = intEnv("DATABASE_SERVICE_PORT")
}
