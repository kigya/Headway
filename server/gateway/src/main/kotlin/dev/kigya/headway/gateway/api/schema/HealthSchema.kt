package dev.kigya.headway.gateway.api.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.api.model.DependencyHealth
import dev.kigya.headway.gateway.api.model.HealthPayload
import dev.kigya.headway.gateway.api.model.ServiceStatus
import dev.kigya.headway.gateway.api.port.CheckHealthStatusUseCaseContract

internal fun SchemaBuilder.healthSchema(
    checkHealthStatusUseCaseContract: CheckHealthStatusUseCaseContract,
) {
    enum<ServiceStatus>()
    type<DependencyHealth>()
    type<HealthPayload>()

    query("_health") {
        description = "Gateway health"
        resolver { stub: Boolean? ->
            checkHealthStatusUseCaseContract()
        }
    }
}
