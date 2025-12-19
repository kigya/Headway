package dev.kigya.headway.database.ext

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

internal suspend fun <T> Database.dbQuery(block: suspend () -> T): T =
    suspendTransaction(this) { block() }
