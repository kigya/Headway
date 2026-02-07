package dev.kigya.headway.gateway.model

import kotlinx.serialization.Serializable

@Serializable
internal enum class ServiceStatus { OK, DEGRADED, DOWN }

@Serializable
internal data class DependencyHealth(
    val name: String,
    val status: ServiceStatus,
)

@Serializable
internal data class HealthPayload(
    val service: String,
    val status: ServiceStatus,
    val uptimeSec: Long,
    val dependencies: List<DependencyHealth> = emptyList(),
)
