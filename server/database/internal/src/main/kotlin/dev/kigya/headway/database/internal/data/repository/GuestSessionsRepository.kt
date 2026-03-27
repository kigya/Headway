package dev.kigya.headway.database.internal.data.repository

import dev.kigya.headway.database.internal.core.extension.dbQuery
import dev.kigya.headway.database.internal.data.table.GuestSessionTable
import dev.kigya.headway.database.internal.domain.error.DatabaseException
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.util.UUID

internal class GuestSessionsRepository(
    private val database: Database,
) {

    suspend fun register(sessionId: UUID) = database.dbQuery {
        val exists = GuestSessionTable.selectAll()
            .where { GuestSessionTable.id eq sessionId }
            .firstOrNull() != null
        if (!exists) {
            GuestSessionTable.insert {
                it[id] = sessionId
                it[createdAt] = CurrentTimestampWithTimeZone
                it[revokedAt] = null
            }
        }
    }

    suspend fun revoke(sessionId: UUID) = database.dbQuery {
        val updated = GuestSessionTable.update({ GuestSessionTable.id eq sessionId }) {
            it[revokedAt] = CurrentTimestampWithTimeZone
        }
        if (updated == 0) {
            throw DatabaseException.NotFound("Guest session not found")
        }
    }

    suspend fun ensureActive(sessionId: UUID) = database.dbQuery {
        val row = GuestSessionTable.selectAll()
            .where { GuestSessionTable.id eq sessionId }
            .firstOrNull()
            ?: throw DatabaseException.NotFound("Guest session not found")
        if (row[GuestSessionTable.revokedAt] != null) {
            throw DatabaseException.Forbidden(message = "Guest session revoked")
        }
    }
}
