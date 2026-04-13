package dev.kigya.headway.core.session.model

sealed interface LaunchDestination {
    data object Auth : LaunchDestination
    data object Home : LaunchDestination
    data object LearnQuestions : LaunchDestination
}
