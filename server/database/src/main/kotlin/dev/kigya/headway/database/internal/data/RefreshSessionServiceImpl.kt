package dev.kigya.headway.database.internal.data

import dev.kigya.headway.database.api.error.SessionDoesNotExistsException
import dev.kigya.headway.database.api.error.SessionValidationException
import dev.kigya.headway.database.api.port.RefreshSessionsServiceContract
import dev.kigya.headway.database.internal.ext.dbQuery
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import java.security.MessageDigest
import java.time.OffsetDateTime
import java.util.UUID

internal class RefreshSessionServiceImpl(
    private val database: Database,
) : RefreshSessionsServiceContract {

    object RefreshSessionsTable : UUIDTable("public.refresh_sessions") {
        val userId = reference("user_id", UsersServiceImpl.UsersTable, onDelete = ReferenceOption.CASCADE)

        val refreshTokenHash = varchar("refresh_token_hash", 255).index()
        val fingerprint = varchar("fingerprint", 255)

        val expiresIn = timestampWithTimeZone("expires_in")
        val createdAt = timestampWithTimeZone("created_at").clientDefault { OffsetDateTime.now() }
    }

    override suspend fun createSession(
        userId: UUID,
        refreshToken: String,
        expiresIn: OffsetDateTime,
        fingerprint: String,
    ) {
        database.dbQuery {
            val alreadyExistsSessionId = RefreshSessionsTable
                .select(RefreshSessionsTable.id)
                .where { RefreshSessionsTable.fingerprint eq fingerprint }
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

        if (row == null) throw SessionDoesNotExistsException()

        if (row[RefreshSessionsTable.fingerprint] != fingerprint) {
            // Session compromised and need to be removed
            RefreshSessionsTable
                .deleteWhere { RefreshSessionsTable.id eq row[RefreshSessionsTable.id].value }

            throw SessionValidationException("Invalid fingerprint")
        }
        if (row[RefreshSessionsTable.expiresIn] < OffsetDateTime.now()) {
            // Session expired and need to be removed
            RefreshSessionsTable
                .deleteWhere { RefreshSessionsTable.id eq row[RefreshSessionsTable.id].value }
            throw SessionValidationException("Session expired")
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
