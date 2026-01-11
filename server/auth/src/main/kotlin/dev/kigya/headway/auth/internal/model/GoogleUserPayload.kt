package dev.kigya.headway.auth.internal.model

internal data class GoogleUserPayload(
    val googleId: String,
    val email: String,
    val isEmailVerified: Boolean,
    val name: String,
    val pictureUrl: String?,
)
