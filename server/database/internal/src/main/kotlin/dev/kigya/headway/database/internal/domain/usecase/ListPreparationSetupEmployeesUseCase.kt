package dev.kigya.headway.database.internal.domain.usecase

import dev.kigya.headway.database.api.model.out.DatabasePreparationEmployeeDto
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.internal.domain.PreparationFacilitatorPolicy
import dev.kigya.headway.database.internal.domain.repository.PreparationRepositoryContract
import java.util.UUID

internal class ListPreparationSetupEmployeesUseCase(
    private val preparationRepository: PreparationRepositoryContract,
) {
    suspend operator fun invoke(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
    ): List<DatabasePreparationEmployeeDto> {
        PreparationFacilitatorPolicy.ensurePreparationRole(facilitatorRole)
        return preparationRepository.listSetupEmployees(
            facilitatorId = facilitatorId,
            facilitatorRole = facilitatorRole,
        )
    }
}
