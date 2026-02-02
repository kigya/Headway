package dev.kigya.headway.database.api.model

import dev.kigya.headway.database.api.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ExposedUser(
    @SerialName("id") @Serializable(UUIDSerializer::class) val id: UUID,
    @SerialName("email") val email: String,
    @SerialName("google_id") val googleId: String?,
    @SerialName("name") val name: String,
    @SerialName("role") val role: ExposedUserRole,
    @SerialName("department") val department: ExposedDepartment,
    @SerialName("avatar_url") val avatarUrl: String?,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
)
