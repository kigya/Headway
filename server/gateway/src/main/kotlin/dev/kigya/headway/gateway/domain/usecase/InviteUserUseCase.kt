package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.gateway.model.GatewayUser

internal class InviteUserUseCase(
    private val databaseRepository: DatabaseRepositoryContract,
) {
    suspend operator fun invoke(
        email: String,
        department: String,
    ): GatewayUser {
        val trimmedEmail = email.trim()
        val trimmedDepartment = department.trim()

        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            throw GatewayException.InvalidRequest("Invalid email")
        }
        if (trimmedDepartment.isBlank()) {
            throw GatewayException.InvalidRequest("Invalid department")
        }

        return databaseRepository.inviteUser(
            email = trimmedEmail,
            department = trimmedDepartment,
        )
    }
}
