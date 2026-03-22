package dev.kigya.headway.admin.internal.domain.usecase

import dev.kigya.headway.admin.internal.core.exception.AdminException
import dev.kigya.headway.admin.internal.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.api.model.out.DatabaseUserDepartment
import dev.kigya.headway.database.api.model.out.DatabaseUserRole

internal class InviteUserUseCase(
    private val databaseRepository: DatabaseRepositoryContract,
) {
    suspend operator fun invoke(
        email: String,
        role: DatabaseUserRole,
        department: DatabaseUserDepartment,
    ): DatabaseUser {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            throw AdminException.InvalidRequest("Invalid email")
        }

        return databaseRepository.inviteUser(
            email = trimmedEmail,
            role = role,
            department = department,
        )
    }
}
