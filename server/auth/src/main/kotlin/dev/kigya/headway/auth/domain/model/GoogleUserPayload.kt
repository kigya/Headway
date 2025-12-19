package dev.kigya.headway.auth.domain.model

internal data class GoogleUserPayload(
    val googleId: String,
    val email: String,
    val isEmailVerified: Boolean,
    val name: String,
    val pictureUrl: String?,
)
