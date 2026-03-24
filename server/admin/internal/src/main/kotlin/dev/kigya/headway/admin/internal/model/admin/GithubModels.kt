package dev.kigya.headway.admin.internal.model.admin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GithubAccessTokenResponse(
    @SerialName("access_token")
    val accessToken: String? = null,
    @SerialName("error")
    val error: String? = null,
    @SerialName("error_description")
    val errorDescription: String? = null,
)

@Serializable
internal data class GithubUserInfo(
    @SerialName("login")
    val login: String,
    @SerialName("id")
    val id: Long,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
)

@Serializable
internal data class CollaboratorPermission(
    @SerialName("permission")
    val permission: String,
)
