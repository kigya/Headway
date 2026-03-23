package dev.kigya.headway.auth.internal.presentation.plugin

import dev.kigya.headway.auth.internal.domain.error.AuthException
import dev.kigya.headway.common.extension.handleDefaultExceptions
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

internal fun Application.authStatusPages() {
    install(StatusPages) {
        handleDefaultExceptions()
        handleAuthExceptions()
    }
}

private fun StatusPagesConfig.handleAuthExceptions() {
    exception<AuthException.InvalidRequest> { call, exception ->
        call.respond(
            status = HttpStatusCode.BadRequest,
            message = exception.message,
        )
    }

    exception<AuthException.Unauthorized> { call, exception ->
        call.respond(
            status = HttpStatusCode.Unauthorized,
            message = exception.message,
        )
    }

    exception<AuthException.UserNotInvited> { call, exception ->
        call.respond(
            status = HttpStatusCode.Forbidden,
            message = exception.message,
        )
    }

    exception<AuthException.UserNotActive> { call, exception ->
        call.respond(
            status = HttpStatusCode.Forbidden,
            message = exception.message,
        )
    }

    exception<AuthException.Forbidden> { call, exception ->
        call.respond(HttpStatusCode.Forbidden, exception.message)
    }

    exception<AuthException.IdentityConflict> { call, exception ->
        call.respond(
            status = HttpStatusCode.Conflict,
            message = exception.message,
        )
    }

    exception<AuthException.DependencyUnavailable> { call, exception ->
        call.respond(
            status = HttpStatusCode.ServiceUnavailable,
            message = exception.message,
        )
    }

    exception<AuthException.UpstreamProtocol> { call, exception ->
        call.respond(
            status = HttpStatusCode.BadGateway,
            message = exception.message,
        )
    }
}
