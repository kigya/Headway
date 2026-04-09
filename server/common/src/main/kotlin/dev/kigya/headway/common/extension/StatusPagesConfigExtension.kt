package dev.kigya.headway.common.extension

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.ParameterConversionException
import io.ktor.server.plugins.PayloadTooLargeException
import io.ktor.server.plugins.UnsupportedMediaTypeException
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respondText

fun StatusPagesConfig.handleDefaultExceptions() {
    exception<BadRequestException> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Bad request"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.BadRequest,
        )
    }

    exception<NotFoundException> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Not found"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.NotFound,
        )
    }

    exception<MissingRequestParameterException> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Bad request"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.BadRequest,
        )
    }

    exception<ParameterConversionException> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Bad request"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.BadRequest,
        )
    }

    exception<CannotTransformContentToTypeException> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Bad request"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.BadRequest,
        )
    }

    exception<UnsupportedMediaTypeException> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Unsupported media type"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.UnsupportedMediaType,
        )
    }

    exception<PayloadTooLargeException> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Payload too large"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.PayloadTooLarge,
        )
    }

    exception<IllegalStateException> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Internal server error"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.InternalServerError,
        )
    }

    exception<SecurityException> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Unauthorized"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.Unauthorized,
        )
    }

    exception<Throwable> { call, exception ->
        call.respondText(
            text = exception.describeForHttpStatusText("Internal server error"),
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.InternalServerError,
        )
    }
}

fun Throwable.describeForHttpStatusText(fallback: String): String {
    val trimmed = message?.trim().orEmpty()
    if (trimmed.isNotEmpty()) {
        return trimmed
    }
    return this::class.simpleName ?: fallback
}
