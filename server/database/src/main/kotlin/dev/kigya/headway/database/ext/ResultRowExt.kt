package dev.kigya.headway.database.ext

import dev.kigya.headway.database.data.UsersServiceImpl
import dev.kigya.headway.database.domain.model.ExposedUser
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import java.time.ZoneOffset

internal fun ResultRow.toUser(): ExposedUser = ExposedUser(
    id = this[UsersServiceImpl.UsersTable.id].value,
    googleId = this[UsersServiceImpl.UsersTable.googleId],
    email = this[UsersServiceImpl.UsersTable.email],
    name = this[UsersServiceImpl.UsersTable.name],
    role = this[UsersServiceImpl.UsersTable.role],
    avatarUrl = this[UsersServiceImpl.UsersTable.avatarUrl],
    isActive = this[UsersServiceImpl.UsersTable.isActive],
    createdAt = this[UsersServiceImpl.UsersTable.createdAt].toInstant().toEpochMilli(),
    updatedAt = this[UsersServiceImpl.UsersTable.updatedAt]
        .toJavaLocalDateTime()
        .toInstant(ZoneOffset.UTC)
        .toEpochMilli(),
)
