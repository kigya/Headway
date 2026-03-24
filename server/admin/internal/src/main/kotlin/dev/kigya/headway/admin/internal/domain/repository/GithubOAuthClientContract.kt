package dev.kigya.headway.admin.internal.domain.repository

import dev.kigya.headway.admin.internal.model.admin.CollaboratorPermission
import dev.kigya.headway.admin.internal.model.admin.GithubUserInfo

internal interface GithubOAuthClientContract {
    suspend fun exchangeCodeForAccessToken(
        code: String,
        redirectUri: String,
    ): String

    suspend fun getCurrentUser(accessToken: String): GithubUserInfo

    suspend fun getCollaboratorPermission(
        accessToken: String,
        owner: String,
        repo: String,
        username: String,
    ): CollaboratorPermission?
}
