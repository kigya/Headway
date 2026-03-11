package github

import org.gradle.internal.impldep.com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.gradle.internal.impldep.com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class VariablesListResponse(
    @JsonProperty("total_count")
    val totalCount: Int = 0,
    val variables: List<GithubVariable> = emptyList(),
)
