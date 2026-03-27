package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable

internal object SessionQuestionTable : UUIDTable(name = "public.session_question") {
    val sessionId = reference(
        name = "session_id",
        foreign = PreparationSessionTable,
        onDelete = ReferenceOption.CASCADE,
    )
    val sortOrder = integer(name = "sort_order")
    val sourceQuestionId = long(name = "source_question_id").nullable()
    val topicTagsJson = text(name = "topic_tags")
    val domain = text(name = "domain").nullable()
    val titleOrPrompt = text(name = "title_or_prompt")
    val body = text(name = "body").nullable()

    init {
        uniqueIndex(sessionId, sortOrder)
    }
}
