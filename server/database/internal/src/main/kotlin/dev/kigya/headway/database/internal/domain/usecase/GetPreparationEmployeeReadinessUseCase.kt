package dev.kigya.headway.database.internal.domain.usecase

import dev.kigya.headway.database.api.model.out.DatabasePreparationReadinessResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.internal.domain.repository.PreparationRepositoryContract
import java.util.UUID

internal class GetPreparationEmployeeReadinessUseCase(
    private val preparationRepository: PreparationRepositoryContract,
) {
    suspend operator fun invoke(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        subjectUserId: UUID,
    ): DatabasePreparationReadinessResponseDto = preparationRepository.getEmployeeReadiness(
        facilitatorId = facilitatorId,
        facilitatorRole = facilitatorRole,
        subjectUserId = subjectUserId,
    )
}
