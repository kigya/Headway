package dev.kigya.headway.gateway.data.repository

import dev.kigya.headway.database.api.model.`in`.DatabaseLearningFacilitatedPageQuery
import dev.kigya.headway.database.api.model.`in`.DatabaseLearningRemarkCreateRequestDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningCatalogResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningPageResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningQuestionDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningRemarkDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningSearchResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningSkillGroup
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.api.model.resource.DatabaseResource
import dev.kigya.headway.gateway.core.http.upstreamCall
import dev.kigya.headway.gateway.domain.repository.LearningQuestionsRepositoryContract
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.util.UUID

internal class LearningQuestionsRepository(
    private val httpClient: HttpClient,
) : LearningQuestionsRepositoryContract {

    override suspend fun getPublicCatalog(locale: String): DatabaseLearningCatalogResponseDto =
        upstreamCall(
            dependency = DATABASE_UPSTREAM,
            request = {
                httpClient.get(DatabaseResource.LearningQuestions.PublicCatalog(locale = locale))
            },
            onSuccess = { it.body<DatabaseLearningCatalogResponseDto>() },
        )

    override suspend fun getCatalog(
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        locale: String,
        subjectUserId: UUID?,
    ): DatabaseLearningCatalogResponseDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.get(
                DatabaseResource.LearningQuestions.Catalog(
                    locale = locale,
                    facilitatorUserId = facilitatorUserId,
                    facilitatorRole = facilitatorRole,
                    subjectUserId = subjectUserId,
                ),
            )
        },
        onSuccess = { it.body<DatabaseLearningCatalogResponseDto>() },
    )

    override suspend fun getPublicPage(
        locale: String,
        skillGroup: DatabaseLearningSkillGroup,
        limit: Int,
        afterQuestionId: Long?,
    ): DatabaseLearningPageResponseDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.get(
                DatabaseResource.LearningQuestions.PublicPage(
                    locale = locale,
                    skillGroup = skillGroup,
                    limit = limit,
                    afterQuestionId = afterQuestionId,
                ),
            )
        },
        onSuccess = { it.body<DatabaseLearningPageResponseDto>() },
    )

    override suspend fun getPage(
        query: DatabaseLearningFacilitatedPageQuery,
    ): DatabaseLearningPageResponseDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.get(
                DatabaseResource.LearningQuestions.Page(
                    locale = query.locale,
                    skillGroup = query.skillGroup,
                    limit = query.limit,
                    afterQuestionId = query.afterQuestionId,
                    facilitatorUserId = query.facilitatorUserId,
                    facilitatorRole = query.facilitatorRole,
                    subjectUserId = query.subjectUserId,
                ),
            )
        },
        onSuccess = { it.body<DatabaseLearningPageResponseDto>() },
    )

    override suspend fun getPublicSearch(
        locale: String,
        query: String,
    ): DatabaseLearningSearchResponseDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.get(
                DatabaseResource.LearningQuestions.PublicSearch(locale = locale, q = query),
            )
        },
        onSuccess = { it.body<DatabaseLearningSearchResponseDto>() },
    )

    override suspend fun getSearch(
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        locale: String,
        query: String,
        subjectUserId: UUID?,
    ): DatabaseLearningSearchResponseDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.get(
                DatabaseResource.LearningQuestions.Search(
                    locale = locale,
                    q = query,
                    facilitatorUserId = facilitatorUserId,
                    facilitatorRole = facilitatorRole,
                    subjectUserId = subjectUserId,
                ),
            )
        },
        onSuccess = { it.body<DatabaseLearningSearchResponseDto>() },
    )

    override suspend fun getPublicQuestion(
        questionId: Long,
        locale: String,
    ): DatabaseLearningQuestionDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.get(
                DatabaseResource.LearningQuestions.PublicById(
                    questionId = questionId,
                    locale = locale,
                ),
            )
        },
        onSuccess = { it.body<DatabaseLearningQuestionDto>() },
    )

    override suspend fun getQuestion(
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        questionId: Long,
        locale: String,
        subjectUserId: UUID?,
    ): DatabaseLearningQuestionDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.get(
                DatabaseResource.LearningQuestions.ById(
                    questionId = questionId,
                    locale = locale,
                    facilitatorUserId = facilitatorUserId,
                    facilitatorRole = facilitatorRole,
                    subjectUserId = subjectUserId,
                ),
            )
        },
        onSuccess = { it.body<DatabaseLearningQuestionDto>() },
    )

    override suspend fun listRemarks(
        questionId: Long,
        subjectUserId: UUID,
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
    ): List<DatabaseLearningRemarkDto> = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.get(
                DatabaseResource.LearningQuestions.Remarks(
                    questionId = questionId,
                    subjectUserId = subjectUserId,
                    facilitatorUserId = facilitatorUserId,
                    facilitatorRole = facilitatorRole,
                ),
            )
        },
        onSuccess = { it.body<List<DatabaseLearningRemarkDto>>() },
    )

    override suspend fun addRemark(
        questionId: Long,
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        body: DatabaseLearningRemarkCreateRequestDto,
    ): DatabaseLearningRemarkDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.post(
                DatabaseResource.LearningQuestions.Remarks(
                    questionId = questionId,
                    subjectUserId = body.subjectUserId,
                    facilitatorUserId = facilitatorUserId,
                    facilitatorRole = facilitatorRole,
                ),
            ) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        },
        onSuccess = { it.body<DatabaseLearningRemarkDto>() },
    )

    override suspend fun revokeGuestSession(sessionId: UUID) = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.post(DatabaseResource.GuestSessions.Revoke(sessionId = sessionId))
        },
        onSuccess = { },
    )
}

private const val DATABASE_UPSTREAM: String = "database"
