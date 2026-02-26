package dev.kigya.headway.common.extension

import dev.kigya.headway.common.model.resource.HealthzResource
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.resources.get

fun Route.healthzRouting() {
    get<HealthzResource> { call.respondText(status = HttpStatusCode.OK, text = "OK") }
}
