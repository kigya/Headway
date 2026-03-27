package dev.kigya.headway.database.internal.data.scope

import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.internal.data.model.ExposedAccountStatus
import dev.kigya.headway.database.internal.data.table.MentorshipsTable
import dev.kigya.headway.database.internal.data.table.UsersTable
import dev.kigya.headway.database.internal.domain.error.DatabaseException
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.UUID

internal fun isSubjectInFacilitatorLearningScope(
    facilitatorId: UUID,
    facilitatorRole: DatabaseUserRole,
    subjectUserId: UUID,
): Boolean {
    val baseFilter: Op<Boolean> =
        (UsersTable.id eq subjectUserId) and
            (UsersTable.isActive eq true) and
            (UsersTable.role eq DatabaseUserRole.EMPLOYEE) and
            (UsersTable.status eq ExposedAccountStatus.ACTIVE)

    return when (facilitatorRole) {
        DatabaseUserRole.EMPLOYEE ->
            subjectUserId == facilitatorId &&
                UsersTable.selectAll().where { baseFilter }.firstOrNull() != null

        DatabaseUserRole.DEVELOPER ->
            UsersTable.selectAll().where { baseFilter }.firstOrNull() != null

        DatabaseUserRole.MANAGER -> {
            val facilitatorRow = UsersTable.selectAll()
                .where { UsersTable.id eq facilitatorId }
                .singleOrNull()
                ?: return false
            val department = facilitatorRow[UsersTable.department]
            UsersTable.selectAll().where {
                baseFilter and (UsersTable.department eq department)
            }.firstOrNull() != null
        }

        DatabaseUserRole.MENTOR ->
            MentorshipsTable.selectAll()
                .where {
                    (MentorshipsTable.mentorId eq facilitatorId) and
                        MentorshipsTable.revokedAt.isNull() and
                        (MentorshipsTable.menteeId eq subjectUserId)
                }
                .firstOrNull() != null

        DatabaseUserRole.GUEST -> false
    }
}

internal fun ensureEmployeeSelfSubject(
    facilitatorId: UUID,
    facilitatorRole: DatabaseUserRole,
    subjectUserId: UUID,
) {
    if (facilitatorRole == DatabaseUserRole.EMPLOYEE && subjectUserId != facilitatorId) {
        throw DatabaseException.Forbidden(
            message = "Employees may only view their own learning progress",
        )
    }
}
