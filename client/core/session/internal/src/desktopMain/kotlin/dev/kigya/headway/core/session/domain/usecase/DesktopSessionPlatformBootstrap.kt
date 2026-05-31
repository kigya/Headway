package dev.kigya.headway.core.session.domain.usecase

fun registerDesktopSessionPlatformHooks() {
    DesktopAppForeground.registerUriHandler()
    DesktopOAuthAbandonmentMonitor.registerWindowActivationListener()
}
