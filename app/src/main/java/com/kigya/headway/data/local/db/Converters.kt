package com.kigya.headway.data.local.db

import androidx.room.TypeConverter
import com.kigya.headway.data.model.SourceDomainModel

class Converters {

    @TypeConverter
    fun sourceToString(source: SourceDomainModel): String = source.name ?: String()

    @TypeConverter
    fun stringToSource(name: String): SourceDomainModel = SourceDomainModel(name, name)
}