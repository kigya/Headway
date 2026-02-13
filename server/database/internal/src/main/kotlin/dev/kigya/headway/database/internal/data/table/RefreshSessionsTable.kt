package dev.kigya.headway.database.internal.data.table

import dev.kigya.headway.database.api.model.`in`.DatabaseSessionPlatform
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import org.postgresql.util.PGobject
import java.time.OffsetDateTime

internal object RefreshSessionsTable : UUIDTable("public.refresh_sessions") {
    val userId = reference(
        name = "user_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    )
    val refreshTokenHash = varchar(
        name = "refresh_token_hash",
        length = 255,
    ).index()
    val fingerprint = varchar(
        name = "fingerprint",
        length = 255,
    )
    val expiresIn = timestampWithTimeZone(name = "expires_in")
    val createdAt = timestampWithTimeZone(name = "created_at")
        .clientDefault { OffsetDateTime.now() }

    val platform = customEnumeration(
        name = "platform",
        sql = "platform_type",
        fromDb = { value ->
            val raw = (value as? PGobject)?.value ?: value.toString()
            DatabaseSessionPlatform.entries.firstOrNull { it.name == raw }
                ?: error("Unknown session_platform value: '$raw'")
        },
        toDb = { p ->
            PGobject().apply { type = "platform_type"; this.value = p.name }
        }
    )

}
