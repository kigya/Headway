package dev.kigya.headway.auth.internal.config

internal object ConfigurationValues {

    val AUTH_SERVICE_HOST: String
        get() = System.getenv("AUTH_SERVICE_HOST")

    val AUTH_SERVICE_PORT: Int
        get() = System.getenv("AUTH_SERVICE_PORT").toInt()

    val DATABASE_SERVICE_HOST: String
        get() = System.getenv("DATABASE_SERVICE_HOST")

    val DATABASE_SERVICE_PORT: Int
        get() = System.getenv("DATABASE_SERVICE_PORT").toInt()

    val DATABASE_SERVICE_URL: String
        get() = "http://$DATABASE_SERVICE_HOST:$DATABASE_SERVICE_PORT/internal/v1/database"
}
