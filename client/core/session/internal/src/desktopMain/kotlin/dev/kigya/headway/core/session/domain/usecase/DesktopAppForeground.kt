package dev.kigya.headway.core.session.domain.usecase

import java.awt.Desktop
import java.awt.EventQueue
import java.awt.Frame
import java.net.URI

internal object DesktopAppForeground {

    fun registerUriHandler() {
        if (!Desktop.isDesktopSupported()) {
            return
        }
        runCatching {
            Desktop.getDesktop().setOpenURIHandler { event ->
                if (isOAuthCompleteUri(event.uri)) {
                    bringToForeground()
                }
            }
        }
    }

    fun bringToForeground() {
        EventQueue.invokeLater {
            runCatching {
                val applicationClass = Class.forName("com.apple.eawt.Application")
                val application = applicationClass.getMethod("getApplication").invoke(null)
                applicationClass.getMethod("requestForeground", Boolean::class.javaPrimitiveType)
                    .invoke(application, true)
            }
            for (frame in Frame.getFrames()) {
                if (frame.isDisplayable && frame.isVisible) {
                    if (frame.extendedState and Frame.ICONIFIED != 0) {
                        frame.extendedState = Frame.NORMAL
                    }
                    frame.toFront()
                    frame.requestFocus()
                }
            }
        }
    }
}

private fun isOAuthCompleteUri(uri: URI): Boolean =
    uri.scheme == HEADWAY_DESKTOP_URL_SCHEME &&
        uri.host == HEADWAY_OAUTH_COMPLETE_HOST &&
        uri.path == HEADWAY_OAUTH_COMPLETE_PATH

internal const val HEADWAY_DESKTOP_URL_SCHEME: String = "headway"

internal const val HEADWAY_OAUTH_COMPLETE_URI: String = "headway://oauth/complete"

private const val HEADWAY_OAUTH_COMPLETE_HOST: String = "oauth"

private const val HEADWAY_OAUTH_COMPLETE_PATH: String = "/complete"
