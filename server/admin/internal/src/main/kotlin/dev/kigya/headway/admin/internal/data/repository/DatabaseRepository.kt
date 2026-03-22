package dev.kigya.headway.admin.internal.data.repository

import dev.kigya.headway.admin.internal.core.exception.AdminException
import dev.kigya.headway.admin.internal.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.database.api.model.`in`.DatabaseInviteUserPayloadDto
import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.api.model.out.DatabaseUserDepartment
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.api.model.resource.DatabaseResource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

internal class DatabaseRepository(
    private val httpClient: HttpClient,
) : DatabaseRepositoryContract {
    override suspend fun inviteUser(
        email: String,
        role: DatabaseUserRole,
        department: DatabaseUserDepartment,
    ): DatabaseUser = requestDatabase(
        request = {
            httpClient.post(DatabaseResource.User.Invite()) {
                contentType(ContentType.Application.Json)
                setBody(
                    DatabaseInviteUserPayloadDto(
                        email = email,
                        department = department.slug,
                        role = role,
                    ),
                )
            }
        },
        onSuccess = { it.body<DatabaseUser>() },
    )
}

private suspend fun <T> requestDatabase(
    request: suspend () -> HttpResponse,
    onSuccess: suspend (HttpResponse) -> T,
): T = try {
    val response = request()
    if (response.status.value in HTTP_SUCCESS_RANGE) {
        onSuccess(response)
    } else {
        throw response.toAdminException()
    }
} catch (exception: AdminException) {
    throw exception
} catch (exception: Throwable) {
    throw AdminException.DependencyUnavailable(
        dependency = DATABASE_DEPENDENCY,
        cause = exception,
    )
}

private suspend fun HttpResponse.toAdminException(): AdminException {
    val message = safeBodyMessage()
    return when (status) {
        HttpStatusCode.BadRequest -> AdminException.InvalidRequest(message)
        HttpStatusCode.Unauthorized -> AdminException.Unauthorized(message)
        HttpStatusCode.Forbidden -> AdminException.Forbidden(message)
        HttpStatusCode.NotFound -> AdminException.NotFound(message)
        HttpStatusCode.Conflict -> AdminException.Conflict(message)
        HttpStatusCode.ServiceUnavailable -> AdminException.DependencyUnavailable(
            dependency = DATABASE_DEPENDENCY,
            message = message,
        )

        else -> AdminException.UpstreamProtocol(
            dependency = DATABASE_DEPENDENCY,
            status = status.value,
            message = message,
        )
    }
}

private suspend fun HttpResponse.safeBodyMessage(): String = bodyAsText()
    .replace(Regex("\\s+"), " ")
    .trim()
    .takeIf(String::isNotBlank)
    ?.take(MAX_ERROR_MESSAGE_LENGTH)
    ?: "Service temporarily unavailable"

private const val DATABASE_DEPENDENCY = "database"
private const val MAX_ERROR_MESSAGE_LENGTH = 2048
private const val HTTP_SUCCESS_MIN = 200
private const val HTTP_SUCCESS_MAX = 299
private val HTTP_SUCCESS_RANGE = HTTP_SUCCESS_MIN..HTTP_SUCCESS_MAX
