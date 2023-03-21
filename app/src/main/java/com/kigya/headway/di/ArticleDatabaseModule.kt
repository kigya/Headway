package com.kigya.headway.di

import android.content.Context
import androidx.room.Room
import com.kigya.headway.data.local.db.ArticleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ArticleDatabaseModule {

    @Singleton
    @Provides
    fun provideArticleDatabase(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(
        context,
        ArticleDatabase::class.java,
        DATABASE_NAME,
    ).build()

    @Singleton
    @Provides
    fun provideArticleDao(
        db: ArticleDatabase,
    ) = db.getArticleDao()

    companion object {
        private const val DATABASE_NAME = "article_db.db"
    }
}
