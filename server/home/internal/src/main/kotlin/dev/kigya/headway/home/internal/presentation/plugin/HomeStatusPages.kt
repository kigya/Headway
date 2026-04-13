package dev.kigya.headway.home.internal.presentation.plugin

import dev.kigya.headway.common.extension.describeForHttpStatusText
import dev.kigya.headway.common.extension.handleDefaultExceptions
import dev.kigya.headway.home.internal.domain.error.HomeException
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respondText

internal fun Application.homeStatusPages() {
    install(StatusPages) {
        handleHomeExceptions()
        handleDefaultExceptions()
    }
}

private fun StatusPagesConfig.handleHomeExceptions() {
    exception<HomeException.InvalidRequest> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Bad request"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.BadRequest,
        )
    }
}
