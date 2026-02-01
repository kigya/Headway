package dev.kigya.headway.gateway.internal.probe.auth

import dev.kigya.headway.gateway.api.model.ServiceStatus

internal interface AuthProbeContract {
    suspend fun check(): ServiceStatus
}
