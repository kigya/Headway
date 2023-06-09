package com.kigya.headway.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kigya.headway.data.model.ArticleDomainModel

@Database(
    entities = [ArticleDomainModel::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao
}
