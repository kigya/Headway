package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.database.api.model.`in`.DatabasePreparationSelectQuestionRequestDto
import dev.kigya.headway.gateway.domain.preparation.requirePreparationFacilitator
import dev.kigya.headway.gateway.domain.repository.PreparationRepositoryContract
import dev.kigya.headway.gateway.mapping.toDatabase
import dev.kigya.headway.gateway.mapping.toGateway
import dev.kigya.headway.gateway.model.GatewayPreparationSessionState
import dev.kigya.headway.gateway.model.GatewayUser
import java.util.UUID

internal class SelectPreparationSessionQuestionUseCase(
    private val preparationRepository: PreparationRepositoryContract,
) {
    suspend operator fun invoke(
        user: GatewayUser,
        sessionId: UUID,
        sessionQuestionId: UUID,
    ): GatewayPreparationSessionState {
        val facilitator = requirePreparationFacilitator(user)
        val dto = preparationRepository.selectQuestion(
            facilitatorId = facilitator.id,
            facilitatorRole = facilitator.role.toDatabase(),
            sessionId = sessionId,
            body = DatabasePreparationSelectQuestionRequestDto(
                sessionQuestionId = sessionQuestionId,
            ),
        )
        return dto.toGateway()
    }
}
