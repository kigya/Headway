package github

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class GithubVariable(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("value")
    val value: String,
)
