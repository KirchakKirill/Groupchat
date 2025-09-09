package com.example.group_chat.data.local.converter

import androidx.room.TypeConverter
import org.threeten.bp.Instant

class InstantConverter
{
    @TypeConverter
    fun fromInstant(instant: Instant?):String?{
        return instant?.toString()
    }

    @TypeConverter
    fun toInstant(str:String?):Instant?{
        return str?.let { Instant.parse(it) }
    }
}