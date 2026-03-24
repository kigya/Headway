package dev.kigya.headway.admin.api.model.resource

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("")
class AdminResource {
    @Serializable
    @Resource("app")
    data class App(val parent: AdminResource = AdminResource())

    @Serializable
    @Resource("bootstrap")
    data class Bootstrap(val parent: AdminResource = AdminResource())

    @Serializable
    @Resource("invite")
    data class Invite(val parent: AdminResource = AdminResource())

    @Serializable
    @Resource("logout")
    data class Logout(val parent: AdminResource = AdminResource())

    @Serializable
    @Resource("assets/{name}")
    data class Asset(
        val parent: AdminResource = AdminResource(),
        val name: String,
    )

    @Serializable
    @Resource("auth")
    data class Auth(val parent: AdminResource = AdminResource()) {
        @Serializable
        @Resource("github")
        data class Github(val parent: Auth = Auth())

        @Serializable
        @Resource("github/callback")
        data class GithubCallback(val parent: Auth = Auth())
    }
}
