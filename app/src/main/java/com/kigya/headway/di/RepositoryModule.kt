package com.kigya.headway.di

import com.kigya.headway.data.NewsRepository
import com.kigya.headway.data.NewsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindRepository(repositoryImpl: NewsRepositoryImpl): NewsRepository
}
