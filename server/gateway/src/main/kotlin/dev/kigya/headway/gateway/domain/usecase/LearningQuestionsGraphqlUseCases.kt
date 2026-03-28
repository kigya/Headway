package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.database.api.model.`in`.DatabaseLearningFacilitatedPageQuery
import dev.kigya.headway.database.api.model.`in`.DatabaseLearningRemarkCreateRequestDto
import dev.kigya.headway.gateway.domain.repository.LearningQuestionsRepositoryContract
import dev.kigya.headway.gateway.graphql.GatewayAppLocale
import dev.kigya.headway.gateway.mapping.toDatabase
import dev.kigya.headway.gateway.mapping.toGateway
import dev.kigya.headway.gateway.mapping.toLearningLocaleWire
import dev.kigya.headway.gateway.model.GatewayLearningQuestion
import dev.kigya.headway.gateway.model.GatewayLearningQuestionRemark
import dev.kigya.headway.gateway.model.GatewayLearningQuestionsCatalog
import dev.kigya.headway.gateway.model.GatewayLearningQuestionsPage
import dev.kigya.headway.gateway.model.GatewayLearningQuestionsSearchResult
import dev.kigya.headway.gateway.model.GatewayLearningSkillGroup
import dev.kigya.headway.gateway.model.GatewayPrincipal
import dev.kigya.headway.gateway.model.GatewayUser
import java.util.UUID

internal class LearningQuestionsGraphqlUseCases(
    private val repository: LearningQuestionsRepositoryContract,
) {

    suspend fun learningQuestionsCatalog(
        principal: GatewayPrincipal,
        locale: GatewayAppLocale,
        subjectUserId: UUID?,
    ): GatewayLearningQuestionsCatalog {
        val wireLocale = locale.toLearningLocaleWire()
        val stripProgress = principal is GatewayPrincipal.Guest
        val dto = when (principal) {
            is GatewayPrincipal.Guest ->
                repository.getPublicCatalog(locale = wireLocale)

            is GatewayPrincipal.User -> {
                val user = principal.user
                repository.getCatalog(
                    facilitatorUserId = user.id,
                    facilitatorRole = user.role.toDatabase(),
                    locale = wireLocale,
                    subjectUserId = subjectUserId ?: user.id,
                )
            }
        }
        return dto.toGateway(stripProgress = stripProgress)
    }

    suspend fun learningQuestionsPage(
        principal: GatewayPrincipal,
        locale: GatewayAppLocale,
        skillGroup: GatewayLearningSkillGroup,
        limit: Int,
        afterQuestionId: Long?,
        subjectUserId: UUID?,
    ): GatewayLearningQuestionsPage {
        val wireLocale = locale.toLearningLocaleWire()
        val stripProgress = principal is GatewayPrincipal.Guest
        val skill = skillGroup.toDatabase()
        val dto = when (principal) {
            is GatewayPrincipal.Guest ->
                repository.getPublicPage(
                    locale = wireLocale,
                    skillGroup = skill,
                    limit = limit,
                    afterQuestionId = afterQuestionId,
                )

            is GatewayPrincipal.User -> {
                val user = principal.user
                repository.getPage(
                    query = DatabaseLearningFacilitatedPageQuery(
                        facilitatorUserId = user.id,
                        facilitatorRole = user.role.toDatabase(),
                        locale = wireLocale,
                        skillGroup = skill,
                        limit = limit,
                        afterQuestionId = afterQuestionId,
                        subjectUserId = subjectUserId ?: user.id,
                    ),
                )
            }
        }
        return dto.toGateway(stripProgress = stripProgress)
    }

    suspend fun learningQuestionsSearch(
        principal: GatewayPrincipal,
        locale: GatewayAppLocale,
        query: String,
        subjectUserId: UUID?,
    ): GatewayLearningQuestionsSearchResult {
        val wireLocale = locale.toLearningLocaleWire()
        val stripProgress = principal is GatewayPrincipal.Guest
        val dto = when (principal) {
            is GatewayPrincipal.Guest ->
                repository.getPublicSearch(locale = wireLocale, query = query)

            is GatewayPrincipal.User -> {
                val user = principal.user
                repository.getSearch(
                    facilitatorUserId = user.id,
                    facilitatorRole = user.role.toDatabase(),
                    locale = wireLocale,
                    query = query,
                    subjectUserId = subjectUserId ?: user.id,
                )
            }
        }
        return dto.toGateway(stripProgress = stripProgress)
    }

    suspend fun learningQuestion(
        principal: GatewayPrincipal,
        locale: GatewayAppLocale,
        questionId: Long,
        subjectUserId: UUID?,
    ): GatewayLearningQuestion {
        val wireLocale = locale.toLearningLocaleWire()
        val stripProgress = principal is GatewayPrincipal.Guest
        val dto = when (principal) {
            is GatewayPrincipal.Guest ->
                repository.getPublicQuestion(questionId = questionId, locale = wireLocale)

            is GatewayPrincipal.User -> {
                val user = principal.user
                repository.getQuestion(
                    facilitatorUserId = user.id,
                    facilitatorRole = user.role.toDatabase(),
                    questionId = questionId,
                    locale = wireLocale,
                    subjectUserId = subjectUserId ?: user.id,
                )
            }
        }
        return dto.toGateway(stripProgress = stripProgress)
    }

    suspend fun learningQuestionRemarks(
        user: GatewayUser,
        questionId: Long,
        subjectUserId: UUID,
    ): List<GatewayLearningQuestionRemark> = repository.listRemarks(
        questionId = questionId,
        subjectUserId = subjectUserId,
        facilitatorUserId = user.id,
        facilitatorRole = user.role.toDatabase(),
    ).map { it.toGateway() }

    suspend fun addLearningQuestionRemark(
        user: GatewayUser,
        questionId: Long,
        subjectUserId: UUID,
        body: String,
    ): GatewayLearningQuestionRemark = repository.addRemark(
        questionId = questionId,
        facilitatorUserId = user.id,
        facilitatorRole = user.role.toDatabase(),
        body = DatabaseLearningRemarkCreateRequestDto(
            subjectUserId = subjectUserId,
            body = body,
        ),
    ).toGateway()

    suspend fun logoutGuest(sessionId: UUID) {
        repository.revokeGuestSession(sessionId = sessionId)
    }
}
