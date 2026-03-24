package dev.kigya.headway.auth.internal.data.jwt

internal data class JwtConfig(
    val issuer: String,
    val audience: String,
    val accessSecret: String,
    val refreshSecret: String,
    val guestSecret: String,
    val accessTtlSec: Long,
    val guestTtlSec: Long,
)
