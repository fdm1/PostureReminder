package com.frankmassi.posturereminder.database

import androidx.lifecycle.LiveData

class PostureEventRepository(private val postureEventDao: PostureEventDao) {
    val allEvents: LiveData<List<PostureEvent>> = postureEventDao.getAllEvents()
    val recentEvents: LiveData<List<PostureEvent>> = postureEventDao.getRecentEvents(10)

    suspend fun insert(event: PostureEvent) {
        postureEventDao.insert(event)
    }
}