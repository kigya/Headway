package dev.kigya.headway.database.api.model.resource

import dev.kigya.headway.common.serialization.UUIDSerializer
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
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

    @Serializable
    @Resource("preparation")
    data class Preparation(val parent: DatabaseResource = DatabaseResource()) {

        @Serializable
        @Resource("setup/employees")
        data class SetupEmployees(
            val parent: Preparation = Preparation(),
            @SerialName("facilitator_user_id")
            @Serializable(UUIDSerializer::class)
            val facilitatorUserId: UUID,
            @SerialName("facilitator_role")
            val facilitatorRole: DatabaseUserRole,
        )

        @Serializable
        @Resource("employees/{subjectUserId}/readiness")
        data class EmployeeReadiness(
            val parent: Preparation = Preparation(),
            @Serializable(UUIDSerializer::class)
            val subjectUserId: UUID,
            @SerialName("facilitator_user_id")
            @Serializable(UUIDSerializer::class)
            val facilitatorUserId: UUID,
            @SerialName("facilitator_role")
            val facilitatorRole: DatabaseUserRole,
        )

        @Serializable
        @Resource("catalog")
        data class Catalog(
            val parent: Preparation = Preparation(),
            val locale: String,
            @SerialName("facilitator_user_id")
            @Serializable(UUIDSerializer::class)
            val facilitatorUserId: UUID,
            @SerialName("facilitator_role")
            val facilitatorRole: DatabaseUserRole,
        )

        @Serializable
        @Resource("sessions")
        data class Sessions(
            val parent: Preparation = Preparation(),
            @SerialName("facilitator_user_id")
            @Serializable(UUIDSerializer::class)
            val facilitatorUserId: UUID,
            @SerialName("facilitator_role")
            val facilitatorRole: DatabaseUserRole,
        ) {

            @Serializable
            @Resource("{sessionId}")
            data class ById(
                val parent: Sessions,
                @Serializable(UUIDSerializer::class)
                val sessionId: UUID,
                @SerialName("facilitator_user_id")
                @Serializable(UUIDSerializer::class)
                val facilitatorUserId: UUID,
                @SerialName("facilitator_role")
                val facilitatorRole: DatabaseUserRole,
            ) {

                @Serializable
                @Resource("outcomes")
                data class Outcomes(
                    val parent: ById,
                )

                @Serializable
                @Resource("select-question")
                data class SelectQuestion(
                    val parent: ById,
                )

                @Serializable
                @Resource("finish")
                data class Finish(
                    val parent: ById,
                )

                @Serializable
                @Resource("summary")
                data class Summary(
                    val parent: ById,
                    val locale: String,
                )
            }
        }
    }
}
