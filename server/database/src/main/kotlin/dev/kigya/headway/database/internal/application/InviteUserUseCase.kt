package dev.kigya.headway.database.internal.application

import dev.kigya.headway.database.api.model.ExposedUser
import dev.kigya.headway.database.api.port.InviteUserUseCaseContract
import dev.kigya.headway.database.internal.data.UsersRepositoryContract

internal class InviteUserUseCase(
    private val usersRepository: UsersRepositoryContract,
) : InviteUserUseCaseContract {
    override suspend fun invoke(email: String, department: String): ExposedUser {
        return usersRepository.inviteUser(email = email, department = department)
    }
}
