package dev.kigya.headway.gateway.data.client

import dev.kigya.headway.database.api.model.`in`.DatabaseInviteUserPayloadDto
import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.api.model.resource.DatabaseUsersResource
import dev.kigya.headway.gateway.core.http.upstreamCall
import dev.kigya.headway.gateway.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.gateway.mapping.toGateway
import dev.kigya.headway.gateway.model.GatewayUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody

internal class DatabaseRepository(
    private val httpClient: HttpClient,
) : DatabaseRepositoryContract {

    override suspend fun inviteUser(email: String, department: String): GatewayUser = upstreamCall(
        dependency = "database",
        request = {
            httpClient.post(DatabaseUsersResource.Invite()) {
                setBody(DatabaseInviteUserPayloadDto(email = email, department = department))
            }
        },
        onSuccess = { it.body<DatabaseUser>().toGateway() },
    )
}
