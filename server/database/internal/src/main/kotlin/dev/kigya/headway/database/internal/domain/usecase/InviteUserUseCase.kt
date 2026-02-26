package dev.kigya.headway.database.internal.domain.usecase

import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.internal.domain.repository.UsersRepositoryContract

internal class InviteUserUseCase(
    private val usersRepository: UsersRepositoryContract,
) {
    suspend operator fun invoke(email: String, department: String): DatabaseUser =
        usersRepository.inviteUser(email = email, department = department)
}
