package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import java.time.OffsetDateTime

internal object GuestSessionTable : UUIDTable(name = "public.guest_session") {
    val createdAt = timestampWithTimeZone(name = "created_at")
        .clientDefault { OffsetDateTime.now() }
    val revokedAt = timestampWithTimeZone(name = "revoked_at").nullable()
}
