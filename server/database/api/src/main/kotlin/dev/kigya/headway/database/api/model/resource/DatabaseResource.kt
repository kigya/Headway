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
    class User(val parent: DatabaseResource = DatabaseResource()) {

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
        class Invite(val parent: User = User())

        @Serializable
        @Resource("google")
        class Google(val parent: User = User()) {

            @Serializable
            @Resource("upsert")
            class Upsert(val parent: Google = Google())
        }
    }

    @Serializable
    @Resource("session")
    class Session {

        @Serializable
        @Resource("validate")
        class Validate(
            val parent: Session = Session(),
        )
    }
}

