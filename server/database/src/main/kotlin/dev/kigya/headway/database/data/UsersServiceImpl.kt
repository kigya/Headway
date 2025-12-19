package dev.kigya.headway.database.data

import dev.kigya.headway.database.domain.model.ExposedUser
import dev.kigya.headway.database.domain.model.ExposedUserRole
import dev.kigya.headway.database.domain.model.UserAlreadyExistsException
import dev.kigya.headway.database.domain.service.UsersService
import dev.kigya.headway.database.ext.dbQuery
import dev.kigya.headway.database.ext.toUser
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import org.postgresql.util.PGobject
import java.util.UUID

internal class UsersServiceImpl(
    private val database: Database,
) : UsersService {

    object UsersTable : UUIDTable("public.users") {
        val googleId = varchar("google_id", 255).uniqueIndex().nullable()
        val email = varchar("email", 255).uniqueIndex()
        val name = varchar("name", 255)
        val role = customEnumeration(
            name = "role",
            fromDb = { value -> ExposedUserRole.entries.first { it.slug == value as String } },
            toDb = {
                PGobject().apply {
                    type = "user_role"
                    value = it.slug
                }
            }
        )
        val avatarUrl = varchar("avatar_url", 500).nullable()
        val isActive = bool("is_active")
        val createdAt = timestampWithTimeZone("created_at").defaultExpression(CurrentTimestampWithTimeZone)
        val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    }

    override suspend fun readById(id: UUID): ExposedUser? = database.dbQuery {
        UsersTable
            .select(UsersTable.columns)
            .where { UsersTable.id eq id }
            .map { it.toUser() }
            .singleOrNull()
    }

    override suspend fun readByGoogleId(googleId: String): ExposedUser? = database.dbQuery {
        UsersTable
            .select(UsersTable.columns)
            .where { UsersTable.googleId eq googleId }
            .map { it.toUser() }
            .singleOrNull()
    }

    override suspend fun readByEmail(email: String): ExposedUser? = database.dbQuery {
        UsersTable
            .select(UsersTable.columns)
            .where { UsersTable.email eq email }
            .map { it.toUser() }
            .singleOrNull()
    }

    override suspend fun insertGoogleUser(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
        role: ExposedUserRole
    ): ExposedUser = database.dbQuery {
        var existingUser = readByEmail(email)
        if (existingUser != null) {
            throw UserAlreadyExistsException("User with email $email already exists", existingUser)
        }
        existingUser = readByGoogleId(googleId)
        if (existingUser != null) {
            throw UserAlreadyExistsException("User with googleId $googleId already exists", existingUser)
        }

        val insertStatement = UsersTable.insert {
            it[this.googleId] = googleId
            it[this.email] = email
            it[this.name] = name
            it[this.avatarUrl] = avatarUrl
            it[this.role] = role
            it[this.isActive] = true
        }

        val newId = insertStatement[UsersTable.id]

        UsersTable
            .select(UsersTable.columns)
            .where { UsersTable.id eq newId }
            .single()
            .toUser()
    }

    override suspend fun updateGoogleUser(
        userId: UUID,
        name: String,
        avatarUrl: String?
    ): ExposedUser? = database.dbQuery {
        UsersTable.update({ UsersTable.id eq userId }) {
            it[this.name] = name
            if (avatarUrl != null) {
                it[this.avatarUrl] = avatarUrl
            }
            it[this.updatedAt] = CurrentDateTime
        }

        UsersTable
            .select(UsersTable.columns)
            .where { UsersTable.id eq userId }
            .map { it.toUser() }
            .singleOrNull()
    }
}
