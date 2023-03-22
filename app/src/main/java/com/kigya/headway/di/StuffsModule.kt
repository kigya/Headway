package com.kigya.headway.di

import com.kigya.headway.utils.logger.LogCatLogger
import com.kigya.headway.utils.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StuffsModule {
    @Provides
    @Singleton
    fun provideLogger(): Logger = LogCatLogger
}
