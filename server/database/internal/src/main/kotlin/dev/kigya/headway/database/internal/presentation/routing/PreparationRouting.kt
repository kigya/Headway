package dev.kigya.headway.database.internal.presentation.routing

import dev.kigya.headway.database.api.model.`in`.DatabasePreparationSelectQuestionRequestDto
import dev.kigya.headway.database.api.model.`in`.DatabasePreparationStartSessionRequestDto
import dev.kigya.headway.database.api.model.`in`.DatabasePreparationSubmitOutcomeRequestDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationEmployeesResponseDto
import dev.kigya.headway.database.api.model.resource.DatabaseResource
import dev.kigya.headway.database.internal.presentation.PreparationUseCases
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

internal fun Route.preparationRouting(preparation: PreparationUseCases) {
    get<DatabaseResource.Preparation.SetupEmployees> { params ->
        val employees = preparation.listSetupEmployees(
            facilitatorId = params.facilitatorUserId,
            facilitatorRole = params.facilitatorRole,
        )
        call.respond(
            HttpStatusCode.OK,
            DatabasePreparationEmployeesResponseDto(employees = employees),
        )
    }

    get<DatabaseResource.Preparation.EmployeeReadiness> { params ->
        val readiness = preparation.getEmployeeReadiness(
            facilitatorId = params.facilitatorUserId,
            facilitatorRole = params.facilitatorRole,
            subjectUserId = params.subjectUserId,
        )
        call.respond(HttpStatusCode.OK, readiness)
    }

    get<DatabaseResource.Preparation.Catalog> { params ->
        val catalog = preparation.listFormatCatalog(
            facilitatorId = params.facilitatorUserId,
            facilitatorRole = params.facilitatorRole,
            locale = params.locale,
        )
        call.respond(HttpStatusCode.OK, catalog)
    }

    post<DatabaseResource.Preparation.Sessions> { params ->
        val body = call.receive<DatabasePreparationStartSessionRequestDto>()
        val started = preparation.startSession(
            facilitatorId = params.facilitatorUserId,
            facilitatorRole = params.facilitatorRole,
            subjectUserId = body.subjectUserId,
            formatCode = body.formatCode,
            preparationLanguage = body.preparationLanguage,
            customDomainFilter = body.customDomainFilter,
        )
        call.respond(HttpStatusCode.Created, started)
    }

    get<DatabaseResource.Preparation.Sessions.ById> { params ->
        val state = preparation.getSessionState(
            facilitatorId = params.facilitatorUserId,
            facilitatorRole = params.facilitatorRole,
            sessionId = params.sessionId,
        )
        call.respond(HttpStatusCode.OK, state)
    }

    post<DatabaseResource.Preparation.Sessions.ById.Outcomes> { params ->
        val body = call.receive<DatabasePreparationSubmitOutcomeRequestDto>()
        val state = preparation.submitOutcome(
            facilitatorId = params.parent.facilitatorUserId,
            facilitatorRole = params.parent.facilitatorRole,
            sessionId = params.parent.sessionId,
            sessionQuestionId = body.sessionQuestionId,
            outcome = body.outcome,
            comment = body.comment,
        )
        call.respond(HttpStatusCode.OK, state)
    }

    post<DatabaseResource.Preparation.Sessions.ById.SelectQuestion> { params ->
        val body = call.receive<DatabasePreparationSelectQuestionRequestDto>()
        val state = preparation.selectQuestion(
            facilitatorId = params.parent.facilitatorUserId,
            facilitatorRole = params.parent.facilitatorRole,
            sessionId = params.parent.sessionId,
            sessionQuestionId = body.sessionQuestionId,
        )
        call.respond(HttpStatusCode.OK, state)
    }

    post<DatabaseResource.Preparation.Sessions.ById.Finish> { params ->
        val state = preparation.finishSession(
            facilitatorId = params.parent.facilitatorUserId,
            facilitatorRole = params.parent.facilitatorRole,
            sessionId = params.parent.sessionId,
        )
        call.respond(HttpStatusCode.OK, state)
    }

    get<DatabaseResource.Preparation.Sessions.ById.Summary> { params ->
        val summary = preparation.getSessionSummary(
            facilitatorId = params.parent.facilitatorUserId,
            facilitatorRole = params.parent.facilitatorRole,
            sessionId = params.parent.sessionId,
            locale = params.locale,
        )
        call.respond(HttpStatusCode.OK, summary)
    }
}
