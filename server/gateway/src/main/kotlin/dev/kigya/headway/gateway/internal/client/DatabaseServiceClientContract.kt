package dev.kigya.headway.gateway.internal.client

import dev.kigya.headway.gateway.internal.model.DatabaseServiceUserDto

internal interface DatabaseServiceClientContract {
    suspend fun inviteUser(email: String, department: String): DatabaseServiceUserDto
}
