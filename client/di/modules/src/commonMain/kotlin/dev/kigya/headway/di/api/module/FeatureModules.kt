package dev.kigya.headway.di.api.module

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.kigya.headway.feature.auth.di.authModule
import dev.kigya.headway.feature.home.di.homeModule
import dev.kigya.headway.feature.learnQuestions.di.learnQuestionsModule
import dev.kigya.headway.feature.splash.di.splashModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureModules: List<Module>
    get() {
        val baseFeatureModule = module {
            singleOf(::DefaultStoreFactory) bind StoreFactory::class
        }
        return listOf(
            baseFeatureModule,
            splashModule,
            authModule,
            homeModule,
            learnQuestionsModule,
        )
    }
