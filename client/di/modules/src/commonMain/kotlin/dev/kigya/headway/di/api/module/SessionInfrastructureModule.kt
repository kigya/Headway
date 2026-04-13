package dev.kigya.headway.di.api.module

import com.apollographql.apollo.ApolloClient
import dev.kigya.headway.core.apollo.HeadwayAccessTokenHolder
import dev.kigya.headway.core.apollo.HeadwayGraphqlExecutor
import dev.kigya.headway.core.apollo.createHeadwayApolloClient
import dev.kigya.headway.core.network.api.HeadwayAccessTokenStore
import dev.kigya.headway.core.network.api.HeadwayGraphqlOperationExecutor
import dev.kigya.headway.di.api.HeadwayGraphqlHttpUrl
import org.koin.dsl.module

fun sessionInfrastructureModule() = module {
    single<HeadwayAccessTokenStore> { HeadwayAccessTokenHolder() }
    single<ApolloClient> {
        createHeadwayApolloClient(
            serverUrl = get<HeadwayGraphqlHttpUrl>().value,
            accessTokenProvider = { get<HeadwayAccessTokenStore>().current() },
        )
    }
    single<HeadwayGraphqlOperationExecutor> { HeadwayGraphqlExecutor(get()) }
}
