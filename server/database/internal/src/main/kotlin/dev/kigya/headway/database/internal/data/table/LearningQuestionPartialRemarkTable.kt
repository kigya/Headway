package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import java.time.OffsetDateTime

internal object LearningQuestionPartialRemarkTable : UUIDTable(name = "public.learning_question_partial_remark") {
    val subjectUserId = reference(
        name = "subject_user_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    )
    val questionId = long(name = "question_id").references(
        ref = QuestionsTable.id,
        onDelete = ReferenceOption.CASCADE,
    )
    val authorUserId = reference(
        name = "author_user_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    )
    val body = text(name = "body")
    val createdAt = timestampWithTimeZone(name = "created_at")
        .clientDefault { OffsetDateTime.now() }
}
