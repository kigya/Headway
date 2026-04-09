package dev.kigya.headway.feature.learnQuestions.api

import androidx.navigation3.runtime.NavKey
import dev.kigya.headway.navigation.api.route.ScreenRouteHolderContract
import kotlinx.serialization.Serializable

@Serializable
data object LearnQuestionsScreenKey : NavKey

interface LearnQuestionsScreenRouteHolderContract : ScreenRouteHolderContract
