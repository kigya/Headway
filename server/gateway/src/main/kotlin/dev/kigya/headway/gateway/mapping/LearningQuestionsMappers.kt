package dev.kigya.headway.gateway.mapping

import dev.kigya.headway.database.api.model.out.DatabaseLearningCatalogResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningPageResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningProgressState
import dev.kigya.headway.database.api.model.out.DatabaseLearningQuestionDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningRemarkDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningSearchResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningSkillGroup
import dev.kigya.headway.database.api.model.out.DatabaseLearningTagDto
import dev.kigya.headway.gateway.graphql.GatewayAppLocale
import dev.kigya.headway.gateway.model.GatewayLearningProgressState
import dev.kigya.headway.gateway.model.GatewayLearningQuestion
import dev.kigya.headway.gateway.model.GatewayLearningQuestionRemark
import dev.kigya.headway.gateway.model.GatewayLearningQuestionsCatalog
import dev.kigya.headway.gateway.model.GatewayLearningQuestionsPage
import dev.kigya.headway.gateway.model.GatewayLearningQuestionsSearchResult
import dev.kigya.headway.gateway.model.GatewayLearningSkillGroup
import dev.kigya.headway.gateway.model.GatewayLearningTag

internal fun GatewayAppLocale.toLearningLocaleWire(): String = when (this) {
    GatewayAppLocale.EN -> "en"
    GatewayAppLocale.RU -> "ru"
}

internal fun DatabaseLearningCatalogResponseDto.toGateway(
    stripProgress: Boolean,
): GatewayLearningQuestionsCatalog = GatewayLearningQuestionsCatalog(
    hardSkills = hardSkills.map { it.toGateway(stripProgress = stripProgress) },
    softSkills = softSkills.map { it.toGateway(stripProgress = stripProgress) },
    resumeHard = if (stripProgress) null else resumeHard,
    resumeSoft = if (stripProgress) null else resumeSoft,
)

internal fun DatabaseLearningPageResponseDto.toGateway(
    stripProgress: Boolean,
): GatewayLearningQuestionsPage = GatewayLearningQuestionsPage(
    items = items.map { it.toGateway(stripProgress = stripProgress) },
    resumeHard = if (stripProgress) null else resumeHard,
    resumeSoft = if (stripProgress) null else resumeSoft,
)

internal fun DatabaseLearningSearchResponseDto.toGateway(
    stripProgress: Boolean,
): GatewayLearningQuestionsSearchResult = GatewayLearningQuestionsSearchResult(
    hardSkills = hardSkills.map { it.toGateway(stripProgress = stripProgress) },
    softSkills = softSkills.map { it.toGateway(stripProgress = stripProgress) },
)

internal fun DatabaseLearningQuestionDto.toGateway(
    stripProgress: Boolean,
): GatewayLearningQuestion = GatewayLearningQuestion(
    id = id,
    skillGroup = skillGroup.toGateway(),
    text = text,
    tags = tags.map { it.toGateway() },
    progress = if (stripProgress) null else progress?.toGateway(),
)

internal fun DatabaseLearningTagDto.toGateway(): GatewayLearningTag =
    GatewayLearningTag(id = id, label = label)

internal fun DatabaseLearningSkillGroup.toGateway(): GatewayLearningSkillGroup = when (this) {
    DatabaseLearningSkillGroup.HARD -> GatewayLearningSkillGroup.HARD
    DatabaseLearningSkillGroup.SOFT -> GatewayLearningSkillGroup.SOFT
}

internal fun DatabaseLearningProgressState.toGateway(): GatewayLearningProgressState = when (this) {
    DatabaseLearningProgressState.NOT_ANSWERED -> GatewayLearningProgressState.NOT_ANSWERED
    DatabaseLearningProgressState.PARTIALLY_ANSWERED -> GatewayLearningProgressState.PARTIALLY_ANSWERED
    DatabaseLearningProgressState.ANSWERED -> GatewayLearningProgressState.ANSWERED
}

internal fun GatewayLearningSkillGroup.toDatabase(): DatabaseLearningSkillGroup = when (this) {
    GatewayLearningSkillGroup.HARD -> DatabaseLearningSkillGroup.HARD
    GatewayLearningSkillGroup.SOFT -> DatabaseLearningSkillGroup.SOFT
}

internal fun DatabaseLearningRemarkDto.toGateway(): GatewayLearningQuestionRemark =
    GatewayLearningQuestionRemark(
        id = id,
        authorUserId = authorUserId,
        body = body,
        createdAtEpochMillis = createdAtEpochMillis,
    )
