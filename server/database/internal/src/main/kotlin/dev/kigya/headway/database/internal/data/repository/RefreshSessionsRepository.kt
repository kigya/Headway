package dev.kigya.headway.database.internal.data.repository

import dev.kigya.headway.database.internal.domain.repository.RefreshSessionsRepositoryContract
import dev.kigya.headway.database.internal.data.table.RefreshSessionsTable
import dev.kigya.headway.database.internal.core.extension.dbQuery
import dev.kigya.headway.database.internal.domain.error.DatabaseException
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import java.security.MessageDigest
import java.time.OffsetDateTime
import java.util.UUID

internal class RefreshSessionsRepository(
    private val database: Database,
) : RefreshSessionsRepositoryContract {

    override suspend fun createSession(
        userId: UUID,
        refreshToken: String,
        expiresIn: OffsetDateTime,
        fingerprint: String,
    ) {
        database.dbQuery {
            val alreadyExistsSessionId = RefreshSessionsTable
                .select(RefreshSessionsTable.id)
                .where {
                    (RefreshSessionsTable.fingerprint eq fingerprint) and (RefreshSessionsTable.userId eq userId)
                }
                .map { it[RefreshSessionsTable.id].value }
                .firstOrNull()

            if (alreadyExistsSessionId != null) {
                RefreshSessionsTable
                    .update(
                        where = { RefreshSessionsTable.id eq alreadyExistsSessionId },
                        body = {
                            it[this.refreshTokenHash] = hashToken(refreshToken)
                            it[this.expiresIn] = expiresIn
                            it[this.createdAt] = OffsetDateTime.now()
                        }
                    )
            } else {
                RefreshSessionsTable.insert {
                    it[this.userId] = userId
                    it[this.refreshTokenHash] = hashToken(refreshToken)
                    it[this.fingerprint] = fingerprint
                    it[this.expiresIn] = expiresIn
                }
            }
        }
    }

    override suspend fun verifySession(
        rawRefreshToken: String,
        fingerprint: String,
    ): UUID = database.dbQuery {
        val hash = hashToken(rawRefreshToken)

        val row = RefreshSessionsTable
            .select(RefreshSessionsTable.columns)
            .where {
                (RefreshSessionsTable.refreshTokenHash eq hash) and
                        (RefreshSessionsTable.expiresIn greater OffsetDateTime.now())
            }.singleOrNull()

        if (row == null) {
            throw DatabaseException.Unauthorized("Session does not exist or expired")
        }

        if (row[RefreshSessionsTable.fingerprint] != fingerprint) {
            RefreshSessionsTable
                .deleteWhere { RefreshSessionsTable.id eq row[RefreshSessionsTable.id].value }

            throw DatabaseException.Unauthorized("Invalid fingerprint. Token is possibly stolen")
        }

        row[RefreshSessionsTable.userId].value
    }

    override suspend fun deleteAllUserSessions(userId: UUID) {
        RefreshSessionsTable.deleteWhere { RefreshSessionsTable.userId eq userId }
    }

    private fun hashToken(token: String): String {
        val bytes = token.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
