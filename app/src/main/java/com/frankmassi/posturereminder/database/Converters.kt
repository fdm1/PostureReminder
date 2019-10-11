package com.frankmassi.posturereminder.database

import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val isoFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    private val outputFormatter = DateTimeFormatter.ofPattern("%Y-%m-%d %H:%M")

    @TypeConverter
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        return value?.let {
            return isoFormatter.parse(value, OffsetDateTime::from)
        }
    }

    @TypeConverter
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(isoFormatter)
    }
}