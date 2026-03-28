package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import org.postgresql.util.PGobject
import java.time.OffsetDateTime

internal object LearningQuestionProgressTable : Table(name = "public.learning_question_progress") {
    val subjectUserId = reference(
        name = "subject_user_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    )
    val questionId = long(name = "question_id").references(
        ref = QuestionsTable.id,
        onDelete = ReferenceOption.CASCADE,
    )
    val state = customEnumeration(
        name = "state",
        sql = "learning_question_progress_state",
        fromDb = { raw -> LearningQuestionProgressState.valueOf(pgEnumRaw(raw)) },
        toDb = { v ->
            PGobject().apply {
                type = "learning_question_progress_state"
                value = v.name
            }
        },
    )
    val updatedAt = timestampWithTimeZone(name = "updated_at")
        .clientDefault { OffsetDateTime.now() }

    override val primaryKey = PrimaryKey(subjectUserId, questionId)
}
