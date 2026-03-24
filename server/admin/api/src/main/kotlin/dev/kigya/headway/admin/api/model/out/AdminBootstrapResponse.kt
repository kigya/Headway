package dev.kigya.headway.admin.api.model.out

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminBootstrapResponse(
    @SerialName("environment")
    val environment: String,
    @SerialName("is_prod")
    val isProd: Boolean,
    @SerialName("repository_full_name")
    val repositoryFullName: String,
    @SerialName("session")
    val session: AdminSessionUserDto? = null,
    @SerialName("role_options")
    val roleOptions: List<AdminOptionDto>,
    @SerialName("department_options")
    val departmentOptions: List<AdminOptionDto>,
)

@Serializable
data class AdminSessionUserDto(
    @SerialName("github_login")
    val githubLogin: String,
    @SerialName("github_avatar_url")
    val githubAvatarUrl: String? = null,
)

@Serializable
data class AdminOptionDto(
    @SerialName("value")
    val value: String,
    @SerialName("label")
    val label: String,
)
