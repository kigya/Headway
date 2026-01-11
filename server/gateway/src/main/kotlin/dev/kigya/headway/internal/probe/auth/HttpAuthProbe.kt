// server/gateway/src/main/kotlin/dev/kigya/headway/internal/probe/HttpAuthProbe.kt
package dev.kigya.headway.internal.probe.auth

import dev.kigya.headway.api.model.ServiceStatus
import dev.kigya.headway.internal.config.ConfigurationValues
import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.http.isSuccess

internal class HttpAuthProbe(
    private val httpClient: HttpClient,
) : AuthProbeContract {

    override suspend fun check(): ServiceStatus {
        return try {
            val response = httpClient.get("${ConfigurationValues.AUTH_SERVICE_URL}/healthz") {
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
}

private const val DEFAULT_TIMEOUT = 1_000L
