package com.frankmassi.posturereminder

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class ReminderWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    private var notificationHelper: ReminderNotificationHelper? = null
    private val notificationFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd h:mma").withZone(
            ZoneId.systemDefault()
        )

    override fun doWork(): Result {
        notificationHelper = ReminderNotificationHelper(this.applicationContext)
        val body = "${notificationFormatter.format(Instant.now()).toLowerCase(Locale.getDefault())}: " +
               applicationContext.getString(R.string.notification_body)

        val notification =
            notificationHelper?.getNotification(applicationContext.getString(R.string.notification_title), body)

        if (notification != null) notificationHelper?.notify(1, notification)
        return Result.success()
    }
}