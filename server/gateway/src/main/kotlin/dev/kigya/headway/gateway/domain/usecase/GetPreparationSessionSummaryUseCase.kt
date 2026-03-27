package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.domain.preparation.requirePreparationFacilitator
import dev.kigya.headway.gateway.domain.repository.PreparationRepositoryContract
import dev.kigya.headway.gateway.graphql.GatewayAppLocale
import dev.kigya.headway.gateway.mapping.toCatalogLocale
import dev.kigya.headway.gateway.mapping.toDatabase
import dev.kigya.headway.gateway.mapping.toGateway
import dev.kigya.headway.gateway.model.GatewayPreparationSessionSummary
import dev.kigya.headway.gateway.model.GatewayUser
import java.util.UUID

internal class GetPreparationSessionSummaryUseCase(
    private val preparationRepository: PreparationRepositoryContract,
) {
    suspend operator fun invoke(
        user: GatewayUser,
        sessionId: UUID,
        locale: GatewayAppLocale,
    ): GatewayPreparationSessionSummary {
        val facilitator = requirePreparationFacilitator(user)
        val dto = preparationRepository.getSessionSummary(
            facilitatorId = facilitator.id,
            facilitatorRole = facilitator.role.toDatabase(),
            sessionId = sessionId,
            locale = locale.toCatalogLocale(),
        )
        return dto.toGateway()
    }
}
