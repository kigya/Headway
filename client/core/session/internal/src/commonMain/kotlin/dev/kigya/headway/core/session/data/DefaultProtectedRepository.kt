package dev.kigya.headway.core.session.data

import com.apollographql.apollo.api.Optional
import dev.kigya.headway.core.apollo.generated.HomeScreenQuery
import dev.kigya.headway.core.apollo.generated.LearningQuestionsCatalogQuery
import dev.kigya.headway.core.network.api.HeadwayGraphqlOperationExecutor
import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.outcome.mapFailure
import dev.kigya.headway.core.outcome.mapSuccess
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.domain.error.mapHeadwayGraphqlFailureToSessionError
import dev.kigya.headway.core.session.domain.repository.LocalSessionPersistenceContract
import dev.kigya.headway.core.session.domain.repository.ProtectedRepositoryContract
import dev.kigya.headway.core.session.model.GuestLearningOverview
import dev.kigya.headway.core.session.model.HomeScreenSummary
import dev.kigya.headway.core.session.model.LocalSessionRecord

internal class DefaultProtectedRepository(
    private val persistence: LocalSessionPersistenceContract,
    private val executor: HeadwayGraphqlOperationExecutor,
) : ProtectedRepositoryContract {

    override suspend fun loadHomeSummary(): Outcome<SessionDomainError, HomeScreenSummary> =
        when (val loaded = persistence.loadRecord()) {
            is Outcome.Failure -> Outcome.failure(loaded.error)
            is Outcome.Success -> when (loaded.value) {
                is LocalSessionRecord.Registered ->
                    executor.executeQuery(HomeScreenQuery()).mapFailure(::mapHeadwayGraphqlFailureToSessionError)
                        .mapSuccess { data ->
                            HomeScreenSummary(greeting = data.homeScreen.greeting)
                        }

                null,
                is LocalSessionRecord.Guest,
                -> Outcome.failure(SessionDomainError.AuthorizationDenied)
            }
        }

    override suspend fun loadGuestLearningOverview(): Outcome<SessionDomainError, GuestLearningOverview> =
        when (val loaded = persistence.loadRecord()) {
            is Outcome.Failure -> Outcome.failure(loaded.error)
            is Outcome.Success -> when (loaded.value) {
                is LocalSessionRecord.Guest ->
                    LearningQuestionsCatalogQuery(subjectUserId = Optional.Absent).let { query ->
                        executor.executeQuery(query).mapFailure(::mapHeadwayGraphqlFailureToSessionError)
                            .mapSuccess { data ->
                                val catalog = data.learningQuestionsCatalog
                                val hardCount = catalog.hardSkills.size
                                val softCount = catalog.softSkills.size
                                GuestLearningOverview(
                                    headline = "Learning catalog",
                                    detailLine = "$hardCount hard skills · $softCount soft skills",
                                )
                            }
                    }

                null,
                is LocalSessionRecord.Registered,
                -> Outcome.failure(SessionDomainError.AuthorizationDenied)
            }
        }
}
