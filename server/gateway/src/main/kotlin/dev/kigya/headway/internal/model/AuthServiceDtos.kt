package dev.kigya.headway.internal.model

import ext.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
internal data class AuthServiceAuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user") val user: AuthServiceUserDto,
)

@Serializable
internal data class AuthServiceRefreshTokenResponse(
    @SerialName("access_token") val accessToken: String,
)

@Serializable
internal data class LoginRequest(
    @SerialName("id_token") val idToken: String,
    @SerialName("fingerprint") val fingerprint: String,
)

@Serializable
internal data class AuthServiceUserDto(
    @SerialName("id") @Serializable(UUIDSerializer::class) val id: UUID,
    @SerialName("email") val email: String,
    @SerialName("google_id") val googleId: String? = null,
    @SerialName("name") val name: String,
    @SerialName("role") val role: AuthServiceUserRoleDto,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
)

@Serializable
internal enum class AuthServiceUserRoleDto {
    DEVELOPER,
    MANAGER,
    MENTOR,
    EMPLOYEE,
    GUEST;
}
