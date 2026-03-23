package dev.kigya.headway.admin.internal.di

import dev.kigya.headway.admin.internal.core.config.AdminSessionConfig
import dev.kigya.headway.admin.internal.core.config.ConfigurationValues
import dev.kigya.headway.admin.internal.core.config.ConfigurationValues.DATABASE_SERVICE_HOST
import dev.kigya.headway.admin.internal.core.config.ConfigurationValues.DATABASE_SERVICE_PORT
import dev.kigya.headway.admin.internal.core.config.DeveloperSettingsConfig
import dev.kigya.headway.admin.internal.data.github.GithubOAuthClient
import dev.kigya.headway.admin.internal.data.repository.DatabaseRepository
import dev.kigya.headway.admin.internal.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.admin.internal.domain.repository.GithubOAuthClientContract
import dev.kigya.headway.admin.internal.domain.usecase.InviteUserUseCase
import dev.kigya.headway.common.config.CommonConfigurationValues
import dev.kigya.headway.common.extension.baseConfig
import dev.kigya.headway.common.extension.createServiceHttpClient
import dev.kigya.headway.database.api.url.DatabaseKoinHttpClient
import dev.kigya.headway.database.api.url.databaseServiceUrlHolder
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

internal val adminInternalModule = module {
    createServiceHttpClient<DatabaseKoinHttpClient>(
        host = DATABASE_SERVICE_HOST,
        port = DATABASE_SERVICE_PORT,
        baseUrl = databaseServiceUrlHolder.baseUrl,
    )

    single {
        AdminSessionConfig(
            sessionSecret = ConfigurationValues.HEADWAY_ADMIN_SESSION_SECRET,
            isSecureCookie = CommonConfigurationValues.environment.isProd,
        )
    }
    single {
        DeveloperSettingsConfig(
            environment = CommonConfigurationValues.environment,
            githubClientId = ConfigurationValues.HEADWAY_GITHUB_OAUTH_CLIENT_ID,
            oauthPublicOrigin = ConfigurationValues.HEADWAY_ADMIN_OAUTH_PUBLIC_ORIGIN,
            repoOwner = ConfigurationValues.HEADWAY_REPO_OWNER,
            repoName = ConfigurationValues.HEADWAY_REPO_NAME,
            sessionConfig = get(),
        )
    }
    single {
        HttpClient(CIO) {
            baseConfig()
        }
    }
    single {
        DatabaseRepository(httpClient = get(named<DatabaseKoinHttpClient>()))
    } bind DatabaseRepositoryContract::class
    single {
        GithubOAuthClient(
            httpClient = get(),
            clientId = ConfigurationValues.HEADWAY_GITHUB_OAUTH_CLIENT_ID,
            clientSecret = ConfigurationValues.HEADWAY_GITHUB_OAUTH_CLIENT_SECRET,
        )
    } bind GithubOAuthClientContract::class

    singleOf(::InviteUserUseCase)
}
