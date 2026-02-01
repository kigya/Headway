package dev.kigya.headway.gateway.api.port

import dev.kigya.headway.gateway.api.model.HealthPayload

interface CheckHealthStatusUseCaseContract {
    suspend operator fun invoke(): HealthPayload
}
