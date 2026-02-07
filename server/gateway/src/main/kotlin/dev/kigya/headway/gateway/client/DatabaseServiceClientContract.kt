package dev.kigya.headway.gateway.client

import dev.kigya.headway.database.api.model.out.DatabaseUser

internal interface DatabaseServiceClientContract {
    suspend fun inviteUser(email: String, department: String): DatabaseUser
}
