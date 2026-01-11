package dev.kigya.headway.database.internal.ext

import dev.kigya.headway.database.api.model.ExposedUser
import dev.kigya.headway.database.internal.data.UsersServiceImpl
import org.jetbrains.exposed.v1.core.ResultRow

internal fun ResultRow.toUser(): ExposedUser {
    val authUserId = this[UsersServiceImpl.UsersTable.authUserId]
    return ExposedUser(
        id = this[UsersServiceImpl.UsersTable.id].value,
        // В контракте у тебя поле называется google_id. Реально в Supabase есть auth_user_id (uuid).
        // Возвращаем его строкой, чтобы не ломать формат ответа.
        googleId = authUserId?.toString(),
        email = this[UsersServiceImpl.UsersTable.email],
        name = this[UsersServiceImpl.UsersTable.fullName],
        role = this[UsersServiceImpl.UsersTable.role],
        avatarUrl = this[UsersServiceImpl.UsersTable.avatarUrl],
        isActive = this[UsersServiceImpl.UsersTable.isActive],
        createdAt = this[UsersServiceImpl.UsersTable.createdAt].toInstant().toEpochMilli(),
        updatedAt = this[UsersServiceImpl.UsersTable.updatedAt].toInstant().toEpochMilli(),
    )
}

