package github

import org.gradle.internal.impldep.com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.gradle.internal.impldep.com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class AccessTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String? = null,
    @JsonProperty("expires_in")
    val expiresIn: Long? = null,
    @JsonProperty("refresh_token")
    val refreshToken: String? = null,
    @JsonProperty("refresh_token_expires_in")
    val refreshTokenExpiresIn: Long? = null,
    @JsonProperty("token_type")
    val tokenType: String? = null,
    @JsonProperty("scope")
    val scope: String? = null,
    @JsonProperty("error")
    val error: String? = null,
    @JsonProperty("error_description")
    val errorDescription: String? = null,
    @JsonProperty("error_uri")
    val errorUri: String? = null,
)
