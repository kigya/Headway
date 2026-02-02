package dev.kigya.headway.gateway.internal.client

import dev.kigya.headway.gateway.internal.config.ConfigurationValues
import dev.kigya.headway.gateway.internal.exception.DatabaseServiceException
import dev.kigya.headway.gateway.internal.exception.GatewayErrorCode
import dev.kigya.headway.gateway.internal.model.DatabaseServiceErrorResponse
import dev.kigya.headway.gateway.internal.model.DatabaseServiceUserDto
import dev.kigya.headway.gateway.internal.model.InviteUserRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

internal class HttpDatabaseServiceClient(
    private val httpClient: HttpClient,
) : DatabaseServiceClientContract {

    override suspend fun inviteUser(email: String, department: String): DatabaseServiceUserDto {
        val response = httpClient.post("${ConfigurationValues.DATABASE_SERVICE_URL}/user/invite") {
            contentType(ContentType.Application.Json)
            setBody(InviteUserRequest(email = email, department = department))
        }

        if (response.status.isSuccess()) return response.body()

        throw response.toDatabaseServiceException()
    }
}

private suspend fun io.ktor.client.statement.HttpResponse.toDatabaseServiceException(): DatabaseServiceException {
    val payload = runCatching { body<DatabaseServiceErrorResponse>() }.getOrNull()

    val code = when (payload?.code) {
        "BAD_REQUEST" -> GatewayErrorCode.BAD_REQUEST
        "UNAUTHORIZED" -> GatewayErrorCode.UNAUTHORIZED
        "FORBIDDEN" -> GatewayErrorCode.FORBIDDEN
        "DEPENDENCY_UNAVAILABLE" -> GatewayErrorCode.DEPENDENCY_UNAVAILABLE
        "INTERNAL" -> GatewayErrorCode.INTERNAL
        else -> GatewayErrorCode.DEPENDENCY_UNAVAILABLE
    }

    return DatabaseServiceException(code = code)
}
