package dev.kigya.headway.admin.internal.domain.repository

import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.api.model.out.DatabaseUserDepartment
import dev.kigya.headway.database.api.model.out.DatabaseUserRole

internal interface DatabaseRepositoryContract {
    suspend fun inviteUser(
        email: String,
        role: DatabaseUserRole,
        department: DatabaseUserDepartment,
    ): DatabaseUser
}
