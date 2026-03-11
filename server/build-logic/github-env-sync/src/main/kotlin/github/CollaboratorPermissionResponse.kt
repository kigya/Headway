package github

import org.gradle.internal.impldep.com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.gradle.internal.impldep.com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class CollaboratorPermissionResponse(
    val permission: String? = null,
    @JsonProperty("user")
    val user: GithubUser? = null,
)
