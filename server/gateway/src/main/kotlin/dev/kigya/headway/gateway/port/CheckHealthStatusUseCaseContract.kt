package dev.kigya.headway.gateway.port

import dev.kigya.headway.gateway.model.HealthPayload

interface CheckHealthStatusUseCaseContract {
    suspend operator fun invoke(): HealthPayload
}
