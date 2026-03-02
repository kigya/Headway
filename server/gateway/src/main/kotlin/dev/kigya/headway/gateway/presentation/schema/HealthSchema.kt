package dev.kigya.headway.gateway.presentation.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.domain.usecase.CheckHealthStatusUseCase
import dev.kigya.headway.gateway.model.GatewayDependencyHealth
import dev.kigya.headway.gateway.model.GatewayHealthPayload
import dev.kigya.headway.gateway.model.GatewayServiceStatus
import dev.kigya.headway.gateway.presentation.routes.GatewayGraphqlOperation

internal fun SchemaBuilder.healthSchema(checkHealthStatus: CheckHealthStatusUseCase) {
    enum<GatewayServiceStatus>()
    type<GatewayDependencyHealth>()
    type<GatewayHealthPayload>()

    query(GatewayGraphqlOperation.Health.name) {
        description = "Gateway health"
        resolver { stub: Boolean? ->
            checkHealthStatus()
        }
    }
}
