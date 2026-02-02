package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import java.time.OffsetDateTime

internal object RefreshSessionsTable : UUIDTable("public.refresh_sessions") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val refreshTokenHash = varchar("refresh_token_hash", 255).index()
    val fingerprint = varchar("fingerprint", 255)
    val expiresIn = timestampWithTimeZone("expires_in")
    val createdAt = timestampWithTimeZone("created_at").clientDefault { OffsetDateTime.now() }
}
