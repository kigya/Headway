package dev.kigya.headway.database.api.model.resource

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/session")
class DatabaseSessionResource {

    @Serializable
    @Resource("validate")
    class Validate(
        val parent: DatabaseSessionResource = DatabaseSessionResource(),
    )
}
