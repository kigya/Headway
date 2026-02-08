package dev.kigya.headway.gateway.presentation.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.model.DependencyHealth
import dev.kigya.headway.gateway.model.HealthPayload
import dev.kigya.headway.gateway.model.ServiceStatus
import dev.kigya.headway.gateway.domain.usecase.CheckHealthStatusUseCase

internal fun SchemaBuilder.healthSchema(
    checkHealthStatus: CheckHealthStatusUseCase,
) {
    enum<ServiceStatus>()
    type<DependencyHealth>()
    type<HealthPayload>()

    query("_health") {
        description = "Gateway health"
        resolver { _: Boolean? ->
            checkHealthStatus()
        }
    }
}
