package github

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class CollaboratorPermissionResponse(
    @JsonProperty("permission")
    val permission: String? = null,
    @JsonProperty("user")
    val user: GithubUser? = null,
)
