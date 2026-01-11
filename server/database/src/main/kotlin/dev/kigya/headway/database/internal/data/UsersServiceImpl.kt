package dev.kigya.headway.database.internal.data

import dev.kigya.headway.database.api.error.UserAlreadyExistsException
import dev.kigya.headway.database.api.model.ExposedUser
import dev.kigya.headway.database.api.model.ExposedUserRole
import dev.kigya.headway.database.api.port.UsersServiceContract
import dev.kigya.headway.database.internal.ext.dbQuery
import dev.kigya.headway.database.internal.ext.toUser
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import org.postgresql.util.PGobject
import java.util.UUID

internal class UsersServiceImpl(
    private val database: Database,
) : UsersServiceContract {

    // Supabase source-of-truth: public.profiles
    object UsersTable : UUIDTable("public.profiles") {

        // В Supabase это FK на auth.users.id. Может быть nullable — оставляем nullable в коде.
        val authUserId = uuid("auth_user_id").nullable()

        // Supabase: email citext. Для JDBC это нормально как String.
        // text() проще, чем varchar() под citext.
        val email = text("email").uniqueIndex()

        // Supabase: full_name
        val fullName = text("full_name")

        // Supabase: role user_role (enum)
        val role = customEnumeration(
            name = "role",
            fromDb = { value ->
                val slug = when (value) {
                    is PGobject -> value.value
                    else -> value.toString()
                }
                ExposedUserRole.entries.first { it.slug == slug }
            },
            toDb = {
                PGobject().apply {
                    type = "user_role"
                    value = it.slug
                }
            }
        )

        val avatarUrl = text("avatar_url").nullable()
        val isActive = bool("is_active")

        // Supabase: timestamptz
        val createdAt = timestampWithTimeZone("created_at")
        val updatedAt = timestampWithTimeZone("updated_at")
    }

    private fun parseUuidOrNull(raw: String): UUID? =
        runCatching { UUID.fromString(raw) }.getOrNull()

    private fun readByIdTx(id: UUID): ExposedUser? =
        UsersTable
            .select(UsersTable.columns)
            .where { UsersTable.id eq id }
            .map { it.toUser() }
            .singleOrNull()

    private fun readByEmailTx(email: String): ExposedUser? =
        UsersTable
            .select(UsersTable.columns)
            .where { UsersTable.email eq email }
            .map { it.toUser() }
            .singleOrNull()

    private fun readByAuthUserIdTx(authUserId: UUID): ExposedUser? =
        UsersTable
            .select(UsersTable.columns)
            .where { UsersTable.authUserId eq authUserId }
            .map { it.toUser() }
            .singleOrNull()

    override suspend fun readById(id: UUID): ExposedUser? =
        database.dbQuery { readByIdTx(id) }

    /**
     * Исторически метод назывался readByGoogleId, но в Supabase у тебя auth_user_id.
     * Поэтому: если строка парсится как UUID -> ищем по auth_user_id, иначе возвращаем null.
     */
    override suspend fun readByGoogleId(googleId: String): ExposedUser? =
        database.dbQuery {
            val authUserId = parseUuidOrNull(googleId) ?: return@dbQuery null
            readByAuthUserIdTx(authUserId)
        }

    override suspend fun readByEmail(email: String): ExposedUser? =
        database.dbQuery { readByEmailTx(email) }

    /**
     * Upsert:
     * - ключом считаем email (как и было)
     * - если googleId выглядит как UUID -> пробуем трактовать как auth_user_id
     */
    override suspend fun upsertGoogleUser(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
    ): ExposedUser = database.dbQuery {

        val authUserId = parseUuidOrNull(googleId)

        val userByEmail = readByEmailTx(email)
        val userByAuth = authUserId?.let { readByAuthUserIdTx(it) }

        // auth_user_id должен быть уникальным: если найден другой профиль — конфликт
        if (userByAuth != null && userByEmail != null && userByAuth.id != userByEmail.id) {
            throw UserAlreadyExistsException(
                message = "User with authUserId=$authUserId already exists",
                user = userByAuth,
            )
        }

        if (userByEmail == null) {
            // Пытаемся создать профиль (если Supabase у тебя создает профиль триггером — это может упасть ограничениями)
            val insertStatement = UsersTable.insert {
                it[this.email] = email
                it[this.fullName] = name
                it[this.avatarUrl] = avatarUrl
                it[this.role] = ExposedUserRole.EMPLOYEE
                it[this.isActive] = true
                if (authUserId != null) it[this.authUserId] = authUserId
            }

            val newId = insertStatement[UsersTable.id]

            return@dbQuery UsersTable
                .select(UsersTable.columns)
                .where { UsersTable.id eq newId }
                .single()
                .toUser()
        }

        // update existing profile
        UsersTable.update({ UsersTable.id eq userByEmail.id }) {
            it[this.fullName] = name
            if (avatarUrl != null) it[this.avatarUrl] = avatarUrl
            if (authUserId != null && userByEmail.googleId == null) {
                // прикрепляем auth_user_id если раньше был пустой
                it[this.authUserId] = authUserId
            }
            // Supabase обычно сам обновляет updated_at триггером, но выставить явно тоже нормально
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

        val insertStatement = UsersTable.insert {
            it[this.email] = email
            it[this.fullName] = name
            it[this.avatarUrl] = avatarUrl
            it[this.role] = role
            it[this.isActive] = true
            if (authUserId != null) it[this.authUserId] = authUserId
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
