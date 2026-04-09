package dev.kigya.headway.gateway.presentation

import dev.kigya.headway.common.util.Environment
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

internal fun Application.installHeadwayGatewayCors(environment: Environment) {
    if (environment.isProd) {
        return
    }
    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(X_HEADWAY_LOCALE_HEADER_NAME)
        allowNonSimpleContentTypes = true
    }
}

internal const val X_HEADWAY_LOCALE_HEADER_NAME: String = "X-Headway-Locale"
