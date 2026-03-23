package dev.kigya.headway.database.internal.data.table

import dev.kigya.headway.database.api.model.out.DatabaseUserDepartment
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.internal.data.model.ExposedAccountStatus
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import org.postgresql.util.PGobject

internal object UsersTable : UUIDTable(name = "public.users") {
    val authUserId = uuid(name = "auth_user_id").nullable()

    val googleSubject = text(name = "google_subject").nullable()

    val email = text(name = "email").uniqueIndex()
    val fullName = text(name = "full_name").nullable()

    val department = customEnumeration(
        name = "department",
        sql = "department",
        fromDb = { value ->
            val slug = enumDbValue(value)
            DatabaseUserDepartment.entries.firstOrNull { it.slug == slug }
                ?: error("Unknown department value: '$slug'")
        },
        toDb = { department ->
            pgEnum(
                type = "department",
                value = department.slug,
            )
        },
    )

    val role = customEnumeration(
        name = "role",
        sql = "user_role",
        fromDb = { value ->
            val slug = enumDbValue(value)
            DatabaseUserRole.entries.firstOrNull { it.slug == slug }
                ?: error("Unknown user_role value: '$slug'")
        },
        toDb = { userRole ->
            pgEnum(
                type = "user_role",
                value = userRole.slug,
            )
        },
    )

    val status = customEnumeration(
        name = "status",
        sql = "account_status",
        fromDb = { value ->
            val slug = enumDbValue(value)
            ExposedAccountStatus.entries.firstOrNull { it.slug == slug }
                ?: error("Unknown account_status value: '$slug'")
        },
        toDb = { accountStatus ->
            pgEnum(
                type = "account_status",
                value = accountStatus.slug,
            )
        },
    )

    val avatarUrl = text(name = "avatar_url").nullable()
    val isActive = bool(name = "is_active")

    val createdAt = timestampWithTimeZone(name = "created_at")
    val updatedAt = timestampWithTimeZone(name = "updated_at")

    private fun enumDbValue(value: Any): String = (value as? PGobject)?.value ?: value.toString()

    private fun pgEnum(
        type: String,
        value: String,
    ) = PGobject().apply {
        this.type = type
        this.value = value
    }
}
