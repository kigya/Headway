package dev.kigya.headway.feature.learnQuestions.internal.ui.route

import androidx.compose.runtime.Composable
import dev.kigya.headway.feature.learnQuestions.api.LearnQuestionsScreenKey
import dev.kigya.headway.feature.learnQuestions.api.LearnQuestionsScreenRouteHolderContract
import dev.kigya.headway.feature.learnQuestions.internal.ui.screen.LearnQuestionsScreen

class LearnQuestionsScreenRouteHolder : LearnQuestionsScreenRouteHolderContract {
    override val screenNavigationKey = LearnQuestionsScreenKey

    override val content = @Composable { LearnQuestionsScreen() }
}
