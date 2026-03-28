package dev.kigya.headway.database.api.model.`in`

import dev.kigya.headway.database.api.model.out.DatabaseLearningSkillGroup
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import java.util.UUID

data class DatabaseLearningFacilitatedPageQuery(
    val facilitatorUserId: UUID,
    val facilitatorRole: DatabaseUserRole,
    val locale: String,
    val skillGroup: DatabaseLearningSkillGroup,
    val limit: Int,
    val afterQuestionId: Long?,
    val subjectUserId: UUID?,
)
