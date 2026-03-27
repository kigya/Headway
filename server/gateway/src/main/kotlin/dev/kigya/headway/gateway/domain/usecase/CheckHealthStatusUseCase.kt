package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.data.probe.base.HttpProber
import dev.kigya.headway.gateway.model.GatewayDependencyHealth
import dev.kigya.headway.gateway.model.GatewayHealthPayload
import dev.kigya.headway.gateway.model.GatewayServiceStatus

internal class CheckHealthStatusUseCase(
    private val authProbe: HttpProber,
    private val databaseProbe: HttpProber,
    private val homeProbe: HttpProber,
) {
    private val startedAtNanos: Long = System.nanoTime()

    suspend operator fun invoke(): GatewayHealthPayload {
        val uptimeSeconds = ((System.nanoTime() - startedAtNanos) / NANOS_IN_SECOND).coerceAtLeast(0L)
        val authStatus = authProbe.check()
        val databaseStatus = databaseProbe.check()
        val homeStatus = homeProbe.check()

        val dependencies = listOf(
            GatewayDependencyHealth(name = "auth", status = authStatus),
            GatewayDependencyHealth(name = "database", status = databaseStatus),
            GatewayDependencyHealth(name = "home", status = homeStatus),
        )

        val statuses = listOf(authStatus, databaseStatus, homeStatus)
        val overallStatus = when {
            statuses.all { it == GatewayServiceStatus.OK } -> GatewayServiceStatus.OK
            statuses.all { it == GatewayServiceStatus.DOWN } -> GatewayServiceStatus.DOWN
            else -> GatewayServiceStatus.DEGRADED
        }

        return GatewayHealthPayload(
            service = "gateway",
            status = overallStatus,
            uptimeSec = uptimeSeconds,
            dependencies = dependencies,
        )
    }
}

private const val NANOS_IN_SECOND = 1_000_000_000L
