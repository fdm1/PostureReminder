package com.frankmassi.posturereminder

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log

/**
 * Service to handle callbacks from the JobScheduler. Requests scheduled with the JobScheduler
 * ultimately land on this service's "onStartJob" method. It runs jobs for a specific amount of time
 * and finishes them. It keeps the activity updated with changes via a Messenger.
 */
class ReminderJobSchedulerService : JobService() {

    private var notificationHelper: ReminderNotificationHelper? = null

    /**
     * When the app's MainActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onStartJob(params: JobParameters): Boolean {
        notificationHelper = ReminderNotificationHelper(this)
        val notification1 = notificationHelper?.getNotification(
            getString(R.string.notification_title), getString(
                R.string.notification_body
            )
        )
        if (notification1 != null) notificationHelper?.notify(1, notification1)
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        // Stop tracking these job parameters, as we've 'finished' executing.
        return false
    }
}
