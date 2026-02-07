package dev.kigya.headway.gateway.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.model.DependencyHealth
import dev.kigya.headway.gateway.model.HealthPayload
import dev.kigya.headway.gateway.model.ServiceStatus
import dev.kigya.headway.gateway.port.CheckHealthStatusUseCaseContract

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
