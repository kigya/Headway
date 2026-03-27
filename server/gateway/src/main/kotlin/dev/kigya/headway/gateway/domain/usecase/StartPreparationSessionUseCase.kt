package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.database.api.model.`in`.DatabasePreparationStartSessionRequestDto
import dev.kigya.headway.gateway.domain.preparation.requirePreparationFacilitator
import dev.kigya.headway.gateway.domain.repository.PreparationRepositoryContract
import dev.kigya.headway.gateway.mapping.toDatabase
import dev.kigya.headway.gateway.mapping.toGateway
import dev.kigya.headway.gateway.model.GatewayPreparationFormatCode
import dev.kigya.headway.gateway.model.GatewayPreparationQuestionDomain
import dev.kigya.headway.gateway.model.GatewayPreparationSessionState
import dev.kigya.headway.gateway.model.GatewayUser
import java.util.UUID

internal class StartPreparationSessionUseCase(
    private val preparationRepository: PreparationRepositoryContract,
) {
    suspend operator fun invoke(
        user: GatewayUser,
        subjectUserId: UUID,
        formatCode: GatewayPreparationFormatCode,
        preparationLanguage: String?,
        customDomainFilter: GatewayPreparationQuestionDomain?,
    ): GatewayPreparationSessionState {
        val facilitator = requirePreparationFacilitator(user)
        val started = preparationRepository.startSession(
            facilitatorId = facilitator.id,
            facilitatorRole = facilitator.role.toDatabase(),
            body = DatabasePreparationStartSessionRequestDto(
                subjectUserId = subjectUserId,
                formatCode = formatCode.toDatabase(),
                preparationLanguage = preparationLanguage,
                customDomainFilter = customDomainFilter?.toDatabase(),
            ),
        )
        return started.state.toGateway()
    }
}
