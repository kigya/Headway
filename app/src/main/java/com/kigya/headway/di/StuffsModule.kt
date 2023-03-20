package com.kigya.headway.di

import com.kigya.headway.utils.logger.LogCatLogger
import com.kigya.headway.utils.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class StuffsModule {
    @Provides
    fun provideLogger(): Logger = LogCatLogger
}
