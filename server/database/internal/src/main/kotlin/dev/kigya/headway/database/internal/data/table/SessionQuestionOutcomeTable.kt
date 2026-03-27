package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import java.time.OffsetDateTime

internal object SessionQuestionOutcomeTable : UUIDTable(name = "public.session_question_outcome") {
    val sessionId = reference(
        name = "session_id",
        foreign = PreparationSessionTable,
        onDelete = ReferenceOption.CASCADE,
    )
    val sessionQuestionId = reference(
        name = "session_question_id",
        foreign = SessionQuestionTable,
        onDelete = ReferenceOption.CASCADE,
    )
    val outcome = text(name = "outcome")
    val comment = text(name = "comment").nullable()
    val recordedAt = timestampWithTimeZone(name = "recorded_at")
        .clientDefault { OffsetDateTime.now() }

    init {
        uniqueIndex(sessionId, sessionQuestionId)
    }
}
