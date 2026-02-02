package dev.kigya.headway.database.internal.config

internal data class DatabaseConfig(
    val host: String,
    val port: Int,
    val name: String,
    val user: String,
    val password: String,
    val sslMode: String,
    val poolSize: Int,
)
