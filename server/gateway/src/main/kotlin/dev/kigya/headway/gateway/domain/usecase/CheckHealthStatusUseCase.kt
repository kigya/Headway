package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.data.probe.base.HttpProber
import dev.kigya.headway.gateway.model.GatewayDependencyHealth
import dev.kigya.headway.gateway.model.GatewayHealthPayload
import dev.kigya.headway.gateway.model.GatewayServiceStatus

internal class CheckHealthStatusUseCase(
    private val authProbe: HttpProber,
    private val databaseProbe: HttpProber,
) {
    private val startedAtNanos: Long = System.nanoTime()

    suspend operator fun invoke(): GatewayHealthPayload {
        val uptimeSeconds = ((System.nanoTime() - startedAtNanos) / NANOS_IN_SECOND).coerceAtLeast(0L)
        val authStatus = authProbe.check()
        val databaseStatus = databaseProbe.check()

        val overallStatus = when (authStatus) {
            GatewayServiceStatus.OK if databaseStatus == GatewayServiceStatus.OK -> GatewayServiceStatus.OK
            GatewayServiceStatus.DOWN if databaseStatus == GatewayServiceStatus.DOWN -> GatewayServiceStatus.DOWN
            else -> GatewayServiceStatus.DEGRADED
        }

        return GatewayHealthPayload(
            service = "gateway",
            status = overallStatus,
            uptimeSec = uptimeSeconds,
            dependencies = listOf(
                GatewayDependencyHealth(name = "auth", status = authStatus),
                GatewayDependencyHealth(name = "database", status = databaseStatus),
            ),
        )
    }
}

private const val NANOS_IN_SECOND = 1_000_000_000L
