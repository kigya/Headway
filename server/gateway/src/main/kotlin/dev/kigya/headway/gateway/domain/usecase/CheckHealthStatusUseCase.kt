package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.data.probe.base.HttpProber
import dev.kigya.headway.gateway.model.DependencyHealth
import dev.kigya.headway.gateway.model.HealthPayload
import dev.kigya.headway.gateway.model.ServiceStatus

internal class CheckHealthStatusUseCase(
    private val authProbe: HttpProber,
    private val databaseProbe: HttpProber,
) {
    private val startedAtNanos: Long = System.nanoTime()

    suspend operator fun invoke(): HealthPayload {

        val uptimeSeconds = ((System.nanoTime() - startedAtNanos) / NANOS_IN_SECOND).coerceAtLeast(0L)
        val authStatus = authProbe.check()
        val databaseStatus = databaseProbe.check()

        val overallStatus = when (authStatus) {
            ServiceStatus.OK if databaseStatus == ServiceStatus.OK -> ServiceStatus.OK
            ServiceStatus.DOWN if databaseStatus == ServiceStatus.DOWN -> ServiceStatus.DOWN
            else -> ServiceStatus.DEGRADED
        }

        return HealthPayload(
            service = "gateway",
            status = overallStatus,
            uptimeSec = uptimeSeconds,
            dependencies = listOf(
                DependencyHealth(name = "auth", status = authStatus),
                DependencyHealth(name = "database", status = databaseStatus),
            ),
        )
    }
}

private const val NANOS_IN_SECOND = 1_000_000_000L
