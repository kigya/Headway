package dev.kigya.headway.database.api.routing

import dev.kigya.headway.database.api.port.RefreshSessionsServiceContract
import dev.kigya.headway.database.api.port.UsersServiceContract
import dev.kigya.headway.database.api.routing.session.sessionRouting
import dev.kigya.headway.database.api.routing.user.usersRouting
import io.ktor.server.routing.Route

internal fun Route.databaseRouting(
    usersService: UsersServiceContract,
    refreshSessionsService: RefreshSessionsServiceContract,
) {
    usersRouting(usersService)
    sessionRouting(refreshSessionsService)
}
