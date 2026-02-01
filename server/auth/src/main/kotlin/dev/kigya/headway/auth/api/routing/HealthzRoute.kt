package dev.kigya.headway.auth.api.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

internal fun Route.healthzRoute() {
    get("/healthz") {
        call.respondText(status = HttpStatusCode.OK, text = "OK")
    }
}
