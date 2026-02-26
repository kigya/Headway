package dev.kigya.headway.auth.api.model.`in`

data class AuthGoogleUserPayloadDto(
    val googleId: String,
    val email: String,
    val name: String,
    val pictureUrl: String?,
)
