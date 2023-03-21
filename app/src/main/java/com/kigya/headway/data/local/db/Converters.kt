package com.kigya.headway.data.local.db

import androidx.room.TypeConverter
import com.kigya.headway.data.dto.SourceDto
import com.kigya.headway.data.model.SourceDomainModel

class Converters {

    @TypeConverter
    fun fromSource(source: SourceDomainModel): String = source.name ?: "null"

    @TypeConverter
    fun toSource(name: String): SourceDomainModel = SourceDomainModel(name, name)
}