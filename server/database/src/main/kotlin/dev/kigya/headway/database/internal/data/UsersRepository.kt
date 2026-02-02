package dev.kigya.headway.database.internal.data

import dev.kigya.headway.database.api.error.UserAlreadyExistsException
import dev.kigya.headway.database.api.error.UserNotInvitedException
import dev.kigya.headway.database.api.model.ExposedAccountStatus
import dev.kigya.headway.database.api.model.ExposedUser
import dev.kigya.headway.database.api.model.ExposedUserRole
import dev.kigya.headway.database.internal.data.table.UsersTable
import dev.kigya.headway.database.internal.extension.dbQuery
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

    private fun parseUuidOrNull(raw: String): UUID? =
        runCatching { UUID.fromString(raw) }.getOrNull()

    private fun readRowByIdTx(id: UUID): ResultRow? =
        UsersTable
            .select(UsersTable.columns)
            .where { UsersTable.id eq id }
            .singleOrNull()

    private fun readByIdTx(id: UUID): ExposedUser? =
        readRowByIdTx(id)?.toUser()

    private fun readRowByEmailTx(email: String): ResultRow? =
        UsersTable
            .select(UsersTable.columns)
            .where { UsersTable.email eq email }
            .singleOrNull()

    private fun readByEmailTx(email: String): ExposedUser? =
        readRowByEmailTx(email)?.toUser()

    private fun readByAuthUserIdTx(authUserId: UUID): ExposedUser? =
        UsersTable
            .select(UsersTable.columns)
            .where { UsersTable.authUserId eq authUserId }
            .map { it.toUser() }
            .singleOrNull()

    override suspend fun readById(id: UUID): ExposedUser? =
        database.dbQuery { readByIdTx(id) }

    override suspend fun readByGoogleId(googleId: String): ExposedUser? =
        database.dbQuery {
            val authUserId = parseUuidOrNull(googleId) ?: return@dbQuery null
            readByAuthUserIdTx(authUserId)
        }

    override suspend fun readByEmail(email: String): ExposedUser? =
        database.dbQuery { readByEmailTx(email) }

    override suspend fun inviteUser(
        email: String,
        department: String,
    ): ExposedUser = database.dbQuery {
        val existingRow = readRowByEmailTx(email)

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
            it[this.role] = ExposedUserRole.EMPLOYEE
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
    ): ExposedUser = database.dbQuery {

        val authUserId = parseUuidOrNull(googleId)

        val rowByEmail = readRowByEmailTx(email) ?: throw UserNotInvitedException()
        val userByEmail = rowByEmail.toUser()
        val statusByEmail = rowByEmail[UsersTable.status]
        if (statusByEmail == ExposedAccountStatus.REVOKED) throw UserNotInvitedException()

        val userByAuth = authUserId?.let { readByAuthUserIdTx(it) }

        if (userByAuth != null && userByAuth.id != userByEmail.id) {
            throw UserAlreadyExistsException(
                message = "User with authUserId=$authUserId already exists",
                user = userByAuth,
            )
        }

        val existingAuthUserId = rowByEmail[UsersTable.authUserId]
        if (existingAuthUserId != null && authUserId != null && existingAuthUserId != authUserId) {
            throw UserAlreadyExistsException(
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
        role: ExposedUserRole,
    ): ExposedUser = database.dbQuery {

        val authUserId = parseUuidOrNull(googleId)

        val existingByEmail = readByEmailTx(email)
        if (existingByEmail != null) {
            throw UserAlreadyExistsException("User with email $email already exists", existingByEmail)
        }

        val existingByAuth = authUserId?.let { readByAuthUserIdTx(it) }
        if (existingByAuth != null) {
            throw UserAlreadyExistsException("User with authUserId=$authUserId already exists", existingByAuth)
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
    ): ExposedUser? = database.dbQuery {

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
}
