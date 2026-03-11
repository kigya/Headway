package github

import org.gradle.internal.impldep.com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class GithubVariable(
    val name: String,
    val value: String,
)
