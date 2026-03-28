package dev.kigya.headway.gateway.domain.repository

import dev.kigya.headway.database.api.model.`in`.DatabaseLearningFacilitatedPageQuery
import dev.kigya.headway.database.api.model.`in`.DatabaseLearningRemarkCreateRequestDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningCatalogResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningPageResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningQuestionDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningRemarkDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningSearchResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningSkillGroup
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import java.util.UUID

internal interface LearningQuestionsRepositoryContract : LearningGuestSessionRevocationContract {

    suspend fun getPublicCatalog(locale: String): DatabaseLearningCatalogResponseDto

    suspend fun getCatalog(
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        locale: String,
        subjectUserId: UUID?,
    ): DatabaseLearningCatalogResponseDto

    suspend fun getPublicPage(
        locale: String,
        skillGroup: DatabaseLearningSkillGroup,
        limit: Int,
        afterQuestionId: Long?,
    ): DatabaseLearningPageResponseDto

    suspend fun getPage(
        query: DatabaseLearningFacilitatedPageQuery,
    ): DatabaseLearningPageResponseDto

    suspend fun getPublicSearch(
        locale: String,
        query: String,
    ): DatabaseLearningSearchResponseDto

    suspend fun getSearch(
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        locale: String,
        query: String,
        subjectUserId: UUID?,
    ): DatabaseLearningSearchResponseDto

    suspend fun getPublicQuestion(
        questionId: Long,
        locale: String,
    ): DatabaseLearningQuestionDto

    suspend fun getQuestion(
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        questionId: Long,
        locale: String,
        subjectUserId: UUID?,
    ): DatabaseLearningQuestionDto

    suspend fun listRemarks(
        questionId: Long,
        subjectUserId: UUID,
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
    ): List<DatabaseLearningRemarkDto>

    suspend fun addRemark(
        questionId: Long,
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        body: DatabaseLearningRemarkCreateRequestDto,
    ): DatabaseLearningRemarkDto
}
