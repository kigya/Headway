package dev.kigya.headway.gateway.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class GatewayServiceStatus {
    @SerialName("OK")
    OK,

    @SerialName("DEGRADED")
    DEGRADED,

    @SerialName("DOWN")
    DOWN,
}

@Serializable
internal data class GatewayDependencyHealth(
    @SerialName("name") val name: String,
    @SerialName("status") val status: GatewayServiceStatus,
)

@Serializable
internal data class GatewayHealthPayload(
    @SerialName("service") val service: String,
    @SerialName("status") val status: GatewayServiceStatus,
    @SerialName("uptimeSec") val uptimeSec: Long,
    @SerialName("dependencies") val dependencies: List<GatewayDependencyHealth> = emptyList(),
)
