package dev.kigya.headway.gateway.presentation.schema

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.auth.GatewayAuthorizationPolicy
import dev.kigya.headway.gateway.domain.auth.GatewayOperation
import dev.kigya.headway.gateway.domain.usecase.LearningQuestionsGraphqlUseCases
import dev.kigya.headway.gateway.domain.usecase.ResolvePrincipalUseCase
import dev.kigya.headway.gateway.graphql.GraphqlRequestContext
import dev.kigya.headway.gateway.model.GatewayLearningProgressState
import dev.kigya.headway.gateway.model.GatewayLearningQuestion
import dev.kigya.headway.gateway.model.GatewayLearningQuestionRemark
import dev.kigya.headway.gateway.model.GatewayLearningQuestionsCatalog
import dev.kigya.headway.gateway.model.GatewayLearningQuestionsPage
import dev.kigya.headway.gateway.model.GatewayLearningQuestionsSearchResult
import dev.kigya.headway.gateway.model.GatewayLearningSkillGroup
import dev.kigya.headway.gateway.model.GatewayLearningTag
import dev.kigya.headway.gateway.model.GatewayPrincipal
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.presentation.routes.GatewayGraphqlOperation
import java.util.UUID

internal fun SchemaBuilder.learningQuestionsSchema(
    resolvePrincipal: ResolvePrincipalUseCase,
    learningQuestions: LearningQuestionsGraphqlUseCases,
) {
    registerLearningTypes()
    registerLearningQueries(
        resolvePrincipal = resolvePrincipal,
        learningQuestions = learningQuestions,
    )
    registerLearningMutations(
        resolvePrincipal = resolvePrincipal,
        learningQuestions = learningQuestions,
    )
}

private fun SchemaBuilder.registerLearningTypes() {
    enum<GatewayLearningSkillGroup>()
    enum<GatewayLearningProgressState>()
    type<GatewayLearningTag>()
    type<GatewayLearningQuestion>()
    type<GatewayLearningQuestionsCatalog>()
    type<GatewayLearningQuestionsPage>()
    type<GatewayLearningQuestionsSearchResult>()
    type<GatewayLearningQuestionRemark>()
}

private fun SchemaBuilder.registerLearningQueries(
    resolvePrincipal: ResolvePrincipalUseCase,
    learningQuestions: LearningQuestionsGraphqlUseCases,
) {
    query(GatewayGraphqlOperation.LearningQuestionsCatalog.name) {
        resolver { subjectUserId: UUID?, ctx: Context ->
            val requestContext = ctx.graphqlRequestContext()
            val principal = resolvePrincipal(requestContext.authorizationHeader)
            GatewayAuthorizationPolicy.ensure(principal, GatewayOperation.LearningRead)
            learningQuestions.learningQuestionsCatalog(
                principal = principal,
                locale = requestContext.appLocale,
                subjectUserId = subjectUserId,
            )
        }
    }

    query(GatewayGraphqlOperation.LearningQuestionsPage.name) {
        resolver {
                skillGroup: GatewayLearningSkillGroup,
                limit: Int,
                afterQuestionId: Long?,
                subjectUserId: UUID?,
                ctx: Context,
            ->
            val requestContext = ctx.graphqlRequestContext()
            val principal = resolvePrincipal(requestContext.authorizationHeader)
            GatewayAuthorizationPolicy.ensure(principal, GatewayOperation.LearningRead)
            learningQuestions.learningQuestionsPage(
                principal = principal,
                locale = requestContext.appLocale,
                skillGroup = skillGroup,
                limit = limit,
                afterQuestionId = afterQuestionId,
                subjectUserId = subjectUserId,
            )
        }
    }

    query(GatewayGraphqlOperation.LearningQuestionsSearch.name) {
        resolver { searchQuery: String, subjectUserId: UUID?, ctx: Context ->
            val requestContext = ctx.graphqlRequestContext()
            val principal = resolvePrincipal(requestContext.authorizationHeader)
            GatewayAuthorizationPolicy.ensure(principal, GatewayOperation.LearningRead)
            learningQuestions.learningQuestionsSearch(
                principal = principal,
                locale = requestContext.appLocale,
                query = searchQuery,
                subjectUserId = subjectUserId,
            )
        }
    }

    query(GatewayGraphqlOperation.LearningQuestion.name) {
        resolver { questionId: Long, subjectUserId: UUID?, ctx: Context ->
            val requestContext = ctx.graphqlRequestContext()
            val principal = resolvePrincipal(requestContext.authorizationHeader)
            GatewayAuthorizationPolicy.ensure(principal, GatewayOperation.LearningRead)
            learningQuestions.learningQuestion(
                principal = principal,
                locale = requestContext.appLocale,
                questionId = questionId,
                subjectUserId = subjectUserId,
            )
        }
    }

    query(GatewayGraphqlOperation.LearningQuestionRemarks.name) {
        resolver { questionId: Long, subjectUserId: UUID, ctx: Context ->
            val requestContext = ctx.graphqlRequestContext()
            val user = ensureLearningUser(resolvePrincipal, requestContext)
            learningQuestions.learningQuestionRemarks(
                user = user,
                questionId = questionId,
                subjectUserId = subjectUserId,
            )
        }
    }
}

private fun SchemaBuilder.registerLearningMutations(
    resolvePrincipal: ResolvePrincipalUseCase,
    learningQuestions: LearningQuestionsGraphqlUseCases,
) {
    mutation(GatewayGraphqlOperation.AddLearningQuestionRemark.name) {
        resolver { questionId: Long, subjectUserId: UUID, body: String, ctx: Context ->
            val requestContext = ctx.graphqlRequestContext()
            val user = ensureLearningUser(resolvePrincipal, requestContext)
            val principal = GatewayPrincipal.User(user)
            GatewayAuthorizationPolicy.ensure(principal, GatewayOperation.LearningRemarkWrite)
            learningQuestions.addLearningQuestionRemark(
                user = user,
                questionId = questionId,
                subjectUserId = subjectUserId,
                body = body,
            )
        }
    }

    mutation(GatewayGraphqlOperation.LogoutGuest.name) {
        resolver { ctx: Context ->
            val requestContext = ctx.graphqlRequestContext()
            val principal = resolvePrincipal(requestContext.authorizationHeader)
            GatewayAuthorizationPolicy.ensure(principal, GatewayOperation.LogoutGuest)
            when (principal) {
                is GatewayPrincipal.Guest ->
                    learningQuestions.logoutGuest(sessionId = principal.guestSessionId)

                is GatewayPrincipal.User ->
                    throw GatewayException.Internal("Unexpected user principal for logoutGuest")
            }
            true
        }
    }
}

private fun Context.graphqlRequestContext(): GraphqlRequestContext =
    get<GraphqlRequestContext>() ?: throw GatewayException.Internal("Missing GraphQL request context")

private suspend fun ensureLearningUser(
    resolvePrincipal: ResolvePrincipalUseCase,
    requestContext: GraphqlRequestContext,
): GatewayUser {
    val principal = resolvePrincipal(requestContext.authorizationHeader)
    return when (principal) {
        is GatewayPrincipal.User -> principal.user
        is GatewayPrincipal.Guest ->
            throw GatewayException.Forbidden(
                reason = GatewayErrorReason.GUEST_NOT_ALLOWED,
                message = "Guests cannot access learning remarks",
            )
    }
}
