package dev.kigya.headway.core.session.data

import dev.kigya.headway.core.apollo.generated.HomeScreenQuery
import dev.kigya.headway.core.apollo.generated.type.GatewayUserRole
import dev.kigya.headway.core.apollo.generated.type.HomeScreenNextInterviewType
import dev.kigya.headway.core.apollo.generated.type.HomeScreenSectionId
import dev.kigya.headway.core.apollo.generated.type.HomeScreenSectionStyle
import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.model.HomeAccessRole
import dev.kigya.headway.core.session.model.HomeActionSemanticType
import dev.kigya.headway.core.session.model.HomeActionVisualStyle
import dev.kigya.headway.core.session.model.HomeNextSessionType
import dev.kigya.headway.core.session.model.HomeScreenAction
import dev.kigya.headway.core.session.model.HomeScreenSummary
import dev.kigya.headway.core.session.model.HomeUserSummary
import kotlinx.collections.immutable.toPersistentList

internal fun mapHomeScreenPayload(
    home: HomeScreenQuery.HomeScreen,
): Outcome<SessionDomainError, HomeScreenSummary> {
    val accessRole = mapAccessRole(home.accessRole)
        ?: return Outcome.failure(SessionDomainError.Unexpected)
    val dateLabel = home.dateLabel.trim()
    if (dateLabel.isEmpty()) {
        return Outcome.failure(SessionDomainError.Unexpected)
    }
    val displayName = home.displayName.trim()
    if (displayName.isEmpty()) {
        return Outcome.failure(SessionDomainError.Unexpected)
    }
    val readiness = home.readinessPercent?.takeIf { it in READINESS_RANGE }
    val nextSessionType = home.nextInterviewType?.let(::mapNextSessionType)
    val nextSessionTypeLabel = home.nextInterviewTypeLabel?.trim()?.takeIf { it.isNotEmpty() }
    val actions = home.sections.asSequence()
        .mapNotNull(::mapHomeSection)
        .toPersistentList()
    val userSummary = HomeUserSummary(
        displayName = displayName,
        avatarUrl = home.avatarUrl?.trim()?.takeIf { it.isNotEmpty() },
        dateLabel = dateLabel,
        accessRole = accessRole,
        roleLabel = home.roleLabel?.trim()?.takeIf { it.isNotEmpty() },
        readinessPercent = readiness,
        nextSessionType = nextSessionType,
        nextSessionTypeLabel = nextSessionTypeLabel,
    )
    return Outcome.success(
        HomeScreenSummary(
            userSummary = userSummary,
            actions = actions,
        ),
    )
}

private fun mapAccessRole(role: GatewayUserRole): HomeAccessRole? = when (role) {
    GatewayUserRole.DEVELOPER -> HomeAccessRole.Developer
    GatewayUserRole.MANAGER -> HomeAccessRole.Manager
    GatewayUserRole.MENTOR -> HomeAccessRole.Mentor
    GatewayUserRole.EMPLOYEE -> HomeAccessRole.Employee
    GatewayUserRole.GUEST -> HomeAccessRole.Guest
    GatewayUserRole.UNKNOWN__ -> null
}

private fun mapNextSessionType(type: HomeScreenNextInterviewType): HomeNextSessionType? =
    when (type) {
        HomeScreenNextInterviewType.CHECK -> HomeNextSessionType.Check
        HomeScreenNextInterviewType.SPOT -> HomeNextSessionType.Spot
        HomeScreenNextInterviewType.MOCK -> HomeNextSessionType.Mock
        HomeScreenNextInterviewType.SOFT_SKILLS -> HomeNextSessionType.SoftSkills
        HomeScreenNextInterviewType.UNKNOWN__ -> null
    }

private fun mapHomeSection(section: HomeScreenQuery.Section): HomeScreenAction? {
    if (section.id == HomeScreenSectionId.UNKNOWN__) {
        return null
    }
    val semantic = mapSemanticType(section.id) ?: return null
    val style = mapVisualStyle(section.style) ?: return null
    val iconUrl = section.iconUrl.trim()
    if (iconUrl.isEmpty()) {
        return null
    }
    val title = section.title.trim()
    if (title.isEmpty()) {
        return null
    }
    return HomeScreenAction(
        semanticType = semantic,
        title = title,
        style = style,
        iconUrl = iconUrl,
    )
}

private fun mapSemanticType(id: HomeScreenSectionId): HomeActionSemanticType? = when (id) {
    HomeScreenSectionId.HOME -> HomeActionSemanticType.Home
    HomeScreenSectionId.START_TRAINING_SESSION -> HomeActionSemanticType.StartTrainingSession
    HomeScreenSectionId.LEARN_QUESTIONS -> HomeActionSemanticType.LearnQuestions
    HomeScreenSectionId.EMPLOYEE_PROGRESS_MANAGEMENT -> HomeActionSemanticType.EmployeeProgressManagement
    HomeScreenSectionId.PEOPLE_MANAGEMENT -> HomeActionSemanticType.PeopleManagement
    HomeScreenSectionId.VIEW_STATISTICS -> HomeActionSemanticType.ViewStatistics
    HomeScreenSectionId.ABOUT -> HomeActionSemanticType.About
    HomeScreenSectionId.SIGN_OUT -> HomeActionSemanticType.SignOut
    HomeScreenSectionId.UNKNOWN__ -> null
}

private fun mapVisualStyle(style: HomeScreenSectionStyle): HomeActionVisualStyle? = when (style) {
    HomeScreenSectionStyle.FILLED_PRIMARY -> HomeActionVisualStyle.FilledPrimary
    HomeScreenSectionStyle.OUTLINED_ACCENT -> HomeActionVisualStyle.OutlinedAccent
    HomeScreenSectionStyle.DEFAULT -> HomeActionVisualStyle.Default
    HomeScreenSectionStyle.UNKNOWN__ -> null
}

private const val READINESS_PERCENT_MAX: Int = 100

private val READINESS_RANGE: IntRange = 0..READINESS_PERCENT_MAX
