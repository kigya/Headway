package github

import org.gradle.internal.impldep.com.fasterxml.jackson.annotation.JsonProperty

internal data class DeviceCodeResponse(
    @JsonProperty("device_code")
    val deviceCode: String,
    @JsonProperty("user_code")
    val userCode: String,
    @JsonProperty("verification_uri")
    val verificationUri: String,
    @JsonProperty("expires_in")
    val expiresIn: Long,
    @JsonProperty("interval")
    val interval: Long,
)
