package dev.kigya.headway.core.session.domain.usecase

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.contract.GoogleIdTokenAcquisitionContract
import dev.kigya.headway.core.session.domain.error.SessionDomainError

class ObtainGoogleIdTokenUseCase(
    private val acquisition: GoogleIdTokenAcquisitionContract,
) {
    suspend operator fun invoke(): Outcome<SessionDomainError, String> = acquisition.obtainIdToken()
}
