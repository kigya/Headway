package github

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import util.GithubApiException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal class GithubApi(
    private val clientId: String? = null
) {

    private val mapper = jacksonObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val oauthHeaders = mapOf(
        "Accept" to "application/json",
        "Content-Type" to "application/x-www-form-urlencoded"
    )

    fun requestDeviceCode(): DeviceCodeResponse {
        val id = clientId ?: error("GitHub clientId is required for login")
        val response = Http.post(
            url = "https://github.com/login/device/code",
            headers = oauthHeaders,
            body = "client_id=$id"
        )

        if (response.code !in 200..299) {
            error("Failed to start GitHub device flow. HTTP ${response.code}: ${response.body}")
        }

        return mapper.readValue(response.body)
    }

    fun pollAccessToken(deviceCode: String): AccessTokenResponse {
        val id = clientId ?: error("GitHub clientId is required for login")

        val response = Http.post(
            url = "https://github.com/login/oauth/access_token",
            headers = mapOf(
                "Accept" to "application/json",
                "Content-Type" to "application/x-www-form-urlencoded"
            ),
            body = buildString {
                append("client_id=").append(id)
                append("&device_code=").append(deviceCode)
                append("&grant_type=urn:ietf:params:oauth:grant-type:device_code")
            }
        )

        when {
            response.code in 200..299 -> {
                return mapper.readValue(response.body)
            }

            response.code in setOf(429, 502, 503, 504) -> {
                throw GithubApiException(
                    message = "Transient GitHub error during token polling: HTTP ${response.code}",
                    statusCode = response.code,
                    retryable = true
                )
            }

            else -> {
                throw GithubApiException(
                    message = "Failed to poll GitHub access token. HTTP ${response.code}: ${response.body}",
                    statusCode = response.code,
                    retryable = false
                )
            }
        }
    }

    fun getCurrentUser(token: String): GithubUser {
        val response = Http.get(
            url = "https://api.github.com/user",
            headers = mapOf(
                "Authorization" to "Bearer $token",
                "Accept" to "application/vnd.github+json",
                "X-GitHub-Api-Version" to "2022-11-28"
            )
        )

        if (response.code in setOf(429, 502, 503, 504)) {
            throw GithubApiException(
                message = "Transient GitHub error while loading current user: HTTP ${response.code}",
                statusCode = response.code,
                retryable = true
            )
        }

        if (response.code !in 200..299) {
            throw GithubApiException(
                message = "Failed to fetch current GitHub user. HTTP ${response.code}: ${response.body}",
                statusCode = response.code,
                retryable = false
            )
        }

        return mapper.readValue(response.body)
    }

    fun getCollaboratorPermission(
        token: String,
        owner: String,
        repo: String,
        username: String
    ): CollaboratorPermissionResponse {
        val response = Http.get(
            url = "https://api.github.com/repos/$owner/$repo/collaborators/$username/permission",
            headers = mapOf(
                "Authorization" to "Bearer $token",
                "Accept" to "application/vnd.github+json",
                "X-GitHub-Api-Version" to "2022-11-28"
            )
        )

        if (response.code in setOf(429, 502, 503, 504)) {
            throw GithubApiException(
                message = "Transient GitHub error while checking collaborator permission: HTTP ${response.code}",
                statusCode = response.code,
                retryable = true
            )
        }

        if (response.code !in 200..299) {
            throw GithubApiException(
                message = "Failed to check collaborator permission. HTTP ${response.code}: ${response.body}",
                statusCode = response.code,
                retryable = false
            )
        }

        return mapper.readValue(response.body)
    }

    fun getRepositoryVariables(
        token: String,
        owner: String,
        repo: String,
    ): Map<String, String> {
        val baseUrl = "https://api.github.com/repos/$owner/$repo/actions/variables"
        return fetchAllActionVariables(
            token = token,
            listBaseUrl = baseUrl,
            notFoundReturnsEmpty = false,
            errorLabel = "repository variables",
        )
    }

    fun getEnvironmentVariables(
        token: String,
        owner: String,
        repo: String,
        environment: String,
    ): Map<String, String> {
        val encodedEnv = encodePathSegment(environment)
        val baseUrl = "https://api.github.com/repos/$owner/$repo/environments/$encodedEnv/variables"
        return fetchAllActionVariables(
            token = token,
            listBaseUrl = baseUrl,
            notFoundReturnsEmpty = true,
            errorLabel = "environment variables for '$environment'",
        )
    }

    private fun fetchAllActionVariables(
        token: String,
        listBaseUrl: String,
        notFoundReturnsEmpty: Boolean,
        errorLabel: String,
    ): Map<String, String> {
        val headers = mapOf(
            "Authorization" to "Bearer $token",
            "Accept" to "application/vnd.github+json",
            "X-GitHub-Api-Version" to "2022-11-28",
        )
        val result = linkedMapOf<String, String>()
        var page = 1
        while (true) {
            val url = "$listBaseUrl?per_page=$VARIABLES_PAGE_SIZE&page=$page"
            val response = Http.get(url = url, headers = headers)

            if (notFoundReturnsEmpty && response.code == 404 && page == 1) {
                return emptyMap()
            }

            if (response.code in setOf(429, 502, 503, 504)) {
                throw GithubApiException(
                    message = "Transient GitHub error while loading $errorLabel: HTTP ${response.code}",
                    statusCode = response.code,
                    retryable = true,
                )
            }

            if (response.code !in 200..299) {
                throw GithubApiException(
                    message = "Failed to fetch $errorLabel. HTTP ${response.code}: ${response.body}",
                    statusCode = response.code,
                    retryable = false,
                )
            }

            val parsed: VariablesListResponse = mapper.readValue(response.body)
            parsed.variables.forEach { variable ->
                result[variable.name] = variable.value
            }

            when {
                parsed.variables.isEmpty() -> break
                parsed.variables.size < VARIABLES_PAGE_SIZE -> break
                parsed.totalCount > 0 && result.size >= parsed.totalCount -> break
                else -> page += 1
            }
        }
        return result
    }

    private fun encodePathSegment(segment: String): String =
        URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20")

    private companion object {

        const val VARIABLES_PAGE_SIZE = 30
    }
}
