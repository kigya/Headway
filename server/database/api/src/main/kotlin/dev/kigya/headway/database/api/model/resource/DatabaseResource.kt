package dev.kigya.headway.database.api.model.resource

import dev.kigya.headway.common.serialization.UUIDSerializer
import io.ktor.resources.Resource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@Resource("")
class DatabaseResource {

    @Serializable
    @Resource("user")
    data class User(val parent: DatabaseResource = DatabaseResource()) {

        @Serializable
        @Resource("")
        data class Query(
            val parent: User = User(),
            @SerialName("google_id")
            val googleId: String? = null,
            @SerialName("user_id")
            @Serializable(UUIDSerializer::class)
            val userId: UUID? = null,
        )

        @Serializable
        @Resource("invite")
        data class Invite(val parent: User = User())

        @Serializable
        @Resource("google")
        data class Google(val parent: User = User()) {

            @Serializable
            @Resource("upsert")
            data class Upsert(val parent: Google = Google())
        }
    }

    @Serializable
    @Resource("session")
    class Session {

        @Serializable
        @Resource("validate")
        data class Validate(
            val parent: Session = Session(),
        )
    }
}
