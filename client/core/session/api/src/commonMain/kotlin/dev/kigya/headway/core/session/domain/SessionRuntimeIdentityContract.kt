package dev.kigya.headway.core.session.domain

interface SessionRuntimeIdentityContract {
    val deviceFingerprint: String
    val sessionGatewayPlatform: HeadwaySessionGatewayPlatform
}
