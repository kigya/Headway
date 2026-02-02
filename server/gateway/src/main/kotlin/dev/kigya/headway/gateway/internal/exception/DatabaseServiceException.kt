package dev.kigya.headway.gateway.internal.exception

internal class DatabaseServiceException(
    val code: GatewayErrorCode,
    cause: Throwable? = null,
) : RuntimeException(null, cause)
