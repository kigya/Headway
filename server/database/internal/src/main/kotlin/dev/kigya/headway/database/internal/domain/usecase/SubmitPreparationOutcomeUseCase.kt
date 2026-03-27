package dev.kigya.headway.database.internal.domain.usecase

import dev.kigya.headway.database.api.model.out.DatabasePreparationOutcomeCode
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionStateDto
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.internal.domain.repository.PreparationRepositoryContract
import java.util.UUID

internal class SubmitPreparationOutcomeUseCase(
    private val preparationRepository: PreparationRepositoryContract,
) {
    suspend operator fun invoke(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        sessionQuestionId: UUID,
        outcome: DatabasePreparationOutcomeCode,
        comment: String?,
    ): DatabasePreparationSessionStateDto = preparationRepository.submitOutcome(
        facilitatorId = facilitatorId,
        facilitatorRole = facilitatorRole,
        sessionId = sessionId,
        sessionQuestionId = sessionQuestionId,
        outcome = outcome,
        comment = comment,
    )
}
