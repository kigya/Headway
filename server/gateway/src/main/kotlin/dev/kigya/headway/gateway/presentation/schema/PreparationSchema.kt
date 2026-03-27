package dev.kigya.headway.gateway.presentation.schema

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.auth.GatewayAuthorizationPolicy
import dev.kigya.headway.gateway.domain.auth.GatewayOperation
import dev.kigya.headway.gateway.domain.usecase.ResolvePrincipalUseCase
import dev.kigya.headway.gateway.graphql.GraphqlRequestContext
import dev.kigya.headway.gateway.model.GatewayPreparationCatalogItem
import dev.kigya.headway.gateway.model.GatewayPreparationEmployee
import dev.kigya.headway.gateway.model.GatewayPreparationFormatCode
import dev.kigya.headway.gateway.model.GatewayPreparationOutcomeCode
import dev.kigya.headway.gateway.model.GatewayPreparationQuestionDomain
import dev.kigya.headway.gateway.model.GatewayPreparationReadiness
import dev.kigya.headway.gateway.model.GatewayPreparationSessionQuestion
import dev.kigya.headway.gateway.model.GatewayPreparationSessionState
import dev.kigya.headway.gateway.model.GatewayPreparationSessionStatus
import dev.kigya.headway.gateway.model.GatewayPreparationSessionSummary
import dev.kigya.headway.gateway.model.GatewayPrincipal
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.presentation.routes.GatewayGraphqlOperation
import java.util.UUID

internal fun SchemaBuilder.preparationSchema(
    resolvePrincipal: ResolvePrincipalUseCase,
    preparation: PreparationGraphqlServices,
) {
    registerPreparationTypes()
    registerPreparationQueries(
        resolvePrincipal = resolvePrincipal,
        preparation = preparation,
    )
    registerPreparationMutations(
        resolvePrincipal = resolvePrincipal,
        preparation = preparation,
    )
}

private fun SchemaBuilder.registerPreparationTypes() {
    enum<GatewayPreparationFormatCode>()
    enum<GatewayPreparationOutcomeCode>()
    enum<GatewayPreparationQuestionDomain>()
    enum<GatewayPreparationSessionStatus>()
    type<GatewayPreparationEmployee>()
    type<GatewayPreparationReadiness>()
    type<GatewayPreparationCatalogItem>()
    type<GatewayPreparationSessionQuestion>()
    type<GatewayPreparationSessionState>()
    type<GatewayPreparationSessionSummary>()
}

private fun SchemaBuilder.registerPreparationQueries(
    resolvePrincipal: ResolvePrincipalUseCase,
    preparation: PreparationGraphqlServices,
) {
    query(GatewayGraphqlOperation.PreparationSetupEmployees.name) {
        resolver { ctx: Context ->
            val requestContext = ctx.graphqlRequestContext()
            val user = ensurePreparationAndResolveUser(
                resolvePrincipal = resolvePrincipal,
                requestContext = requestContext,
            )
            preparation.getPreparationSetupEmployees(user)
        }
    }

    query(GatewayGraphqlOperation.PreparationEmployeeReadiness.name) {
        resolver { subjectUserId: UUID, ctx: Context ->
            val requestContext = ctx.graphqlRequestContext()
            val user = ensurePreparationAndResolveUser(
                resolvePrincipal = resolvePrincipal,
                requestContext = requestContext,
            )
            preparation.getPreparationEmployeeReadiness(user, subjectUserId)
        }
    }

    query(GatewayGraphqlOperation.PreparationFormatCatalog.name) {
        resolver { ctx: Context ->
            val requestContext = ctx.graphqlRequestContext()
            val user = ensurePreparationAndResolveUser(
                resolvePrincipal = resolvePrincipal,
                requestContext = requestContext,
            )
            preparation.getPreparationFormatCatalog(user, requestContext.appLocale)
        }
    }

    query(GatewayGraphqlOperation.PreparationSession.name) {
        resolver { sessionId: UUID, ctx: Context ->
            val requestContext = ctx.graphqlRequestContext()
            val user = ensurePreparationAndResolveUser(
                resolvePrincipal = resolvePrincipal,
                requestContext = requestContext,
            )
            preparation.getPreparationSessionState(user, sessionId)
        }
    }

    query(GatewayGraphqlOperation.PreparationSessionSummary.name) {
        resolver { sessionId: UUID, ctx: Context ->
            val requestContext = ctx.graphqlRequestContext()
            val user = ensurePreparationAndResolveUser(
                resolvePrincipal = resolvePrincipal,
                requestContext = requestContext,
            )
            preparation.getPreparationSessionSummary(user, sessionId, requestContext.appLocale)
        }
    }
}

private fun SchemaBuilder.registerPreparationMutations(
    resolvePrincipal: ResolvePrincipalUseCase,
    preparation: PreparationGraphqlServices,
) {
    mutation(GatewayGraphqlOperation.PreparationStartSession.name) {
        resolver {
                subjectUserId: UUID,
                formatCode: GatewayPreparationFormatCode,
                preparationLanguage: String?,
                customDomainFilter: GatewayPreparationQuestionDomain?,
                ctx: Context,
            ->
            val requestContext = ctx.graphqlRequestContext()
            val user = ensurePreparationAndResolveUser(
                resolvePrincipal = resolvePrincipal,
                requestContext = requestContext,
            )
            preparation.startPreparationSession(
                user = user,
                subjectUserId = subjectUserId,
                formatCode = formatCode,
                preparationLanguage = preparationLanguage,
                customDomainFilter = customDomainFilter,
            )
        }
    }

    mutation(GatewayGraphqlOperation.PreparationSubmitOutcome.name) {
        resolver {
                sessionId: UUID,
                sessionQuestionId: UUID,
                outcome: GatewayPreparationOutcomeCode,
                comment: String?,
                ctx: Context,
            ->
            val requestContext = ctx.graphqlRequestContext()
            val user = ensurePreparationAndResolveUser(
                resolvePrincipal = resolvePrincipal,
                requestContext = requestContext,
            )
            preparation.submitPreparationOutcome(
                user = user,
                sessionId = sessionId,
                sessionQuestionId = sessionQuestionId,
                outcome = outcome,
                comment = comment,
            )
        }
    }

    mutation(GatewayGraphqlOperation.PreparationSelectQuestion.name) {
        resolver { sessionId: UUID, sessionQuestionId: UUID, ctx: Context ->
            val requestContext = ctx.graphqlRequestContext()
            val user = ensurePreparationAndResolveUser(
                resolvePrincipal = resolvePrincipal,
                requestContext = requestContext,
            )
            preparation.selectPreparationSessionQuestion(user, sessionId, sessionQuestionId)
        }
    }

    mutation(GatewayGraphqlOperation.PreparationFinishSession.name) {
        resolver { sessionId: UUID, ctx: Context ->
            val requestContext = ctx.graphqlRequestContext()
            val user = ensurePreparationAndResolveUser(
                resolvePrincipal = resolvePrincipal,
                requestContext = requestContext,
            )
            preparation.finishPreparationSession(user, sessionId)
        }
    }
}

private fun Context.graphqlRequestContext(): GraphqlRequestContext =
    get<GraphqlRequestContext>() ?: throw GatewayException.Internal("Missing GraphQL request context")

private suspend fun ensurePreparationAndResolveUser(
    resolvePrincipal: ResolvePrincipalUseCase,
    requestContext: GraphqlRequestContext,
): GatewayUser {
    val principal = resolvePrincipal(requestContext.authorizationHeader)
    GatewayAuthorizationPolicy.ensure(principal, GatewayOperation.Preparation)
    return when (principal) {
        is GatewayPrincipal.User -> principal.user
        is GatewayPrincipal.Guest ->
            throw GatewayException.Internal("Unexpected guest principal")
    }
}
