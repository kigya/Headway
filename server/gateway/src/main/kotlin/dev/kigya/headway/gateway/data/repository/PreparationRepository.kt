package dev.kigya.headway.gateway.data.repository

import dev.kigya.headway.database.api.model.`in`.DatabasePreparationSelectQuestionRequestDto
import dev.kigya.headway.database.api.model.`in`.DatabasePreparationStartSessionRequestDto
import dev.kigya.headway.database.api.model.`in`.DatabasePreparationSubmitOutcomeRequestDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationCatalogResponseDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationEmployeesResponseDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationReadinessResponseDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionStateDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionSummaryDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationStartSessionResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.api.model.resource.DatabaseResource
import dev.kigya.headway.gateway.core.http.upstreamCall
import dev.kigya.headway.gateway.domain.repository.PreparationRepositoryContract
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.util.UUID

internal class PreparationRepository(
    private val httpClient: HttpClient,
) : PreparationRepositoryContract {

    override suspend fun getSetupEmployees(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
    ): DatabasePreparationEmployeesResponseDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.get(
                DatabaseResource.Preparation.SetupEmployees(
                    facilitatorUserId = facilitatorId,
                    facilitatorRole = facilitatorRole,
                ),
            )
        },
        onSuccess = { it.body<DatabasePreparationEmployeesResponseDto>() },
    )

    override suspend fun getEmployeeReadiness(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        subjectUserId: UUID,
    ): DatabasePreparationReadinessResponseDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.get(
                DatabaseResource.Preparation.EmployeeReadiness(
                    subjectUserId = subjectUserId,
                    facilitatorUserId = facilitatorId,
                    facilitatorRole = facilitatorRole,
                ),
            )
        },
        onSuccess = { it.body<DatabasePreparationReadinessResponseDto>() },
    )

    override suspend fun getFormatCatalog(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        locale: String,
    ): DatabasePreparationCatalogResponseDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.get(
                DatabaseResource.Preparation.Catalog(
                    locale = locale,
                    facilitatorUserId = facilitatorId,
                    facilitatorRole = facilitatorRole,
                ),
            )
        },
        onSuccess = { it.body<DatabasePreparationCatalogResponseDto>() },
    )

    override suspend fun startSession(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        body: DatabasePreparationStartSessionRequestDto,
    ): DatabasePreparationStartSessionResponseDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.post(
                DatabaseResource.Preparation.Sessions(
                    facilitatorUserId = facilitatorId,
                    facilitatorRole = facilitatorRole,
                ),
            ) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        },
        onSuccess = { it.body<DatabasePreparationStartSessionResponseDto>() },
    )

    override suspend fun getSessionState(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
    ): DatabasePreparationSessionStateDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.get(
                sessionById(
                    facilitatorId = facilitatorId,
                    facilitatorRole = facilitatorRole,
                    sessionId = sessionId,
                ),
            )
        },
        onSuccess = { it.body<DatabasePreparationSessionStateDto>() },
    )

    override suspend fun submitOutcome(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        body: DatabasePreparationSubmitOutcomeRequestDto,
    ): DatabasePreparationSessionStateDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.post(
                DatabaseResource.Preparation.Sessions.ById.Outcomes(
                    parent = sessionById(
                        facilitatorId = facilitatorId,
                        facilitatorRole = facilitatorRole,
                        sessionId = sessionId,
                    ),
                ),
            ) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        },
        onSuccess = { it.body<DatabasePreparationSessionStateDto>() },
    )

    override suspend fun selectQuestion(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        body: DatabasePreparationSelectQuestionRequestDto,
    ): DatabasePreparationSessionStateDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.post(
                DatabaseResource.Preparation.Sessions.ById.SelectQuestion(
                    parent = sessionById(
                        facilitatorId = facilitatorId,
                        facilitatorRole = facilitatorRole,
                        sessionId = sessionId,
                    ),
                ),
            ) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        },
        onSuccess = { it.body<DatabasePreparationSessionStateDto>() },
    )

    override suspend fun finishSession(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
    ): DatabasePreparationSessionStateDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.post(
                DatabaseResource.Preparation.Sessions.ById.Finish(
                    parent = sessionById(
                        facilitatorId = facilitatorId,
                        facilitatorRole = facilitatorRole,
                        sessionId = sessionId,
                    ),
                ),
            )
        },
        onSuccess = { it.body<DatabasePreparationSessionStateDto>() },
    )

    override suspend fun getSessionSummary(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        locale: String,
    ): DatabasePreparationSessionSummaryDto = upstreamCall(
        dependency = DATABASE_UPSTREAM,
        request = {
            httpClient.get(
                DatabaseResource.Preparation.Sessions.ById.Summary(
                    parent = sessionById(
                        facilitatorId = facilitatorId,
                        facilitatorRole = facilitatorRole,
                        sessionId = sessionId,
                    ),
                    locale = locale,
                ),
            )
        },
        onSuccess = { it.body<DatabasePreparationSessionSummaryDto>() },
    )

    private fun sessionById(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
    ): DatabaseResource.Preparation.Sessions.ById {
        val sessions = DatabaseResource.Preparation.Sessions(
            facilitatorUserId = facilitatorId,
            facilitatorRole = facilitatorRole,
        )
        return DatabaseResource.Preparation.Sessions.ById(
            parent = sessions,
            sessionId = sessionId,
            facilitatorUserId = facilitatorId,
            facilitatorRole = facilitatorRole,
        )
    }
}

private const val DATABASE_UPSTREAM: String = "database"
