package dev.kigya.headway.database.internal.mapping

import dev.kigya.headway.database.api.model.ExposedDepartment
import dev.kigya.headway.database.api.model.ExposedUser
import dev.kigya.headway.database.internal.data.table.UsersTable
import org.jetbrains.exposed.v1.core.ResultRow

internal fun ResultRow.toUser(): ExposedUser {
    val authUserId = this[UsersTable.authUserId]
    val email = this[UsersTable.email]
    return ExposedUser(
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

internal fun String.toExposedDepartment(): ExposedDepartment =
    when (this.trim().uppercase()) {
        "ANDROID" -> ExposedDepartment.ANDROID
        "IOS" -> ExposedDepartment.IOS
        "CROSS_PLATFORM" -> ExposedDepartment.CROSSPLATFORM
        else -> error("Unknown department: '$this'")
    }

