package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
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
}
