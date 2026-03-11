package util

internal class GithubApiException(
    message: String,
    val statusCode: Int? = null,
    val retryable: Boolean = false,
) : RuntimeException(message)
