package dev.kigya.headway.admin.api.model.resource

import io.ktor.resources.Resource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Resource("")
class AdminResource {
    @Serializable
    @Resource("app")
    data class App(@SerialName("parent") val parent: AdminResource = AdminResource())

    @Serializable
    @Resource("bootstrap")
    data class Bootstrap(@SerialName("parent") val parent: AdminResource = AdminResource())

    @Serializable
    @Resource("invite")
    data class Invite(@SerialName("parent") val parent: AdminResource = AdminResource())

    @Serializable
    @Resource("logout")
    data class Logout(@SerialName("parent") val parent: AdminResource = AdminResource())

    @Serializable
    @Resource("assets/{name}")
    data class Asset(
        @SerialName("parent")
        val parent: AdminResource = AdminResource(),
        @SerialName("name")
        val name: String,
    )

    @Serializable
    @Resource("auth")
    data class Auth(@SerialName("parent") val parent: AdminResource = AdminResource()) {
        @Serializable
        @Resource("github")
        data class Github(@SerialName("parent") val parent: Auth = Auth())

        @Serializable
        @Resource("github/callback")
        data class GithubCallback(@SerialName("parent") val parent: Auth = Auth())
    }
}
