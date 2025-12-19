package dev.kigya.headway.ext

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import java.util.UUID

internal fun SchemaBuilder.stringScalarUUID() {
    stringScalar<UUID> {
        name = "UUID"
        description = "Unique identifier (UUID)"
        serialize = (UUID::toString)
        deserialize = (UUID::fromString)
    }
}

internal fun SchemaBuilder.stringScalarLong() {
    stringScalar<Long> {
        name = "Long"
        description = "64-bit integer (serialized as String)"
        serialize = (Long::toString)

        deserialize = { string -> string.toLong() }
    }
}
