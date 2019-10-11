package com.frankmassi.posturereminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.frankmassi.posturereminder.database.PostureEvent
import com.frankmassi.posturereminder.database.PostureEventRepository
import com.frankmassi.posturereminder.database.PostureReminderDatabase
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class PostureEventViewModel(application: Application) : AndroidViewModel(application){
    private val repository: PostureEventRepository

    val allEvents: LiveData<List<PostureEvent>>
    val recentEvents: LiveData<List<PostureEvent>>

    init {
        val eventDao = PostureReminderDatabase.getDatabase(application).postureEventDao()
        repository = PostureEventRepository(eventDao)
        allEvents = repository.allEvents
        recentEvents = repository.recentEvents
    }

    fun insert(postureEvent: PostureEvent) = viewModelScope.launch {
        repository.insert(postureEvent)
    }

    fun insert(boolean: Boolean) = insert(PostureEvent(0, boolean, OffsetDateTime.now()))
}