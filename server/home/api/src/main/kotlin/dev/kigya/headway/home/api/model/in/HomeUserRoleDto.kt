package dev.kigya.headway.home.api.model.`in`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HomeUserRoleDto {
    @SerialName("DEVELOPER")
    DEVELOPER,

    @SerialName("MANAGER")
    MANAGER,

    @SerialName("MENTOR")
    MENTOR,

    @SerialName("EMPLOYEE")
    EMPLOYEE,

    @SerialName("GUEST")
    GUEST,
}
