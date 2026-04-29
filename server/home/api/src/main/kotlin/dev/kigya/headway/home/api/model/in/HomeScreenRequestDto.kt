package dev.kigya.headway.home.api.model.`in`

import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class HomeScreenRequestDto(
    @SerialName("user_id")
    @Serializable(UUIDSerializer::class)
    val userId: UUID,
    @SerialName("user_name")
    val userName: String,
    @SerialName("user_role")
    val userRole: HomeUserRoleDto,
    @SerialName("user_avatar_url")
    val userAvatarUrl: String? = null,
    @SerialName("locale")
    val locale: HomeAppLocaleDto,
)
