package com.frankmassi.posturereminder.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "posture_events")
data class PostureEvent(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "was_good") val wasGood: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: OffsetDateTime? = null) {

    fun displayCreatedAt(): String? {
        return createdAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd h:mma"))?.toLowerCase()
    }
}