package dev.kigya.headway.core.network.api

import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Query
import dev.kigya.headway.core.outcome.Outcome

interface HeadwayGraphqlOperationExecutor {
    suspend fun <D : Query.Data> executeQuery(
        query: Query<D>,
    ): Outcome<HeadwayRemoteGraphqlFailure, D>

    suspend fun <D : Mutation.Data> executeMutation(
        mutation: Mutation<D>,
    ): Outcome<HeadwayRemoteGraphqlFailure, D>
}
