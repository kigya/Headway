package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.common.exception.BadRequestException
import dev.kigya.headway.common.model.CommonApiError
import dev.kigya.headway.gateway.client.DatabaseServiceClientContract
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.port.InviteUserUseCaseContract
import dev.kigya.headway.gateway.internal.client.DatabaseServiceClientContract
import dev.kigya.headway.gateway.internal.mapping.toPublic

internal class InviteUserUseCase(
    private val databaseClient: DatabaseServiceClientContract,
) : InviteUserUseCaseContract {

    override suspend fun invoke(email: String, department: String): GatewayUser {
        val trimmedEmail = email.trim()
        val trimmedDepartment = department.trim()

        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            throw BadRequestException(CommonApiError("Invalid email"))
        }
        if (trimmedDepartment.isBlank()) {
            throw BadRequestException(CommonApiError("Invalid department"))
        }

        val dto = databaseClient.inviteUser(
            email = trimmedEmail,
            department = trimmedDepartment,
        )
        return dto.toPublic()
    }
}
