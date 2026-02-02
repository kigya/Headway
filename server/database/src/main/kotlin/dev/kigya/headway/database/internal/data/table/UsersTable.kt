package dev.kigya.headway.database.internal.data.table

import dev.kigya.headway.database.api.model.ExposedAccountStatus
import dev.kigya.headway.database.api.model.ExposedDepartment
import dev.kigya.headway.database.api.model.ExposedUserRole
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import org.postgresql.util.PGobject

internal object UsersTable : UUIDTable(name = "public.profiles") {

    private fun enumDbValue(value: Any): String =
        (value as? PGobject)?.value ?: value.toString()

    private fun pgEnum(type: String, value: String) = PGobject().apply {
        this.type = type
        this.value = value
    }

    val authUserId = uuid("auth_user_id").nullable()

    val email = text("email").uniqueIndex()
    val fullName = text("full_name").nullable()

    val department = customEnumeration(
        name = "department",
        sql = "department",
        fromDb = { value ->
            val slug = enumDbValue(value)
            ExposedDepartment.entries.firstOrNull { it.slug == slug }
                ?: error("Unknown department value: '$slug'")
        },
        toDb = { dep -> pgEnum("department", dep.slug) }
    )

    val role = customEnumeration(
        name = "role",
        sql = "user_role",
        fromDb = { value ->
            val slug = enumDbValue(value)
            ExposedUserRole.entries.firstOrNull { it.slug == slug }
                ?: error("Unknown user_role value: '$slug'")
        },
        toDb = { r -> pgEnum("user_role", r.slug) }
    )

    val status = customEnumeration(
        name = "status",
        sql = "account_status",
        fromDb = { value ->
            val slug = enumDbValue(value)
            ExposedAccountStatus.entries.firstOrNull { it.slug == slug }
                ?: error("Unknown account_status value: '$slug'")
        },
        toDb = { s -> pgEnum("account_status", s.slug) }
    )

    val avatarUrl = text("avatar_url").nullable()
    val isActive = bool("is_active")

    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
}
