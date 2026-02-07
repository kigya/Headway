package dev.kigya.headway.common.extension

import dev.kigya.headway.common.exception.BadRequestException
import dev.kigya.headway.common.exception.InternalServerException
import dev.kigya.headway.common.exception.UserDoesNotExistException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

suspend inline fun <reified T> HttpResponse.successBodyOrThrow(
    customHandler: (HttpResponse) -> T? = { null },
): T {
    val response = this
    val result = runCatching { customHandler(response) }.getOrNull()

    return result ?: when (response.status) {
        HttpStatusCode.OK -> response.body<T>()
        HttpStatusCode.BadRequest -> throw BadRequestException(response.body())
        HttpStatusCode.NotFound -> throw UserDoesNotExistException(response.body())
        else -> throw InternalServerException(response.body())
    }
}
