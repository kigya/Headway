package dev.kigya.headway.core.session.domain.usecase

internal object DesktopOAuthLoopbackEndpoint {

    const val HOST: String = "127.0.0.1"

    const val PORT: Int = 8405

    const val CALLBACK_PATH: String = "/oauth2callback"

    const val OPEN_APP_PATH: String = "/open-headway"

    val redirectUri: String
        get() = "http://$HOST:$PORT$CALLBACK_PATH"

    val openAppHttpUri: String
        get() = "http://$HOST:$PORT$OPEN_APP_PATH"
}
