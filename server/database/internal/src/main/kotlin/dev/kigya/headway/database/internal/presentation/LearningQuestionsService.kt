package dev.kigya.headway.database.internal.presentation

import dev.kigya.headway.database.api.model.`in`.DatabaseLearningFacilitatedPageQuery
import dev.kigya.headway.database.api.model.`in`.DatabaseLearningRemarkCreateRequestDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningCatalogResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningPageResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningQuestionDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningRemarkDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningSearchResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningSkillGroup
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.internal.data.repository.LearningQuestionRemarksRepository
import dev.kigya.headway.database.internal.data.repository.LearningQuestionsReadRepository
import dev.kigya.headway.database.internal.data.scope.ensureEmployeeSelfSubject
import dev.kigya.headway.database.internal.data.scope.isSubjectInFacilitatorLearningScope
import dev.kigya.headway.database.internal.domain.error.DatabaseException
import java.util.UUID

internal class LearningQuestionsService(
    private val readRepository: LearningQuestionsReadRepository,
    private val remarksRepository: LearningQuestionRemarksRepository,
) {

    suspend fun getPublicCatalog(locale: String): DatabaseLearningCatalogResponseDto =
        readRepository.loadPublicCatalog(locale = locale)

    suspend fun getCatalog(
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        locale: String,
        subjectUserId: UUID?,
    ): DatabaseLearningCatalogResponseDto = readRepository.loadCatalog(
        facilitatorUserId = facilitatorUserId,
        facilitatorRole = facilitatorRole,
        locale = locale,
        subjectUserId = subjectUserId,
    )

    suspend fun getPublicPage(
        locale: String,
        skillGroup: DatabaseLearningSkillGroup,
        limit: Int,
        afterQuestionId: Long?,
    ): DatabaseLearningPageResponseDto = readRepository.loadPublicPage(
        locale = locale,
        skillGroup = skillGroup,
        limit = limit.coerceIn(LEARNING_PAGE_MIN, LEARNING_PAGE_MAX),
        afterQuestionId = afterQuestionId,
    )

    suspend fun getPage(
        query: DatabaseLearningFacilitatedPageQuery,
    ): DatabaseLearningPageResponseDto = readRepository.loadPage(
        query = query.copy(
            limit = query.limit.coerceIn(LEARNING_PAGE_MIN, LEARNING_PAGE_MAX),
        ),
    )

    suspend fun getPublicSearch(
        locale: String,
        query: String,
    ): DatabaseLearningSearchResponseDto =
        readRepository.loadPublicSearch(locale = locale, query = query)

    suspend fun getSearch(
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        locale: String,
        query: String,
        subjectUserId: UUID?,
    ): DatabaseLearningSearchResponseDto = readRepository.loadSearch(
        facilitatorUserId = facilitatorUserId,
        facilitatorRole = facilitatorRole,
        locale = locale,
        query = query,
        subjectUserId = subjectUserId,
    )

    suspend fun getPublicQuestion(
        questionId: Long,
        locale: String,
    ): DatabaseLearningQuestionDto =
        readRepository.loadPublicQuestionById(questionId = questionId, locale = locale)

    suspend fun getQuestion(
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        questionId: Long,
        locale: String,
        subjectUserId: UUID?,
    ): DatabaseLearningQuestionDto = readRepository.loadQuestionById(
        facilitatorUserId = facilitatorUserId,
        facilitatorRole = facilitatorRole,
        questionId = questionId,
        locale = locale,
        subjectUserId = subjectUserId,
    )

    suspend fun listRemarks(
        questionId: Long,
        subjectUserId: UUID,
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
    ): List<DatabaseLearningRemarkDto> {
        resolveSubjectForRemarks(
            facilitatorUserId = facilitatorUserId,
            facilitatorRole = facilitatorRole,
            subjectUserId = subjectUserId,
        )
        return remarksRepository.listRemarks(
            questionId = questionId,
            subjectUserId = subjectUserId,
        )
    }

    suspend fun addRemark(
        questionId: Long,
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        body: DatabaseLearningRemarkCreateRequestDto,
    ): DatabaseLearningRemarkDto {
        val subjectUserId = body.subjectUserId
        resolveSubjectForRemarks(
            facilitatorUserId = facilitatorUserId,
            facilitatorRole = facilitatorRole,
            subjectUserId = subjectUserId,
        )
        val trimmed = body.body.trim()
        if (trimmed.isEmpty()) {
            throw DatabaseException.InvalidRequest(
                message = "Remark body is blank",
            )
        }
        if (trimmed.length > REMARK_BODY_MAX_LENGTH) {
            throw DatabaseException.InvalidRequest(
                message = "Remark body exceeds maximum length",
            )
        }
        return remarksRepository.insertRemark(
            questionId = questionId,
            subjectUserId = subjectUserId,
            authorUserId = facilitatorUserId,
            body = trimmed,
        )
    }
}

private fun resolveSubjectForRemarks(
    facilitatorUserId: UUID,
    facilitatorRole: DatabaseUserRole,
    subjectUserId: UUID,
) {
    ensureEmployeeSelfSubject(
        facilitatorId = facilitatorUserId,
        facilitatorRole = facilitatorRole,
        subjectUserId = subjectUserId,
    )
    if (!isSubjectInFacilitatorLearningScope(
            facilitatorId = facilitatorUserId,
            facilitatorRole = facilitatorRole,
            subjectUserId = subjectUserId,
        )
    ) {
        throw DatabaseException.Forbidden(
            message = "Subject not in facilitator scope",
        )
    }
}

private const val LEARNING_PAGE_MIN: Int = 1

private const val LEARNING_PAGE_MAX: Int = 100

private const val REMARK_BODY_MAX_LENGTH: Int = 2000
