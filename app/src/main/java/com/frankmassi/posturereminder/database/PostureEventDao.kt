package com.frankmassi.posturereminder.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PostureEventDao {
    @Query("SELECT * from posture_events ORDER BY id ASC")
    fun getAllEvents(): LiveData<List<PostureEvent>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(postureEvent: PostureEvent)

    @Query("DELETE FROM posture_events")
    suspend fun deleteAll()

    @Query("SELECT * FROM posture_events ORDER BY created_at DESC limit :numberOfEvents")
    fun getRecentEvents(numberOfEvents: Int): LiveData<List<PostureEvent>>
}