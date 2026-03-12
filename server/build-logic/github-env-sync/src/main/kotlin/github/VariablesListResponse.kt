package github

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class VariablesListResponse(
    @JsonProperty("total_count")
    val totalCount: Int = 0,
    @JsonProperty("variables")
    val variables: List<GithubVariable> = emptyList(),
)
