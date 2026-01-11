package dev.kigya.headway.api.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.api.model.DependencyHealth
import dev.kigya.headway.api.model.HealthPayload
import dev.kigya.headway.api.model.ServiceStatus
import dev.kigya.headway.api.port.CheckHealthStatusUseCaseContract

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
