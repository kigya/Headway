package github

import org.gradle.internal.impldep.com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class GithubUser(
    val login: String,
    val id: Long? = null,
)
