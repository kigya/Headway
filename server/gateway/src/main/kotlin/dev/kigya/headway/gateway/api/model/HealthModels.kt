package dev.kigya.headway.gateway.api.model

import kotlinx.serialization.Serializable

@Serializable
enum class ServiceStatus { OK, DEGRADED, DOWN }

@Serializable
data class DependencyHealth(
    val name: String,
    val status: ServiceStatus,
)

@Serializable
data class HealthPayload(
    val service: String,
    val status: ServiceStatus,
    val uptimeSec: Long,
    val dependencies: List<DependencyHealth> = emptyList(),
)
