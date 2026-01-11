package dev.kigya.headway.database.internal.config

internal object ConfigurationValues {

    val DATABASE_SERVICE_HOST: String
        get() = System.getenv("DATABASE_SERVICE_HOST")

    val DATABASE_SERVICE_PORT: Int
        get() = System.getenv("DATABASE_SERVICE_PORT").toInt()
}
