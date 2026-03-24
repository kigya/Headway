package dev.kigya.headway.admin.api.model.`in`

import dev.kigya.headway.database.api.model.out.DatabaseUserDepartment
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminInviteUserPayloadDto(
    @SerialName("email")
    val email: String,
    @SerialName("role")
    val role: DatabaseUserRole,
    @SerialName("department")
    val department: DatabaseUserDepartment,
)
