package dev.kigya.headway.gateway.domain.repository

import dev.kigya.headway.database.api.model.`in`.DatabasePreparationSelectQuestionRequestDto
import dev.kigya.headway.database.api.model.`in`.DatabasePreparationStartSessionRequestDto
import dev.kigya.headway.database.api.model.`in`.DatabasePreparationSubmitOutcomeRequestDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationCatalogResponseDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationEmployeesResponseDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationReadinessResponseDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionStateDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionSummaryDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationStartSessionResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import java.util.UUID

internal interface PreparationRepositoryContract {

    suspend fun getSetupEmployees(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
    ): DatabasePreparationEmployeesResponseDto

    suspend fun getEmployeeReadiness(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        subjectUserId: UUID,
    ): DatabasePreparationReadinessResponseDto

    suspend fun getFormatCatalog(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        locale: String,
    ): DatabasePreparationCatalogResponseDto

    suspend fun startSession(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        body: DatabasePreparationStartSessionRequestDto,
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
        body: DatabasePreparationSubmitOutcomeRequestDto,
    ): DatabasePreparationSessionStateDto

    suspend fun selectQuestion(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        body: DatabasePreparationSelectQuestionRequestDto,
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
