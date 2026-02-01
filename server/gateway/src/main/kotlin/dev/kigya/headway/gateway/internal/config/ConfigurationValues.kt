package dev.kigya.headway.gateway.internal.config

import dev.kigya.headway.common.extension.intEnv
import dev.kigya.headway.common.extension.stringEnv

internal object ConfigurationValues {

    val GATEWAY_SERVICE_HOST: String
        get() = stringEnv("GATEWAY_SERVICE_HOST")

    val GATEWAY_SERVICE_PORT: Int
        get() = intEnv("GATEWAY_SERVICE_PORT")

    val AUTH_SERVICE_HOST: String
        get() = stringEnv("AUTH_SERVICE_HOST")

    val AUTH_SERVICE_PORT: Int
        get() = intEnv("AUTH_SERVICE_PORT")

    val AUTH_SERVICE_URL: String
        get() = "http://$AUTH_SERVICE_HOST:$AUTH_SERVICE_PORT/internal/v1/auth"
}
