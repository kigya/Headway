package com.kigya.headway.di

import com.kigya.headway.data.NewsRepository
import com.kigya.headway.data.NewsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindRepository(repositoryImpl: NewsRepositoryImpl): NewsRepository
}
