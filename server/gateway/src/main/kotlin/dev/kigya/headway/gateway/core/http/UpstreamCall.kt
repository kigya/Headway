package dev.kigya.headway.gateway.core.http

import dev.kigya.headway.gateway.core.exception.GatewayException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

internal suspend inline fun <T> upstreamCall(
    dependency: String,
    crossinline request: suspend () -> HttpResponse,
    crossinline onSuccess: suspend (HttpResponse) -> T,
): T {
    val response = try {
        request()
    } catch (t: Throwable) {
        throw GatewayException.DependencyUnavailable(dependency = dependency, cause = t)
    }

    if (response.status.isSuccess()) return onSuccess(response)
    throw response.toGatewayException(dependency = dependency)
}
