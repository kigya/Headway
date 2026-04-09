package dev.kigya.headway.core.apollo

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.exception.ApolloHttpException
import com.apollographql.apollo.exception.ApolloNetworkException
import dev.kigya.headway.core.network.api.HeadwayGraphqlOperationExecutor
import dev.kigya.headway.core.network.api.HeadwayRemoteGraphqlFailure
import dev.kigya.headway.core.outcome.Outcome
import kotlinx.coroutines.TimeoutCancellationException
import kotlin.coroutines.cancellation.CancellationException

class HeadwayGraphqlExecutor(
    private val apolloClient: ApolloClient,
) : HeadwayGraphqlOperationExecutor {

    override suspend fun <D : Query.Data> executeQuery(
        query: Query<D>,
    ): Outcome<HeadwayRemoteGraphqlFailure, D> =
        runCatching { apolloClient.query(query).execute() }.foldApolloResponse()

    override suspend fun <D : Mutation.Data> executeMutation(
        mutation: Mutation<D>,
    ): Outcome<HeadwayRemoteGraphqlFailure, D> =
        runCatching { apolloClient.mutation(mutation).execute() }.foldApolloResponse()

    private fun <D : Operation.Data> Result<ApolloResponse<D>>.foldApolloResponse():
        Outcome<HeadwayRemoteGraphqlFailure, D> =
        fold(
            onSuccess = { response -> response.toOutcome() },
            onFailure = { throwable ->
                when (throwable) {
                    is TimeoutCancellationException -> Outcome.failure(HeadwayRemoteGraphqlFailure.UnexpectedResponse)
                    is CancellationException -> throw throwable
                    else -> Outcome.failure(mapFailure(throwable))
                }
            },
        )

    private fun <D : Operation.Data> ApolloResponse<D>.toOutcome(): Outcome<HeadwayRemoteGraphqlFailure, D> {
        val failureException = exception
        if (failureException != null) {
            return Outcome.failure(mapFailure(failureException))
        }
        val successData = data
        if (successData != null && errors.isNullOrEmpty()) {
            return Outcome.success(successData)
        }
        val firstError = errors?.firstOrNull()
        if (firstError != null) {
            val extensions = firstError.extensions
            val category = extensions?.get(EXT_CATEGORY)?.toString()
            val reason = extensions?.get(EXT_REASON)?.toString()
            val code = extensions?.get(EXT_CODE)?.toString()
            val httpStatus = intFromExtensionOrNull(extensions?.get(EXT_HTTP_STATUS))
            val dependency = extensions?.get(EXT_DEPENDENCY)?.toString()
            val retryable = extensions?.get(EXT_RETRYABLE) as? Boolean ?: false
            return Outcome.failure(
                HeadwayRemoteGraphqlFailure.GraphQl(
                    message = firstError.message,
                    category = category,
                    reason = reason,
                    code = code,
                    httpStatus = httpStatus,
                    dependency = dependency,
                    retryable = retryable,
                ),
            )
        }
        return if (successData != null) {
            Outcome.success(successData)
        } else {
            Outcome.failure(HeadwayRemoteGraphqlFailure.UnexpectedResponse)
        }
    }

    private fun mapFailure(throwable: Throwable): HeadwayRemoteGraphqlFailure = when (throwable) {
        is ApolloNetworkException -> HeadwayRemoteGraphqlFailure.Network
        is ApolloHttpException -> HeadwayRemoteGraphqlFailure.Network
        else -> HeadwayRemoteGraphqlFailure.UnexpectedResponse
    }

    private fun intFromExtensionOrNull(value: Any?): Int? = when (value) {
        null -> null
        is Int -> value
        is Long -> value.toInt()
        is Double -> value.toInt()
        is String -> value.toIntOrNull()
        else -> value.toString().toIntOrNull()
    }

    private companion object {
        const val EXT_CATEGORY: String = "category"
        const val EXT_REASON: String = "reason"
        const val EXT_CODE: String = "code"
        const val EXT_HTTP_STATUS: String = "httpStatus"
        const val EXT_DEPENDENCY: String = "dependency"
        const val EXT_RETRYABLE: String = "retryable"
    }
}
