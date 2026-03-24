package dev.kigya.headway.admin.api.model.out

import dev.kigya.headway.database.api.model.out.DatabaseUserDepartment
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminInviteUserResponse(
    @SerialName("message")
    val message: String,
    @SerialName("invited_email")
    val invitedEmail: String,
    @SerialName("role")
    val role: DatabaseUserRole,
    @SerialName("department")
    val department: DatabaseUserDepartment,
)
