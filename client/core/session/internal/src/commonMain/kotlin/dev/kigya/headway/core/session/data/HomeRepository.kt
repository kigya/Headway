package dev.kigya.headway.core.session.data

import dev.kigya.headway.core.apollo.generated.HomeScreenQuery
import dev.kigya.headway.core.network.api.HeadwayGraphqlOperationExecutor
import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.outcome.mapFailure
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.domain.error.mapHeadwayGraphqlFailureToSessionError
import dev.kigya.headway.core.session.domain.repository.HomeRepositoryContract
import dev.kigya.headway.core.session.domain.repository.LocalSessionPersistenceContract
import dev.kigya.headway.core.session.model.HomeScreenSummary
import dev.kigya.headway.core.session.model.LocalSessionRecord

internal class HomeRepository(
    private val persistence: LocalSessionPersistenceContract,
    private val executor: HeadwayGraphqlOperationExecutor,
) : HomeRepositoryContract {

    override suspend fun loadHomeSummary(): Outcome<SessionDomainError, HomeScreenSummary> =
        when (val loaded = persistence.loadRecord()) {
            is Outcome.Failure -> Outcome.failure(loaded.error)
            is Outcome.Success -> when (val record = loaded.value) {
                is LocalSessionRecord.Registered -> when (
                    val query = executor.executeQuery(HomeScreenQuery())
                        .mapFailure(::mapHeadwayGraphqlFailureToSessionError)
                ) {
                    is Outcome.Failure -> query
                    is Outcome.Success -> mapHomeScreenPayload(
                        home = query.value.homeScreen,
                        registeredDisplayNameFallback = resolveRegisteredDisplayNameFallback(record),
                    )
                }

                null,
                is LocalSessionRecord.Guest,
                -> Outcome.failure(SessionDomainError.AuthorizationDenied)
            }
        }
}

private fun resolveRegisteredDisplayNameFallback(record: LocalSessionRecord.Registered): String {
    val name = record.userName.trim()
    if (name.isNotEmpty()) {
        return name
    }
    val localPart = record.userEmail.substringBefore('@').trim()
    if (localPart.isNotEmpty()) {
        return localPart
    }
    return REGISTERED_DISPLAY_NAME_FALLBACK
}

private const val REGISTERED_DISPLAY_NAME_FALLBACK: String = "User"
