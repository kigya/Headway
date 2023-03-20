package com.kigya.headway.data.local.db

import androidx.room.TypeConverter
import com.kigya.headway.data.dto.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name ?: "null"
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}
