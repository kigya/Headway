package dev.kigya.headway.database.internal.data.repository

import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.internal.core.extension.dbQuery
import dev.kigya.headway.database.internal.data.model.ExposedAccountStatus
import dev.kigya.headway.database.internal.data.table.UsersTable
import dev.kigya.headway.database.internal.domain.error.DatabaseException
import dev.kigya.headway.database.internal.domain.repository.UsersRepositoryContract
import dev.kigya.headway.database.internal.mapping.toExposedDepartment
import dev.kigya.headway.database.internal.mapping.toUser
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import java.util.UUID

internal class UsersRepository(
    private val database: Database,
) : UsersRepositoryContract {

    override suspend fun readById(id: UUID): DatabaseUser? = database.dbQuery { readByIdTx(id) }

    override suspend fun readByGoogleId(googleId: String): DatabaseUser? = database.dbQuery {
        val authUserId = parseUuidOrNull(googleId) ?: return@dbQuery null
        readByAuthUserIdTx(authUserId)
    }

    override suspend fun readByEmail(email: String): DatabaseUser? =
        database.dbQuery { readByEmailTx(email) }

    override suspend fun inviteUser(
        email: String,
        department: String,
        role: DatabaseUserRole?,
    ): DatabaseUser = database.dbQuery {
        val existingRow = readRowByEmailTx(email)
        val targetRole = role ?: DatabaseUserRole.EMPLOYEE

        if (existingRow != null) {
            val currentId = existingRow[UsersTable.id].value
            val currentStatus = existingRow[UsersTable.status]

            UsersTable.update({ UsersTable.id eq currentId }) {
                it[this.department] = department.toExposedDepartment()
                it[this.updatedAt] = CurrentTimestampWithTimeZone

                when (currentStatus) {
                    ExposedAccountStatus.INVITED,
                    ExposedAccountStatus.REVOKED,
                    -> {
                        it[this.role] = targetRole
                        it[this.status] = ExposedAccountStatus.INVITED
                        it[this.isActive] = false
                    }

                    ExposedAccountStatus.ACTIVE -> {
                        // keep ACTIVE as-is
                    }
                }
            }

            return@dbQuery readByIdTx(currentId)!!
        }

        val newId = UsersTable.insert {
            it[this.email] = email
            it[this.fullName] = email
            it[this.department] = department.toExposedDepartment()
            it[this.role] = targetRole
            it[this.status] = ExposedAccountStatus.INVITED
            it[this.isActive] = false
        }[UsersTable.id].value

        readByIdTx(newId)!!
    }

    override suspend fun upsertGoogleUser(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
    ): DatabaseUser = database.dbQuery {
        val authUserId = parseUuidOrNull(googleId)

        val rowByEmail = readRowByEmailTx(email) ?: throw DatabaseException.UserNotInvited()
        val userByEmail = rowByEmail.toUser()
        val statusByEmail = rowByEmail[UsersTable.status]
        if (statusByEmail == ExposedAccountStatus.REVOKED) throw DatabaseException.UserNotInvited()

        val userByAuth = authUserId?.let { readByAuthUserIdTx(it) }

        if (userByAuth != null && userByAuth.id != userByEmail.id) {
            throw DatabaseException.UserAlreadyExists(
                message = "User with authUserId=$authUserId already exists",
                user = userByAuth,
            )
        }

        val existingAuthUserId = rowByEmail[UsersTable.authUserId]
        if (existingAuthUserId != null && authUserId != null && existingAuthUserId != authUserId) {
            throw DatabaseException.UserAlreadyExists(
                message = "User with email=$email already linked to a different authUserId",
                user = userByEmail,
            )
        }

        UsersTable.update({ UsersTable.id eq userByEmail.id }) {
            it[this.fullName] = name
            if (avatarUrl != null) it[this.avatarUrl] = avatarUrl
            if (authUserId != null && existingAuthUserId == null) {
                it[this.authUserId] = authUserId
            }
            if (statusByEmail == ExposedAccountStatus.INVITED) {
                it[this.status] = ExposedAccountStatus.ACTIVE
                it[this.isActive] = true
            }
            it[this.updatedAt] = CurrentTimestampWithTimeZone
        }

        UsersTable
            .select(UsersTable.columns)
            .where { UsersTable.id eq userByEmail.id }
            .single()
            .toUser()
    }

    override suspend fun insertGoogleUser(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
        role: DatabaseUserRole,
    ): DatabaseUser = database.dbQuery {
        val authUserId = parseUuidOrNull(googleId)

        val existingByEmail = readByEmailTx(email)
        if (existingByEmail != null) {
            throw DatabaseException.UserAlreadyExists(
                user = existingByEmail,
                message = "User with email $email already exists",
            )
        }

        val existingByAuth = authUserId?.let { readByAuthUserIdTx(it) }
        if (existingByAuth != null) {
            throw DatabaseException.UserAlreadyExists(
                user = existingByAuth,
                message = "User with authUserId=$authUserId already exists",
            )
        }

        val newId = UsersTable.insert {
            it[this.email] = email
            it[this.fullName] = name
            it[this.avatarUrl] = avatarUrl
            it[this.role] = role
            it[this.status] = ExposedAccountStatus.ACTIVE
            it[this.isActive] = true
            if (authUserId != null) it[this.authUserId] = authUserId
        }[UsersTable.id].value

        UsersTable
            .select(UsersTable.columns)
            .where { UsersTable.id eq newId }
            .single()
            .toUser()
    }

    override suspend fun updateGoogleUser(
        userId: UUID,
        name: String,
        avatarUrl: String?,
    ): DatabaseUser? = database.dbQuery {
        UsersTable.update({ UsersTable.id eq userId }) {
            it[this.fullName] = name
            if (avatarUrl != null) it[this.avatarUrl] = avatarUrl
            it[this.updatedAt] = CurrentTimestampWithTimeZone
        }

        UsersTable
            .select(UsersTable.columns)
            .where { UsersTable.id eq userId }
            .map { it.toUser() }
            .singleOrNull()
    }

    private fun parseUuidOrNull(raw: String): UUID? =
        runCatching { UUID.fromString(raw) }.getOrNull()

    private fun readRowByIdTx(id: UUID): ResultRow? = UsersTable
        .select(UsersTable.columns)
        .where { UsersTable.id eq id }
        .singleOrNull()

    private fun readByIdTx(id: UUID): DatabaseUser? = readRowByIdTx(id)?.toUser()

    private fun readRowByEmailTx(email: String): ResultRow? = UsersTable
        .select(UsersTable.columns)
        .where { UsersTable.email eq email }
        .singleOrNull()

    private fun readByEmailTx(email: String): DatabaseUser? = readRowByEmailTx(email)?.toUser()

    private fun readByAuthUserIdTx(authUserId: UUID): DatabaseUser? = UsersTable
        .select(UsersTable.columns)
        .where { UsersTable.authUserId eq authUserId }
        .map { it.toUser() }
        .singleOrNull()
}
