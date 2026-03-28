package dev.kigya.headway.database.internal.presentation

import dev.kigya.headway.database.internal.data.repository.GuestSessionsRepository
import dev.kigya.headway.database.internal.domain.usecase.CreateGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.CreateSessionUseCase
import dev.kigya.headway.database.internal.domain.usecase.GetGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.InviteUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.UpsertGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.ValidateSessionUseCase

internal data class DatabaseApplicationUseCases(
    val getUser: GetGoogleUserUseCase,
    val createGoogleUser: CreateGoogleUserUseCase,
    val upsertGoogleUser: UpsertGoogleUserUseCase,
    val inviteUser: InviteUserUseCase,
    val createSession: CreateSessionUseCase,
    val validateSession: ValidateSessionUseCase,
    val preparation: PreparationUseCases,
    val learningQuestions: LearningQuestionsService,
    val guestSessions: GuestSessionsRepository,
)
