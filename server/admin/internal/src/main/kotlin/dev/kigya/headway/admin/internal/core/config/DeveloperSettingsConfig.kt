package dev.kigya.headway.admin.internal.core.config

import dev.kigya.headway.common.util.Environment

internal data class DeveloperSettingsConfig(
    val environment: Environment,
    val githubClientId: String,
    val oauthPublicOrigin: String?,
    val repoOwner: String,
    val repoName: String,
    val sessionConfig: AdminSessionConfig,
) {
    val repositoryFullName: String
        get() = "$repoOwner/$repoName"
}
