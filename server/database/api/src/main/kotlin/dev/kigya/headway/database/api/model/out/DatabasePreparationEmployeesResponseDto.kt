package dev.kigya.headway.database.api.model.out

import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DatabasePreparationEmployeesResponseDto(
    @SerialName("employees")
    val employees: List<DatabasePreparationEmployeeDto>,
)

@Serializable
data class DatabasePreparationEmployeeDto(
    @SerialName("id")
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("department")
    val department: DatabaseUserDepartment?,
    @SerialName("avatar_url")
    val avatarUrl: String?,
)
