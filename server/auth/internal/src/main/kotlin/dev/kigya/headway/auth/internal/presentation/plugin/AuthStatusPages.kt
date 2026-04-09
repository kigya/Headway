package dev.kigya.headway.auth.internal.presentation.plugin

import dev.kigya.headway.auth.internal.domain.error.AuthException
import dev.kigya.headway.common.extension.describeForHttpStatusText
import dev.kigya.headway.common.extension.handleDefaultExceptions
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respondText

internal fun Application.authStatusPages() {
    install(StatusPages) {
        handleAuthExceptions()
        handleDefaultExceptions()
    }
}

private fun StatusPagesConfig.handleAuthExceptions() {
    exception<AuthException.InvalidRequest> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Invalid request"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.BadRequest,
        )
    }

    exception<AuthException.Unauthorized> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Unauthorized"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.Unauthorized,
        )
    }

    exception<AuthException.UserNotInvited> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Forbidden"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.Forbidden,
        )
    }

    exception<AuthException.UserNotActive> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Forbidden"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.Forbidden,
        )
    }

    exception<AuthException.Forbidden> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Forbidden"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.Forbidden,
        )
    }

    exception<AuthException.IdentityConflict> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Conflict"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.Conflict,
        )
    }

    exception<AuthException.DependencyUnavailable> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Service unavailable"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.ServiceUnavailable,
        )
    }

    exception<AuthException.UpstreamProtocol> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Bad gateway"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.BadGateway,
        )
    }
}
