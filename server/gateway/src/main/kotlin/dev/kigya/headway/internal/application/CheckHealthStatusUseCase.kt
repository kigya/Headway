package dev.kigya.headway.internal.application

import dev.kigya.headway.api.model.DependencyHealth
import dev.kigya.headway.api.model.HealthPayload
import dev.kigya.headway.api.model.ServiceStatus
import dev.kigya.headway.api.port.CheckHealthStatusUseCaseContract
import dev.kigya.headway.internal.probe.auth.AuthProbeContract

private const val NANOS_IN_SECOND = 1_000_000_000L

internal class CheckHealthStatusUseCase(
    private val authProbe: AuthProbeContract,
) : CheckHealthStatusUseCaseContract {

    private val startedAtNanos: Long = System.nanoTime()

    override suspend fun invoke(): HealthPayload {

        val uptimeSeconds = ((System.nanoTime() - startedAtNanos) / NANOS_IN_SECOND).coerceAtLeast(0L)
        val authStatus = authProbe.check()

        val overallStatus = when (authStatus) {
            ServiceStatus.OK -> ServiceStatus.OK
            else -> ServiceStatus.DEGRADED
        }

        return HealthPayload(
            service = "gateway",
            status = overallStatus,
            uptimeSec = uptimeSeconds,
            dependencies = listOf(
                DependencyHealth(name = "auth", status = authStatus),
            ),
        )
    }
}
