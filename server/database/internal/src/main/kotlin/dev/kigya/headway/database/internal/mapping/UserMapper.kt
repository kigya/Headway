package dev.kigya.headway.database.internal.mapping

import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.api.model.out.DatabaseUserDepartment
import dev.kigya.headway.database.internal.data.table.UsersTable
import org.jetbrains.exposed.v1.core.ResultRow

internal fun ResultRow.toUser(): DatabaseUser {
    val authUserId = this[UsersTable.authUserId]
    val email = this[UsersTable.email]
    return DatabaseUser(
        id = this[UsersTable.id].value,
        googleId = authUserId?.toString(),
        email = email,
        name = this[UsersTable.fullName] ?: email,
        department = this[UsersTable.department],
        role = this[UsersTable.role],
        avatarUrl = this[UsersTable.avatarUrl],
        isActive = this[UsersTable.isActive],
        createdAt = this[UsersTable.createdAt].toInstant().toEpochMilli(),
        updatedAt = this[UsersTable.updatedAt].toInstant().toEpochMilli(),
    )
}

internal fun String.toExposedDepartment(): DatabaseUserDepartment =
    when (this.trim().uppercase()) {
        "ANDROID" -> DatabaseUserDepartment.ANDROID
        "IOS" -> DatabaseUserDepartment.IOS
        "CROSS_PLATFORM" -> DatabaseUserDepartment.CROSSPLATFORM
        else -> error("Unknown department: '$this'")
    }

