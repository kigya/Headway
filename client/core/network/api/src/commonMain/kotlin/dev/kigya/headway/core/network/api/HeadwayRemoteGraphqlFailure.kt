package dev.kigya.headway.core.network.api

sealed interface HeadwayRemoteGraphqlFailure {

    data object Network : HeadwayRemoteGraphqlFailure

    data class GraphQl(
        val message: String,
        val category: String?,
        val reason: String?,
        val code: String?,
        val httpStatus: Int?,
        val dependency: String?,
        val retryable: Boolean,
    ) : HeadwayRemoteGraphqlFailure

    data object UnexpectedResponse : HeadwayRemoteGraphqlFailure
}
