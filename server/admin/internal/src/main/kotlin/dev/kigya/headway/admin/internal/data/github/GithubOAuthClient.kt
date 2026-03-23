package dev.kigya.headway.admin.internal.data.github

import dev.kigya.headway.admin.internal.core.exception.AdminException
import dev.kigya.headway.admin.internal.domain.repository.GithubOAuthClientContract
import dev.kigya.headway.admin.internal.model.admin.CollaboratorPermission
import dev.kigya.headway.admin.internal.model.admin.GithubAccessTokenResponse
import dev.kigya.headway.admin.internal.model.admin.GithubUserInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode

internal class GithubOAuthClient(
    private val httpClient: HttpClient,
    private val clientId: String,
    private val clientSecret: String,
) : GithubOAuthClientContract {
    override suspend fun exchangeCodeForAccessToken(
        code: String,
        redirectUri: String,
    ): String {
        val response = requestGithub {
            httpClient.post {
                url(TOKEN_URL)
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    Parameters.build {
                        append("client_id", clientId)
                        append("client_secret", clientSecret)
                        append("code", code)
                        append("redirect_uri", redirectUri)
                    }.formUrlEncode(),
                )
            }
        }

        if (response.status.value in transientStatusCodes) {
            throw AdminException.DependencyUnavailable(dependency = GITHUB_DEPENDENCY)
        }
        if (response.status.value !in successStatusCodes) {
            throw AdminException.Unauthorized("GitHub authorization failed")
        }

        val payload = response.body<GithubAccessTokenResponse>()
        return payload.accessToken
            ?.trim()
            ?.takeIf(String::isNotBlank)
            ?: throw AdminException.Unauthorized(
                payload.errorDescription?.takeIf(String::isNotBlank) ?: "GitHub authorization failed",
            )
    }

    override suspend fun getCurrentUser(accessToken: String): GithubUserInfo {
        val response = requestGithub {
            httpClient.get {
                url(USER_URL)
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                header(HttpHeaders.Accept, GITHUB_API_ACCEPT)
                header(GITHUB_API_VERSION_HEADER, GITHUB_API_VERSION)
            }
        }

        if (response.status.value in transientStatusCodes) {
            throw AdminException.DependencyUnavailable(dependency = GITHUB_DEPENDENCY)
        }
        if (response.status.value !in successStatusCodes) {
            throw AdminException.Unauthorized("GitHub authorization failed")
        }

        return response.body()
    }

    override suspend fun getCollaboratorPermission(
        accessToken: String,
        owner: String,
        repo: String,
        username: String,
    ): CollaboratorPermission? {
        val response = requestGithub {
            httpClient.get {
                url("$REPOSITORY_API_PREFIX/$owner/$repo/collaborators/$username/permission")
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                header(HttpHeaders.Accept, GITHUB_API_ACCEPT)
                header(GITHUB_API_VERSION_HEADER, GITHUB_API_VERSION)
            }
        }

        if (response.status.value == HTTP_NOT_FOUND) {
            return null
        }
        if (response.status.value in transientStatusCodes) {
            throw AdminException.DependencyUnavailable(dependency = GITHUB_DEPENDENCY)
        }
        if (response.status.value !in successStatusCodes) {
            throw AdminException.DependencyUnavailable(
                dependency = GITHUB_DEPENDENCY,
                message = response.bodyAsText().ifBlank { "GitHub collaborator verification failed" },
            )
        }

        return response.body()
    }

    private suspend fun requestGithub(request: suspend () -> HttpResponse): HttpResponse = try {
        request()
    } catch (throwable: Throwable) {
        throw AdminException.DependencyUnavailable(
            dependency = GITHUB_DEPENDENCY,
            cause = throwable,
        )
    }
}

private const val GITHUB_DEPENDENCY = "github"
private const val HTTP_OK_MIN = 200
private const val HTTP_OK_MAX = 299
private const val HTTP_NOT_FOUND = 404
private const val HTTP_TOO_MANY_REQUESTS = 429
private const val HTTP_INTERNAL_SERVER_ERROR = 500
private const val HTTP_BAD_GATEWAY = 502
private const val HTTP_SERVICE_UNAVAILABLE = 503
private const val HTTP_GATEWAY_TIMEOUT = 504
private const val TOKEN_URL = "https://github.com/login/oauth/access_token"
private const val USER_URL = "https://api.github.com/user"
private const val REPOSITORY_API_PREFIX = "https://api.github.com/repos"
private const val GITHUB_API_ACCEPT = "application/vnd.github+json"
private const val GITHUB_API_VERSION_HEADER = "X-GitHub-Api-Version"
private const val GITHUB_API_VERSION = "2022-11-28"
private val transientStatusCodes = setOf(
    HTTP_TOO_MANY_REQUESTS,
    HTTP_INTERNAL_SERVER_ERROR,
    HTTP_BAD_GATEWAY,
    HTTP_SERVICE_UNAVAILABLE,
    HTTP_GATEWAY_TIMEOUT,
)
private val successStatusCodes = HTTP_OK_MIN..HTTP_OK_MAX
