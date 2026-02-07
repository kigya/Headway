package dev.kigya.headway.gateway.data.probe.base

import dev.kigya.headway.gateway.model.ServiceStatus

fun interface HttpProber {
    suspend fun check(): ServiceStatus
}
