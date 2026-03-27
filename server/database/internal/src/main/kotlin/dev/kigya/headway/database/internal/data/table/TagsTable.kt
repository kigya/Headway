package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.Table

internal object TagsTable : Table(name = "public.tags") {
    val id = long(name = "id").autoIncrement()
    val key = text(name = "key").uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}
