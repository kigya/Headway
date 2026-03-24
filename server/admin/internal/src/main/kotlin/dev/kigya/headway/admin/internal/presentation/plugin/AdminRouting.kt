package dev.kigya.headway.admin.internal.presentation.plugin

import dev.kigya.headway.admin.api.model.`in`.AdminInviteUserPayloadDto
import dev.kigya.headway.admin.api.model.out.AdminInviteUserResponse
import dev.kigya.headway.admin.api.model.resource.AdminResource
import dev.kigya.headway.admin.api.url.adminServiceUrlHolder
import dev.kigya.headway.admin.internal.core.config.DeveloperSettingsConfig
import dev.kigya.headway.admin.internal.core.exception.AdminException
import dev.kigya.headway.admin.internal.core.session.AdminSession
import dev.kigya.headway.admin.internal.core.session.clearAdminCookies
import dev.kigya.headway.admin.internal.core.session.readAdminSessionOrNull
import dev.kigya.headway.admin.internal.core.session.readOauthState
import dev.kigya.headway.admin.internal.core.session.requireAdminSession
import dev.kigya.headway.admin.internal.core.session.setAdminSession
import dev.kigya.headway.admin.internal.core.session.setOauthState
import dev.kigya.headway.admin.internal.domain.repository.GithubOAuthClientContract
import dev.kigya.headway.admin.internal.domain.usecase.InviteUserUseCase
import dev.kigya.headway.common.extension.healthzRouting
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

internal fun Application.adminRouting(
    config: DeveloperSettingsConfig,
    githubOAuthClient: GithubOAuthClientContract,
    inviteUser: InviteUserUseCase,
) {
    routing {
        route(adminServiceUrlHolder.baseUrl) {
            healthzRouting()
            appPage()
            assetPage()
            bootstrap(config)
            githubAuth(config)
            githubCallback(config, githubOAuthClient)
            invite(config, inviteUser)
            logout(config)
        }
    }
}

private fun Route.appPage() {
    get<AdminResource.App> {
        call.respondClasspathAsset(
            resourcePath = INDEX_RESOURCE_PATH,
            contentType = ContentType.Text.Html,
        )
    }
}

private fun Route.assetPage() {
    get<AdminResource.Asset> { resource ->
        when (resource.name) {
            APP_JS_NAME ->
                call.respondClasspathAsset(
                    resourcePath = APP_JS_RESOURCE_PATH,
                    contentType = javascriptContentType,
                )

            STYLES_CSS_NAME ->
                call.respondClasspathAsset(
                    resourcePath = STYLES_CSS_RESOURCE_PATH,
                    contentType = ContentType.Text.CSS,
                )

            else -> throw NotFoundException("Asset not found")
        }
    }
}

private fun Route.bootstrap(config: DeveloperSettingsConfig) {
    get<AdminResource.Bootstrap> {
        call.respond(config.toBootstrap(call.readAdminSessionOrNull(config.sessionConfig)))
    }
}

private fun Route.githubAuth(config: DeveloperSettingsConfig) {
    get<AdminResource.Auth.Github> {
        val state = randomState()
        call.setOauthState(config.sessionConfig, state)
        call.respondRedirect(
            buildGithubAuthorizeUrl(
                clientId = config.githubClientId,
                redirectUri = githubCallbackUrl(
                    call = call,
                    oauthPublicOrigin = config.oauthPublicOrigin,
                ),
                state = state,
            ),
        )
    }
}

private fun Route.githubCallback(
    config: DeveloperSettingsConfig,
    githubOAuthClient: GithubOAuthClientContract,
) {
    get<AdminResource.Auth.GithubCallback> {
        val expectedState = call.readOauthState(config.sessionConfig)
        val providedState = call.request.queryParameters["state"]?.trim()
        val code = call.request.queryParameters["code"]?.trim()
        if (expectedState == null || providedState.isNullOrBlank() || expectedState != providedState) {
            call.clearAdminCookies(config.sessionConfig)
            call.respondRedirect(appRedirect(error = INVALID_STATE_ERROR))
            return@get
        }
        if (code.isNullOrBlank()) {
            call.clearAdminCookies(config.sessionConfig)
            call.respondRedirect(appRedirect(error = AUTH_FAILED_ERROR))
            return@get
        }

        try {
            val accessToken = githubOAuthClient.exchangeCodeForAccessToken(
                code = code,
                redirectUri = githubCallbackUrl(
                    call = call,
                    oauthPublicOrigin = config.oauthPublicOrigin,
                ),
            )
            val githubUser = githubOAuthClient.getCurrentUser(accessToken)
            val permission = githubOAuthClient.getCollaboratorPermission(
                accessToken = accessToken,
                owner = config.repoOwner,
                repo = config.repoName,
                username = githubUser.login,
            )

            call.clearAdminCookies(config.sessionConfig)
            if (permission == null || permission.permission !in allowedCollaboratorPermissions) {
                call.respondRedirect(
                    appRedirect(
                        screen = ACCESS_DENIED_SCREEN,
                        login = githubUser.login,
                        repo = config.repositoryFullName,
                    ),
                )
                return@get
            }

            call.setAdminSession(
                config = config.sessionConfig,
                session = AdminSession(
                    githubLogin = githubUser.login,
                    githubAvatarUrl = githubUser.avatarUrl,
                    hasRepositoryAccess = true,
                    createdAt = System.currentTimeMillis(),
                ),
            )
            call.respondRedirect(appRedirect())
        } catch (exception: AdminException.Unauthorized) {
            call.clearAdminCookies(config.sessionConfig)
            call.respondRedirect(appRedirect(error = AUTH_FAILED_ERROR))
        } catch (exception: AdminException.DependencyUnavailable) {
            call.clearAdminCookies(config.sessionConfig)
            call.respondRedirect(appRedirect(error = GITHUB_UNAVAILABLE_ERROR))
        }
    }
}

private fun Route.invite(
    config: DeveloperSettingsConfig,
    inviteUser: InviteUserUseCase,
) {
    post<AdminResource.Invite> {
        call.requireAdminSession(config.sessionConfig)
        val request = call.receive<AdminInviteUserPayloadDto>()
        val invitedUser = inviteUser(
            email = request.email,
            role = request.role,
            department = request.department,
        )
        call.respond(
            AdminInviteUserResponse(
                message = buildInviteMessage(
                    email = invitedUser.email,
                    role = invitedUser.role,
                    department = invitedUser.department,
                ),
                invitedEmail = invitedUser.email,
                role = invitedUser.role,
                department = invitedUser.department,
            ),
        )
    }
}

private fun Route.logout(config: DeveloperSettingsConfig) {
    post<AdminResource.Logout> {
        call.clearAdminCookies(config.sessionConfig)
        call.respond(HttpStatusCode.NoContent)
    }
}
