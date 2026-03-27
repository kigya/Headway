package dev.kigya.headway.gateway.domain.auth

internal enum class GatewayOperation {
    HealthCheck,
    InviteUser,
    HomeScreen,
    Preparation,
    LearningRead,
    LearningRemarkWrite,
    LogoutGuest,
}
