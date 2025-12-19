package dev.kigya.headway.database.routing

import dev.kigya.headway.database.domain.service.RefreshSessionsService
import dev.kigya.headway.database.domain.service.UsersService
import dev.kigya.headway.database.routing.session.sessionRouting
import dev.kigya.headway.database.routing.user.usersRouting
import io.ktor.server.routing.Route

internal fun Route.databaseRouting(
    usersService: UsersService,
    refreshSessionsService: RefreshSessionsService,
) {
    usersRouting(usersService)
    sessionRouting(refreshSessionsService)
}
