package dev.kigya.headway.gateway.mapping

import dev.kigya.headway.database.api.model.out.DatabasePreparationCatalogItemDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationEmployeeDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationReadinessResponseDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionQuestionDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionStateDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionSummaryDto
import dev.kigya.headway.gateway.model.GatewayPreparationCatalogItem
import dev.kigya.headway.gateway.model.GatewayPreparationEmployee
import dev.kigya.headway.gateway.model.GatewayPreparationReadiness
import dev.kigya.headway.gateway.model.GatewayPreparationSessionQuestion
import dev.kigya.headway.gateway.model.GatewayPreparationSessionState
import dev.kigya.headway.gateway.model.GatewayPreparationSessionSummary

internal fun DatabasePreparationEmployeeDto.toGateway(): GatewayPreparationEmployee =
    GatewayPreparationEmployee(
        id = id,
        displayName = displayName,
        department = department?.toGateway(),
        avatarUrl = avatarUrl,
    )

internal fun DatabasePreparationReadinessResponseDto.toGateway(): GatewayPreparationReadiness =
    GatewayPreparationReadiness(
        readinessPercent = readinessPercent?.toInt(),
        recommendedFormat = recommendedFormatCode.toGateway(),
        recommendedLabel = recommendedLabel,
    )

internal fun DatabasePreparationCatalogItemDto.toGateway(): GatewayPreparationCatalogItem =
    GatewayPreparationCatalogItem(
        code = formatCode.toGateway(),
        name = displayName,
        description = shortDescription,
        tags = typeTags,
        defaultLanguage = defaultPreparationLanguage,
        selectableLanguages = selectablePreparationLanguages,
    )

internal fun DatabasePreparationSessionStateDto.toGateway(): GatewayPreparationSessionState =
    GatewayPreparationSessionState(
        sessionId = sessionId,
        status = status.toGateway(),
        format = formatCode.toGateway(),
        language = preparationLanguage,
        subjectDisplayName = subjectDisplayName,
        subjectAvatarUrl = subjectAvatarUrl,
        currentQuestionId = currentQuestionId,
        lastActiveQuestionId = lastActiveQuestionId,
        customDomainFilter = customDomainFilter?.toGateway(),
        questions = questions.map { it.toGateway() },
        startedAtEpochMillis = startedAtEpochMillis,
        endedAtEpochMillis = endedAtEpochMillis,
    )

internal fun DatabasePreparationSessionQuestionDto.toGateway(): GatewayPreparationSessionQuestion =
    GatewayPreparationSessionQuestion(
        id = id,
        order = sortOrder,
        topicTags = topicTags,
        domain = domain?.toGateway(),
        text = titleOrPrompt,
        body = body,
        outcome = outcome?.toGateway(),
        comment = comment,
        addressedForUi = addressedForUi,
    )

internal fun DatabasePreparationSessionSummaryDto.toGateway(): GatewayPreparationSessionSummary =
    GatewayPreparationSessionSummary(
        sessionId = sessionId,
        durationSeconds = durationSeconds,
        headline = headline,
        body = body,
    )
