package dev.kigya.headway.internal.config

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

private fun stringEnv(name: String): String =
    requireNotNull(System.getenv(name)) { "Missing env var: $name" }

private fun intEnv(name: String): Int =
    requireNotNull(System.getenv(name)) { "Missing env var: $name" }.toIntOrNull()
        ?: error("Invalid env var: $name")
