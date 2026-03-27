package dev.kigya.headway.database.api.model.resource

import dev.kigya.headway.common.serialization.UUIDSerializer
import dev.kigya.headway.database.api.model.out.DatabaseLearningSkillGroup
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
    data class User(@SerialName("parent") val parent: DatabaseResource = DatabaseResource()) {

        @Serializable
        @Resource("")
        data class Query(
            @SerialName("parent")
            val parent: User = User(),
            @SerialName("google_id")
            val googleId: String? = null,
            @SerialName("user_id")
            @Serializable(UUIDSerializer::class)
            val userId: UUID? = null,
        )

        @Serializable
        @Resource("invite")
        data class Invite(@SerialName("parent") val parent: User = User())

        @Serializable
        @Resource("google")
        data class Google(@SerialName("parent") val parent: User = User()) {

            @Serializable
            @Resource("upsert")
            data class Upsert(@SerialName("parent") val parent: Google = Google())
        }
    }

    @Serializable
    @Resource("session")
    class Session {

        @Serializable
        @Resource("validate")
        data class Validate(
            @SerialName("parent")
            val parent: Session = Session(),
        )
    }

    @Serializable
    @Resource("preparation")
    data class Preparation(@SerialName("parent") val parent: DatabaseResource = DatabaseResource()) {

        @Serializable
        @Resource("setup/employees")
        data class SetupEmployees(
            @SerialName("parent")
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
            @SerialName("parent")
            val parent: Preparation = Preparation(),
            @SerialName("subjectUserId")
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
            @SerialName("parent")
            val parent: Preparation = Preparation(),
            @SerialName("locale")
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
            @SerialName("parent")
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
                @SerialName("parent")
                val parent: Sessions,
                @SerialName("sessionId")
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
                    @SerialName("parent")
                    val parent: ById,
                )

                @Serializable
                @Resource("select-question")
                data class SelectQuestion(
                    @SerialName("parent")
                    val parent: ById,
                )

                @Serializable
                @Resource("finish")
                data class Finish(
                    @SerialName("parent")
                    val parent: ById,
                )

                @Serializable
                @Resource("summary")
                data class Summary(
                    @SerialName("parent")
                    val parent: ById,
                    @SerialName("locale")
                    val locale: String,
                )
            }
        }
    }

    @Serializable
    @Resource("learning-questions")
    data class LearningQuestions(@SerialName("parent") val parent: DatabaseResource = DatabaseResource()) {

        @Serializable
        @Resource("public/catalog")
        data class PublicCatalog(
            @SerialName("parent")
            val parent: LearningQuestions = LearningQuestions(),
            @SerialName("locale")
            val locale: String,
        )

        @Serializable
        @Resource("public/page")
        data class PublicPage(
            @SerialName("parent")
            val parent: LearningQuestions = LearningQuestions(),
            @SerialName("locale")
            val locale: String,
            @SerialName("skill_group")
            val skillGroup: DatabaseLearningSkillGroup,
            @SerialName("limit")
            val limit: Int,
            @SerialName("after_question_id")
            val afterQuestionId: Long? = null,
        )

        @Serializable
        @Resource("public/search")
        data class PublicSearch(
            @SerialName("parent")
            val parent: LearningQuestions = LearningQuestions(),
            @SerialName("locale")
            val locale: String,
            @SerialName("q")
            val q: String,
        )

        @Serializable
        @Resource("public/{questionId}")
        data class PublicById(
            @SerialName("parent")
            val parent: LearningQuestions = LearningQuestions(),
            @SerialName("questionId")
            val questionId: Long,
            @SerialName("locale")
            val locale: String,
        )

        @Serializable
        @Resource("catalog")
        data class Catalog(
            @SerialName("parent")
            val parent: LearningQuestions = LearningQuestions(),
            @SerialName("locale")
            val locale: String,
            @SerialName("facilitator_user_id")
            @Serializable(UUIDSerializer::class)
            val facilitatorUserId: UUID,
            @SerialName("facilitator_role")
            val facilitatorRole: DatabaseUserRole,
            @SerialName("subject_user_id")
            @Serializable(UUIDSerializer::class)
            val subjectUserId: UUID?,
        )

        @Serializable
        @Resource("page")
        data class Page(
            @SerialName("parent")
            val parent: LearningQuestions = LearningQuestions(),
            @SerialName("locale")
            val locale: String,
            @SerialName("skill_group")
            val skillGroup: DatabaseLearningSkillGroup,
            @SerialName("limit")
            val limit: Int,
            @SerialName("after_question_id")
            val afterQuestionId: Long? = null,
            @SerialName("facilitator_user_id")
            @Serializable(UUIDSerializer::class)
            val facilitatorUserId: UUID,
            @SerialName("facilitator_role")
            val facilitatorRole: DatabaseUserRole,
            @SerialName("subject_user_id")
            @Serializable(UUIDSerializer::class)
            val subjectUserId: UUID?,
        )

        @Serializable
        @Resource("search")
        data class Search(
            @SerialName("parent")
            val parent: LearningQuestions = LearningQuestions(),
            @SerialName("locale")
            val locale: String,
            @SerialName("q")
            val q: String,
            @SerialName("facilitator_user_id")
            @Serializable(UUIDSerializer::class)
            val facilitatorUserId: UUID,
            @SerialName("facilitator_role")
            val facilitatorRole: DatabaseUserRole,
            @SerialName("subject_user_id")
            @Serializable(UUIDSerializer::class)
            val subjectUserId: UUID?,
        )

        @Serializable
        @Resource("{questionId}")
        data class ById(
            @SerialName("parent")
            val parent: LearningQuestions = LearningQuestions(),
            @SerialName("questionId")
            val questionId: Long,
            @SerialName("locale")
            val locale: String,
            @SerialName("facilitator_user_id")
            @Serializable(UUIDSerializer::class)
            val facilitatorUserId: UUID,
            @SerialName("facilitator_role")
            val facilitatorRole: DatabaseUserRole,
            @SerialName("subject_user_id")
            @Serializable(UUIDSerializer::class)
            val subjectUserId: UUID?,
        )

        @Serializable
        @Resource("{questionId}/remarks")
        data class Remarks(
            @SerialName("parent")
            val parent: LearningQuestions = LearningQuestions(),
            @SerialName("questionId")
            val questionId: Long,
            @SerialName("subject_user_id")
            @Serializable(UUIDSerializer::class)
            val subjectUserId: UUID,
            @SerialName("facilitator_user_id")
            @Serializable(UUIDSerializer::class)
            val facilitatorUserId: UUID,
            @SerialName("facilitator_role")
            val facilitatorRole: DatabaseUserRole,
        )
    }

    @Serializable
    @Resource("guest-sessions")
    data class GuestSessions(@SerialName("parent") val parent: DatabaseResource = DatabaseResource()) {

        @Serializable
        @Resource("")
        data class Register(@SerialName("parent") val parent: GuestSessions = GuestSessions())

        @Serializable
        @Resource("{sessionId}/revoke")
        data class Revoke(
            @SerialName("parent")
            val parent: GuestSessions = GuestSessions(),
            @SerialName("sessionId")
            @Serializable(UUIDSerializer::class)
            val sessionId: UUID,
        )

        @Serializable
        @Resource("{sessionId}/validate")
        data class Validate(
            @SerialName("parent")
            val parent: GuestSessions = GuestSessions(),
            @SerialName("sessionId")
            @Serializable(UUIDSerializer::class)
            val sessionId: UUID,
        )
    }
}
