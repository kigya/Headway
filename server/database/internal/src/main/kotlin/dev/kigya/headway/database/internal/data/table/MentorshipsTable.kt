package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import java.time.OffsetDateTime

internal object MentorshipsTable : Table(name = "public.mentorships") {
    val mentorId = reference(
        name = "mentor_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    )
    val menteeId = reference(
        name = "mentee_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    )
    val createdBy = optReference(
        name = "created_by",
        foreign = UsersTable,
        onDelete = ReferenceOption.SET_NULL,
    )
    val createdAt = timestampWithTimeZone(name = "created_at")
        .clientDefault { OffsetDateTime.now() }
    val revokedAt = timestampWithTimeZone(name = "revoked_at").nullable()

    override val primaryKey = PrimaryKey(mentorId, menteeId)
}
