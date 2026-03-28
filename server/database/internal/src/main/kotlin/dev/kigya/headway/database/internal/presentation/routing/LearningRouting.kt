package dev.kigya.headway.database.internal.presentation.routing

import dev.kigya.headway.database.api.model.`in`.DatabaseLearningFacilitatedPageQuery
import dev.kigya.headway.database.api.model.`in`.DatabaseLearningRemarkCreateRequestDto
import dev.kigya.headway.database.api.model.resource.DatabaseResource
import dev.kigya.headway.database.internal.presentation.LearningQuestionsService
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

internal fun Route.learningQuestionsRouting(learning: LearningQuestionsService) {
    registerLearningCatalogRoutes(learning)
    registerLearningPageRoutes(learning)
    registerLearningSearchRoutes(learning)
    registerLearningQuestionByIdRoutes(learning)
    registerLearningRemarks(learning)
}

private fun Route.registerLearningCatalogRoutes(learning: LearningQuestionsService) {
    get<DatabaseResource.LearningQuestions.PublicCatalog> { params ->
        val locale = requireNonBlankLearningLocale(params.locale)
        call.respond(learning.getPublicCatalog(locale = locale))
    }

    get<DatabaseResource.LearningQuestions.Catalog> { params ->
        val locale = requireNonBlankLearningLocale(params.locale)
        call.respond(
            learning.getCatalog(
                facilitatorUserId = params.facilitatorUserId,
                facilitatorRole = params.facilitatorRole,
                locale = locale,
                subjectUserId = params.subjectUserId,
            ),
        )
    }
}

private fun Route.registerLearningPageRoutes(learning: LearningQuestionsService) {
    get<DatabaseResource.LearningQuestions.PublicPage> { params ->
        val locale = requireNonBlankLearningLocale(params.locale)
        call.respond(
            learning.getPublicPage(
                locale = locale,
                skillGroup = params.skillGroup,
                limit = params.limit,
                afterQuestionId = params.afterQuestionId,
            ),
        )
    }

    get<DatabaseResource.LearningQuestions.Page> { params ->
        val locale = requireNonBlankLearningLocale(params.locale)
        call.respond(
            learning.getPage(
                query = DatabaseLearningFacilitatedPageQuery(
                    facilitatorUserId = params.facilitatorUserId,
                    facilitatorRole = params.facilitatorRole,
                    locale = locale,
                    skillGroup = params.skillGroup,
                    limit = params.limit,
                    afterQuestionId = params.afterQuestionId,
                    subjectUserId = params.subjectUserId,
                ),
            ),
        )
    }
}

private fun Route.registerLearningSearchRoutes(learning: LearningQuestionsService) {
    get<DatabaseResource.LearningQuestions.PublicSearch> { params ->
        val locale = requireNonBlankLearningLocale(params.locale)
        call.respond(learning.getPublicSearch(locale = locale, query = params.q))
    }

    get<DatabaseResource.LearningQuestions.Search> { params ->
        val locale = requireNonBlankLearningLocale(params.locale)
        call.respond(
            learning.getSearch(
                facilitatorUserId = params.facilitatorUserId,
                facilitatorRole = params.facilitatorRole,
                locale = locale,
                query = params.q,
                subjectUserId = params.subjectUserId,
            ),
        )
    }
}

private fun Route.registerLearningQuestionByIdRoutes(learning: LearningQuestionsService) {
    get<DatabaseResource.LearningQuestions.PublicById> { params ->
        val locale = requireNonBlankLearningLocale(params.locale)
        call.respond(learning.getPublicQuestion(questionId = params.questionId, locale = locale))
    }

    get<DatabaseResource.LearningQuestions.ById> { params ->
        val locale = requireNonBlankLearningLocale(params.locale)
        call.respond(
            learning.getQuestion(
                facilitatorUserId = params.facilitatorUserId,
                facilitatorRole = params.facilitatorRole,
                questionId = params.questionId,
                locale = locale,
                subjectUserId = params.subjectUserId,
            ),
        )
    }
}

private fun Route.registerLearningRemarks(learning: LearningQuestionsService) {
    get<DatabaseResource.LearningQuestions.Remarks> { params ->
        call.respond(
            learning.listRemarks(
                questionId = params.questionId,
                subjectUserId = params.subjectUserId,
                facilitatorUserId = params.facilitatorUserId,
                facilitatorRole = params.facilitatorRole,
            ),
        )
    }

    post<DatabaseResource.LearningQuestions.Remarks> { params ->
        val body = call.receive<DatabaseLearningRemarkCreateRequestDto>()
        call.respond(
            learning.addRemark(
                questionId = params.questionId,
                routeSubjectUserId = params.subjectUserId,
                facilitatorUserId = params.facilitatorUserId,
                facilitatorRole = params.facilitatorRole,
                body = body,
            ),
        )
    }
}

private fun requireNonBlankLearningLocale(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isBlank()) {
        throw BadRequestException(LEARNING_LOCALE_BLANK_MESSAGE)
    }
    return trimmed
}

private const val LEARNING_LOCALE_BLANK_MESSAGE: String = "Locale is blank"
