package dev.kigya.headway.common.extension

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.ParameterConversionException
import io.ktor.server.plugins.PayloadTooLargeException
import io.ktor.server.plugins.UnsupportedMediaTypeException
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

fun StatusPagesConfig.handleDefaultExceptions() {
    exception<BadRequestException> { call, exception ->
        call.respond(
            status = HttpStatusCode.BadRequest,
            message = exception.message.orEmpty(),
        )
    }

    exception<NotFoundException> { call, exception ->
        call.respond(
            status = HttpStatusCode.NotFound,
            message = exception.message.orEmpty(),
        )
    }

    exception<MissingRequestParameterException> { call, exception ->
        call.respond(
            status = HttpStatusCode.BadRequest,
            message = exception.message.orEmpty(),
        )
    }

    exception<ParameterConversionException> { call, exception ->
        call.respond(
            status = HttpStatusCode.BadRequest,
            message = exception.message.orEmpty(),
        )
    }

    exception<CannotTransformContentToTypeException> { call, exception ->
        call.respond(
            status = HttpStatusCode.BadRequest,
            message = exception.message.orEmpty(),
        )
    }

    exception<UnsupportedMediaTypeException> { call, exception ->
        call.respond(
            status = HttpStatusCode.UnsupportedMediaType,
            message = exception.message.orEmpty(),
        )
    }

    exception<PayloadTooLargeException> { call, exception ->
        call.respond(
            status = HttpStatusCode.PayloadTooLarge,
            message = exception.message.orEmpty(),
        )
    }

    exception<IllegalStateException> { call, exception ->
        call.respond(
            status = HttpStatusCode.InternalServerError,
            message = exception.message.orEmpty(),
        )
    }

    exception<SecurityException> { call, exception ->
        call.respond(
            status = HttpStatusCode.Unauthorized,
            message = exception.message.orEmpty(),
        )
    }

    exception<Throwable> { call, exception ->
        call.respond(
            status = HttpStatusCode.InternalServerError,
            message = exception.message.orEmpty(),
        )
    }
}
