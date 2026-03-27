package dev.kigya.headway.database.internal.domain.repository

import dev.kigya.headway.database.api.model.out.DatabasePreparationCatalogResponseDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationEmployeeDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationFormatCode
import dev.kigya.headway.database.api.model.out.DatabasePreparationOutcomeCode
import dev.kigya.headway.database.api.model.out.DatabasePreparationQuestionDomain
import dev.kigya.headway.database.api.model.out.DatabasePreparationReadinessResponseDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionStateDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionSummaryDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationStartSessionResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import java.util.UUID

internal interface PreparationRepositoryContract {

    suspend fun listSetupEmployees(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
    ): List<DatabasePreparationEmployeeDto>

    suspend fun getEmployeeReadiness(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        subjectUserId: UUID,
    ): DatabasePreparationReadinessResponseDto

    suspend fun listFormatCatalog(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        locale: String,
    ): DatabasePreparationCatalogResponseDto

    suspend fun startSession(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        subjectUserId: UUID,
        formatCode: DatabasePreparationFormatCode,
        preparationLanguage: String,
        customDomainFilter: DatabasePreparationQuestionDomain?,
    ): DatabasePreparationStartSessionResponseDto

    suspend fun getSessionState(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
    ): DatabasePreparationSessionStateDto

    suspend fun submitOutcome(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        sessionQuestionId: UUID,
        outcome: DatabasePreparationOutcomeCode,
        comment: String?,
    ): DatabasePreparationSessionStateDto

    suspend fun selectQuestion(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        sessionQuestionId: UUID,
    ): DatabasePreparationSessionStateDto

    suspend fun finishSession(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
    ): DatabasePreparationSessionStateDto

    suspend fun getSessionSummary(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        locale: String,
    ): DatabasePreparationSessionSummaryDto
}
