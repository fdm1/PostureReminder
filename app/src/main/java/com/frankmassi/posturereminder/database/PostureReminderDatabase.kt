package com.frankmassi.posturereminder.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(PostureEvent::class), version = 1)
@TypeConverters(Converters::class)
public abstract class PostureReminderDatabase : RoomDatabase() {

    abstract fun postureEventDao(): PostureEventDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: PostureReminderDatabase? = null

        fun getDatabase(context: Context): PostureReminderDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PostureReminderDatabase::class.java,
                    "posture_reminder_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

