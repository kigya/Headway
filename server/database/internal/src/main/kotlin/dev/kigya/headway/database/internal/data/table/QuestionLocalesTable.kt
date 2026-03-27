package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import org.postgresql.util.PGobject
import java.time.OffsetDateTime

internal enum class QuestionLocaleCode {
    RU,
    EN,
}

internal object QuestionLocalesTable : Table(name = "public.question_locales") {
    val questionId = long(name = "question_id").references(
        ref = QuestionsTable.id,
        onDelete = ReferenceOption.CASCADE,
    )
    val locale = customEnumeration(
        name = "locale",
        sql = "locale_code",
        fromDb = { raw -> QuestionLocaleCode.valueOf(pgEnumRaw(raw)) },
        toDb = { v ->
            PGobject().apply {
                type = "locale_code"
                value = v.name
            }
        },
    )
    val questionText = text(name = "question_text")
    val aiAnswer = text(name = "ai_answer").nullable()
    val updatedAt = timestampWithTimeZone(name = "updated_at")
        .clientDefault { OffsetDateTime.now() }

    override val primaryKey = PrimaryKey(questionId, locale)
}
