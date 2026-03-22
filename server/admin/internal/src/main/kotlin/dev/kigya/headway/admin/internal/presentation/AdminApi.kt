package dev.kigya.headway.admin.internal.presentation

import dev.kigya.headway.admin.internal.core.config.DeveloperSettingsConfig
import dev.kigya.headway.admin.internal.domain.repository.GithubOAuthClientContract
import dev.kigya.headway.admin.internal.domain.usecase.InviteUserUseCase
import dev.kigya.headway.admin.internal.presentation.plugin.adminRouting
import dev.kigya.headway.admin.internal.presentation.plugin.adminStatusPages
import dev.kigya.headway.common.extension.defaultContentNegotiation
import dev.kigya.headway.common.extension.defaultResources
import io.ktor.server.application.Application

internal fun Application.installAdminApi(
    config: DeveloperSettingsConfig,
    githubOAuthClient: GithubOAuthClientContract,
    inviteUser: InviteUserUseCase,
) {
    defaultContentNegotiation()
    defaultResources()
    adminStatusPages()
    adminRouting(
        config = config,
        githubOAuthClient = githubOAuthClient,
        inviteUser = inviteUser,
    )
}
