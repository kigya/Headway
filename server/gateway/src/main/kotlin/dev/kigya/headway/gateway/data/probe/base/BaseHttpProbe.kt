package dev.kigya.headway.gateway.data.probe.base

import dev.kigya.headway.common.model.resource.HealthzResource
import dev.kigya.headway.gateway.model.ServiceStatus
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.timeout
import io.ktor.http.isSuccess

open class BaseHttpProbe(
    private val httpClient: HttpClient,
) : HttpProber {
    override suspend fun check(): ServiceStatus = try {
        val response = httpClient.get(HealthzResource()) {
            timeout {
                connectTimeoutMillis = DEFAULT_TIMEOUT
                requestTimeoutMillis = DEFAULT_TIMEOUT
                socketTimeoutMillis = DEFAULT_TIMEOUT
            }
        }
        if (response.status.isSuccess()) ServiceStatus.OK else ServiceStatus.DOWN
    } catch (_: Throwable) {
        ServiceStatus.DOWN
    }
}

private const val DEFAULT_TIMEOUT = 1_000L
