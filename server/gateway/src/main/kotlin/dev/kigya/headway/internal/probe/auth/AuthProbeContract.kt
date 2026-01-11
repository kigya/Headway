package dev.kigya.headway.internal.probe.auth

import dev.kigya.headway.api.model.ServiceStatus

internal interface AuthProbeContract {
    suspend fun check(): ServiceStatus
}
