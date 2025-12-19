package dev.kigya.headway

internal object ConfigurationValues {

    val GATEWAY_SERVICE_HOST: String
        get() = System.getenv("GATEWAY_SERVICE_HOST")

    val GATEWAY_SERVICE_PORT: Int
        get() = System.getenv("GATEWAY_SERVICE_PORT").toInt()

    val AUTH_SERVICE_HOST: String
        get() = System.getenv("AUTH_SERVICE_HOST")

    val AUTH_SERVICE_PORT: Int
        get() = System.getenv("AUTH_SERVICE_PORT").toInt()

    val AUTH_SERVICE_URL: String
        get() = "http://$AUTH_SERVICE_HOST:$AUTH_SERVICE_PORT/internal/v1/auth"
}
