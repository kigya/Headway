package dev.kigya.headway.core.session.di

import dev.kigya.headway.core.session.data.AuthRepository
import dev.kigya.headway.core.session.data.DefaultLocalSessionPersistenceRepository
import dev.kigya.headway.core.session.data.DefaultProtectedRepository
import dev.kigya.headway.core.session.domain.repository.AuthRepositoryContract
import dev.kigya.headway.core.session.domain.repository.LocalSessionPersistenceContract
import dev.kigya.headway.core.session.domain.repository.ProtectedRepositoryContract
import dev.kigya.headway.core.session.domain.usecase.LoadGuestLearningOverviewUseCase
import dev.kigya.headway.core.session.domain.usecase.LoadHomeScreenSummaryUseCase
import dev.kigya.headway.core.session.domain.usecase.LoginAsGuestUseCase
import dev.kigya.headway.core.session.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.core.session.domain.usecase.LogoutGuestUseCase
import dev.kigya.headway.core.session.domain.usecase.LogoutRegisteredUseCase
import dev.kigya.headway.core.session.domain.usecase.ObtainGoogleIdTokenUseCase
import dev.kigya.headway.core.session.domain.usecase.ResolveLaunchDestinationUseCase
import dev.kigya.headway.core.session.domain.usecase.SessionClock
import dev.kigya.headway.core.session.domain.usecase.systemSessionClock
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun sessionDomainModule() = module {
    single<SessionClock> { systemSessionClock() }
    singleOf(::DefaultLocalSessionPersistenceRepository) bind LocalSessionPersistenceContract::class
    singleOf(::AuthRepository) bind AuthRepositoryContract::class
    singleOf(::DefaultProtectedRepository) bind ProtectedRepositoryContract::class
    singleOf(::ResolveLaunchDestinationUseCase)
    singleOf(::ObtainGoogleIdTokenUseCase)
    singleOf(::LoginWithGoogleUseCase)
    singleOf(::LoginAsGuestUseCase)
    singleOf(::LogoutRegisteredUseCase)
    singleOf(::LogoutGuestUseCase)
    singleOf(::LoadHomeScreenSummaryUseCase)
    singleOf(::LoadGuestLearningOverviewUseCase)
}
