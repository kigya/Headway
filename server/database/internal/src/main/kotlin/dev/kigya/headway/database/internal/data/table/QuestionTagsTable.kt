package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

internal object QuestionTagsTable : Table(name = "public.question_tags") {
    val questionId = long(name = "question_id").references(
        ref = QuestionsTable.id,
        onDelete = ReferenceOption.CASCADE,
    )
    val tagId = long(name = "tag_id").references(
        ref = TagsTable.id,
        onDelete = ReferenceOption.CASCADE,
    )

    override val primaryKey = PrimaryKey(questionId, tagId)
}
