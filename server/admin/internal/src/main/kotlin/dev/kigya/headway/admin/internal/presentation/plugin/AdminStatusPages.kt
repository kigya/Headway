package dev.kigya.headway.admin.internal.presentation.plugin

import dev.kigya.headway.admin.internal.core.exception.AdminException
import dev.kigya.headway.common.extension.handleDefaultExceptions
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

internal fun Application.adminStatusPages() {
    install(StatusPages) {
        handleDefaultExceptions()
        handleAdminExceptions()
    }
}

private fun StatusPagesConfig.handleAdminExceptions() {
    exception<AdminException> { call, exception ->
        call.respond(
            status = exception.httpStatus,
            message = exception.message,
        )
    }
}
