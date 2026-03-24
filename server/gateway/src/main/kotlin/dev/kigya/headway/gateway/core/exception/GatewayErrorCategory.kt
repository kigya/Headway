package dev.kigya.headway.gateway.core.exception

internal enum class GatewayErrorCategory {
    VALIDATION,
    AUTHENTICATION,
    AUTHORIZATION,
    CONFLICT,
    DEPENDENCY,
    INTERNAL,
    NOT_FOUND,
}
