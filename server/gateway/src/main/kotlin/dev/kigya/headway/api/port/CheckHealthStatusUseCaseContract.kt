package dev.kigya.headway.api.port

import dev.kigya.headway.api.model.HealthPayload

interface CheckHealthStatusUseCaseContract {
    suspend operator fun invoke(): HealthPayload
}
