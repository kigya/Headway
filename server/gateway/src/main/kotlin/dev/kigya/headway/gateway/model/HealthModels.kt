package dev.kigya.headway.gateway.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class ServiceStatus {
    @SerialName("OK")
    OK,

    @SerialName("DEGRADED")
    DEGRADED,

    @SerialName("DOWN")
    DOWN;
}

@Serializable
internal data class DependencyHealth(
    @SerialName("name") val name: String,
    @SerialName("status") val status: ServiceStatus,
)

@Serializable
internal data class HealthPayload(
    @SerialName("service") val service: String,
    @SerialName("status") val status: ServiceStatus,
    @SerialName("uptimeSec") val uptimeSec: Long,
    @SerialName("dependencies") val dependencies: List<DependencyHealth> = emptyList(),
)
