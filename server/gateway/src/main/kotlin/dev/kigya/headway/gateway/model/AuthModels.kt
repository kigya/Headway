package dev.kigya.headway.gateway.model

import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
internal data class GatewayUser(
    @SerialName("id") @Serializable(UUIDSerializer::class) val id: UUID,
    @SerialName("email") val email: String,
    @SerialName("name") val name: String,
    @SerialName("role") val role: GatewayUserRole,
    @SerialName("avatarUrl") val avatarUrl: String? = null,
    @SerialName("department") val department: GatewayUserDepartment? = null,
)

@Serializable
internal data class GatewayGoogleLoginResponse(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
    @SerialName("user") val user: GatewayUser,
)

@Serializable
internal data class GatewayRefreshAccessTokenResponse(
    @SerialName("accessToken") val accessToken: String,
)

@Serializable
internal enum class GatewayUserRole {
    @SerialName("DEVELOPER")
    DEVELOPER,

    @SerialName("MANAGER")
    MANAGER,

    @SerialName("MENTOR")
    MENTOR,

    @SerialName("EMPLOYEE")
    EMPLOYEE,

    @SerialName("GUEST")
    GUEST;
}

@Serializable
internal enum class GatewayUserDepartment {
    @SerialName("ANDROID")
    ANDROID,

    @SerialName("IOS")
    IOS,

    @SerialName("CROSS_PLATFORM")
    CROSSPLATFORM;
}

@Serializable
internal enum class GatewaySessionPlatform {
    @SerialName("ANDROID")
    ANDROID,

    @SerialName("IOS")
    IOS,

    @SerialName("DESKTOP")
    DESKTOP,

    @SerialName("WEB")
    WEB;
}
