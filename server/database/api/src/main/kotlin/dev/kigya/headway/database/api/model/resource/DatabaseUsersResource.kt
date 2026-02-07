package dev.kigya.headway.database.api.model.resource

import dev.kigya.headway.common.serialization.UUIDSerializer
import io.ktor.resources.Resource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Resource("/user")
data class DatabaseUsersResource(
    @SerialName("google_id")
    val googleId: String? = null,

    @SerialName("user_id")
    @Serializable(UUIDSerializer::class)
    val userId: UUID? = null,
) {
    @Serializable
    @Resource("invite")
    class Invite(val parent: DatabaseUsersResource = DatabaseUsersResource())

    @Serializable
    @Resource("google")
    class Google(val parent: DatabaseUsersResource = DatabaseUsersResource()) {
        @Serializable
        @Resource("upsert")
        class Upsert(val parent: Google = Google())
    }
}
