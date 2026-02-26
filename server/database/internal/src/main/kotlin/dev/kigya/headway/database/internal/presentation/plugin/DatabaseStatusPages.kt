package dev.kigya.headway.database.internal.presentation.plugin

import dev.kigya.headway.common.extension.handleDefaultExceptions
import dev.kigya.headway.database.internal.domain.error.DatabaseException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

internal fun Application.databaseStatusPages() {
    install(StatusPages) {
        handleDefaultExceptions()
        handleDatabaseExceptions()
    }
}

private fun StatusPagesConfig.handleDatabaseExceptions() {
    exception<DatabaseException.InvalidRequest> { call, exception ->
        call.respond(
            status = HttpStatusCode.BadRequest,
            message = exception.message,
        )
    }

    exception<DatabaseException.Unauthorized> { call, exception ->
        call.respond(
            status = HttpStatusCode.Unauthorized,
            message = exception.message,
        )
    }

    exception<DatabaseException.UserNotInvited> { call, exception ->
        call.respond(
            status = HttpStatusCode.Forbidden,
            message = exception.message,
        )
    }

    exception<DatabaseException.Forbidden> { call, exception ->
        call.respond(
            status = HttpStatusCode.Forbidden,
            message = exception.message,
        )
    }

    exception<DatabaseException.UserAlreadyExists> { call, exception ->
        call.respond(
            status = HttpStatusCode.Conflict,
            message = exception.user,
        )
    }

    exception<DatabaseException.Conflict> { call, exception ->
        call.respond(
            status = HttpStatusCode.Conflict,
            message = exception.message,
        )
    }

    exception<DatabaseException.DependencyUnavailable> { call, exception ->
        call.respond(
            status = HttpStatusCode.ServiceUnavailable,
            message = exception.message,
        )
    }
}
