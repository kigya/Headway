package dev.kigya.headway.database.internal.domain

import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.internal.domain.error.DatabaseException

internal object PreparationFacilitatorPolicy {

    fun ensurePreparationRole(facilitatorRole: DatabaseUserRole) {
        when (facilitatorRole) {
            DatabaseUserRole.GUEST,
            DatabaseUserRole.EMPLOYEE,
            -> throw DatabaseException.PreparationForbidden()

            DatabaseUserRole.DEVELOPER,
            DatabaseUserRole.MANAGER,
            DatabaseUserRole.MENTOR,
            -> Unit
        }
    }
}
