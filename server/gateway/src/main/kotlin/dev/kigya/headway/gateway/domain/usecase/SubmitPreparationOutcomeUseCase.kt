package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.database.api.model.`in`.DatabasePreparationSubmitOutcomeRequestDto
import dev.kigya.headway.gateway.domain.preparation.requirePreparationFacilitator
import dev.kigya.headway.gateway.domain.repository.PreparationRepositoryContract
import dev.kigya.headway.gateway.mapping.toDatabase
import dev.kigya.headway.gateway.mapping.toGateway
import dev.kigya.headway.gateway.model.GatewayPreparationOutcomeCode
import dev.kigya.headway.gateway.model.GatewayPreparationSessionState
import dev.kigya.headway.gateway.model.GatewayUser
import java.util.UUID

internal class SubmitPreparationOutcomeUseCase(
    private val preparationRepository: PreparationRepositoryContract,
) {
    suspend operator fun invoke(
        user: GatewayUser,
        sessionId: UUID,
        sessionQuestionId: UUID,
        outcome: GatewayPreparationOutcomeCode,
        comment: String?,
    ): GatewayPreparationSessionState {
        val facilitator = requirePreparationFacilitator(user)
        val dto = preparationRepository.submitOutcome(
            facilitatorId = facilitator.id,
            facilitatorRole = facilitator.role.toDatabase(),
            sessionId = sessionId,
            body = DatabasePreparationSubmitOutcomeRequestDto(
                sessionQuestionId = sessionQuestionId,
                outcome = outcome.toDatabase(),
                comment = comment,
            ),
        )
        return dto.toGateway()
    }
}
