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
    throw DatabaseException.DependencyUnavailable(dependency = "postgres", cause = e)
} catch (e: SQLException) {
    throw DatabaseException.DependencyUnavailable(dependency = "postgres", cause = e)
}
