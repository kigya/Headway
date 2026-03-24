package dev.kigya.headway.home.api.model.resource

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("")
class HomeResource {

    @Serializable
    @Resource("screen")
    data class Screen(
        val parent: HomeResource = HomeResource(),
    )
}
