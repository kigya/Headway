package dev.kigya.headway.database.internal.presentation

import dev.kigya.headway.common.extension.defaultContentNegotiation
import dev.kigya.headway.common.extension.defaultResources
import dev.kigya.headway.database.internal.presentation.plugin.databaseRouting
import dev.kigya.headway.database.internal.presentation.plugin.databaseStatusPages
import io.ktor.server.application.Application

internal fun Application.installDatabaseApi(useCases: DatabaseApplicationUseCases) {
    defaultContentNegotiation()
    defaultResources()
    databaseStatusPages()
    databaseRouting(useCases = useCases)
}
