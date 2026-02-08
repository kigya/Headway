package dev.kigya.headway.gateway.data.client

import dev.kigya.headway.database.api.model.`in`.DatabaseInviteUserPayloadDto
import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.api.model.resource.DatabaseUsersResource
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.gateway.mapping.toGateway
import dev.kigya.headway.gateway.model.GatewayUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

internal class DatabaseRepository(
    private val httpClient: HttpClient,
) : DatabaseRepositoryContract {

    override suspend fun inviteUser(email: String, department: String): GatewayUser {
        val response = try {
            httpClient.post(DatabaseUsersResource.Invite()) {
                setBody(DatabaseInviteUserPayloadDto(email = email, department = department))
            }
        } catch (t: Throwable) {
            throw GatewayException.DependencyUnavailable("database", cause = t)
        }

        if (response.status.isSuccess()) return response.body<DatabaseUser>().toGateway()
        throw response.toGatewayException(dependency = "database")
    }
}

private suspend fun HttpResponse.toGatewayException(dependency: String): GatewayException {
    val msg = runCatching { bodyAsText() }.getOrNull()?.trim().orEmpty()
    val safeMsg = msg.takeIf { it.isNotBlank() }

    return when (status) {
        HttpStatusCode.BadRequest ->
            GatewayException.InvalidRequest(safeMsg ?: "Bad request")

        HttpStatusCode.Unauthorized ->
            GatewayException.Unauthorized(safeMsg ?: "Unauthorized")

        HttpStatusCode.Forbidden ->
            GatewayException.Forbidden(safeMsg ?: "Forbidden")

        HttpStatusCode.ServiceUnavailable ->
            GatewayException.DependencyUnavailable(
                dependency,
                message = safeMsg ?: "Dependency unavailable: $dependency"
            )

        else ->
            GatewayException.UpstreamProtocol(
                dependency = dependency,
                status = status.value,
                message = safeMsg ?: "Unexpected response from $dependency: ${status.value}"
            )
    }
}
