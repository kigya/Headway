package dev.kigya.headway.admin.internal.presentation.plugin

import dev.kigya.headway.admin.internal.core.exception.AdminException
import dev.kigya.headway.common.extension.describeForHttpStatusText
import dev.kigya.headway.common.extension.handleDefaultExceptions
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respondText

internal fun Application.adminStatusPages() {
    install(StatusPages) {
        handleAdminExceptions()
        handleDefaultExceptions()
    }
}

private fun StatusPagesConfig.handleAdminExceptions() {
    exception<AdminException> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Error"),
            contentType = ContentType.Text.Plain,
            status = exception.httpStatus,
        )
    }
}
