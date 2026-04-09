package dev.kigya.headway.database.internal.presentation.plugin

import dev.kigya.headway.common.extension.describeForHttpStatusText
import dev.kigya.headway.common.extension.handleDefaultExceptions
import dev.kigya.headway.database.internal.domain.error.DatabaseException
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respondText

internal fun Application.databaseStatusPages() {
    install(StatusPages) {
        handleDatabaseExceptions()
        handleDefaultExceptions()
    }
}

private fun StatusPagesConfig.handleDatabaseExceptions() {
    exception<DatabaseException.InvalidRequest> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Bad request"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.BadRequest,
        )
    }

    exception<DatabaseException.Unauthorized> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Unauthorized"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.Unauthorized,
        )
    }

    exception<DatabaseException.UserNotInvited> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Forbidden"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.Forbidden,
        )
    }

    exception<DatabaseException.NotFound> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Not found"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.NotFound,
        )
    }

    exception<DatabaseException.PreparationForbidden> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Forbidden"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.Forbidden,
        )
    }

    exception<DatabaseException.PreparationConflict> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Conflict"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.Conflict,
        )
    }

    exception<DatabaseException.Forbidden> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Forbidden"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.Forbidden,
        )
    }

    exception<DatabaseException.UserAlreadyExists> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Conflict"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.Conflict,
        )
    }

    exception<DatabaseException.Conflict> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Conflict"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.Conflict,
        )
    }

    exception<DatabaseException.DependencyUnavailable> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Service unavailable"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.ServiceUnavailable,
        )
    }
}
