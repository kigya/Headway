package dev.kigya.headway.auth.api.model

import dev.kigya.headway.auth.api.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user") val user: User,
)

@Serializable
data class RefreshTokenResponse(
    @SerialName("access_token") val accessToken: String,
)

@Serializable
data class User(
    @SerialName("id") @Serializable(UUIDSerializer::class) val id: UUID,
    @SerialName("email") val email: String,
    @SerialName("google_id") val googleId: String?,
    @SerialName("name") val name: String,
    @SerialName("role") val role: UserRole,
    @SerialName("avatar_url") val avatarUrl: String?,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
)

@Serializable
enum class UserRole(val slug: String) {
    DEVELOPER("developer"),
    MANAGER("manager"),
    MENTOR("mentor"),
    EMPLOYEE("employee"),
    GUEST("guest");
}

@Serializable
internal data class LoginRequest(
    @SerialName("id_token") val idToken: String,
    @SerialName("fingerprint") val fingerprint: String,
)
