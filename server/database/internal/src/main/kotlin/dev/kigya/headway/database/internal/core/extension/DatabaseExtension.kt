package dev.kigya.headway.database.internal.core.extension

import dev.kigya.headway.database.internal.domain.error.DatabaseException
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import java.sql.SQLException

internal suspend fun <T> Database.dbQuery(block: suspend () -> T): T = try {
    suspendTransaction(this) { block() }
} catch (e: DatabaseException) {
    throw e
} catch (e: ExposedSQLException) {
    throw e.toDatabaseException()
} catch (e: SQLException) {
    throw e.toDatabaseException()
}

internal fun SQLException.toDatabaseException(): DatabaseException {
    val normalizedMessage = message.orEmpty().normalizeSqlMessage()
    return if (normalizedMessage.isInviteDomainViolation()) {
        DatabaseException.InvalidRequest(
            message = normalizedMessage,
            cause = this,
        )
    } else {
        DatabaseException.DependencyUnavailable(
            dependency = POSTGRES_DEPENDENCY,
            cause = this,
        )
    }
}

private fun ExposedSQLException.toDatabaseException(): DatabaseException =
    (cause as? SQLException)?.toDatabaseException()
        ?: DatabaseException.DependencyUnavailable(
            dependency = POSTGRES_DEPENDENCY,
            cause = this,
        )

private fun String.normalizeSqlMessage(): String = lineSequence()
    .firstOrNull()
    .orEmpty()
    .removePrefix(ERROR_PREFIX)
    .trim()

private fun String.isInviteDomainViolation(): Boolean =
    contains(INVITE_DOMAIN_SEGMENT) && endsWith(INVITE_DOMAIN_SUFFIX)

private const val POSTGRES_DEPENDENCY = "postgres"
private const val ERROR_PREFIX = "ERROR:"
private const val INVITE_DOMAIN_SEGMENT = "Email domain \""
private const val INVITE_DOMAIN_SUFFIX = "\" is not allowed"
