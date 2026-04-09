package dev.kigya.headway.feature.learnQuestions.di

import dev.kigya.headway.feature.learnQuestions.api.LearnQuestionsScreenRouteHolderContract
import dev.kigya.headway.feature.learnQuestions.internal.ui.route.LearnQuestionsScreenRouteHolder
import dev.kigya.headway.feature.learnQuestions.internal.ui.screen.LearnQuestionsStoreFactory
import dev.kigya.headway.feature.learnQuestions.internal.ui.screen.LearnQuestionsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val learnQuestionsModule
    get() = module {
        factoryOf(::LearnQuestionsStoreFactory)
        viewModelOf(::LearnQuestionsViewModel)
        singleOf(::LearnQuestionsScreenRouteHolder) bind LearnQuestionsScreenRouteHolderContract::class
    }
