package dev.kigya.headway.gateway.mapping

import dev.kigya.headway.database.api.model.out.DatabasePreparationFormatCode
import dev.kigya.headway.database.api.model.out.DatabasePreparationOutcomeCode
import dev.kigya.headway.database.api.model.out.DatabasePreparationQuestionDomain
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionStatus
import dev.kigya.headway.gateway.graphql.GatewayAppLocale
import dev.kigya.headway.gateway.model.GatewayPreparationFormatCode
import dev.kigya.headway.gateway.model.GatewayPreparationOutcomeCode
import dev.kigya.headway.gateway.model.GatewayPreparationQuestionDomain
import dev.kigya.headway.gateway.model.GatewayPreparationSessionStatus

internal fun DatabasePreparationFormatCode.toGateway(): GatewayPreparationFormatCode =
    GatewayPreparationFormatCode.valueOf(name)

internal fun GatewayPreparationFormatCode.toDatabase(): DatabasePreparationFormatCode =
    DatabasePreparationFormatCode.valueOf(name)

internal fun DatabasePreparationSessionStatus.toGateway(): GatewayPreparationSessionStatus =
    GatewayPreparationSessionStatus.valueOf(name)

internal fun DatabasePreparationOutcomeCode.toGateway(): GatewayPreparationOutcomeCode =
    GatewayPreparationOutcomeCode.valueOf(name)

internal fun GatewayPreparationOutcomeCode.toDatabase(): DatabasePreparationOutcomeCode =
    DatabasePreparationOutcomeCode.valueOf(name)

internal fun DatabasePreparationQuestionDomain.toGateway(): GatewayPreparationQuestionDomain =
    GatewayPreparationQuestionDomain.valueOf(name)

internal fun GatewayPreparationQuestionDomain.toDatabase(): DatabasePreparationQuestionDomain =
    DatabasePreparationQuestionDomain.valueOf(name)

internal fun GatewayAppLocale.toCatalogLocale(): String = when (this) {
    GatewayAppLocale.EN -> "en"
    GatewayAppLocale.RU -> "ru"
}
