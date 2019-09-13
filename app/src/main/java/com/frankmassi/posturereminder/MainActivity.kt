/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.frankmassi.posturereminder

import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Messenger
import android.util.Log
import android.widget.*
import java.util.concurrent.TimeUnit

/**
 * Schedules and configures jobs to be executed by a [JobScheduler].
 *
 * [MyJobService] can send messages to this via a [Messenger]
 * that is sent in the Intent that starts the Service.
 */
class MainActivity : Activity() {

    lateinit private var durationTimeEditText: EditText

    // Handler for incoming messages from the service.
    lateinit private var serviceComponent: ComponentName
    lateinit private var notificationHelper: ReminderNotificationHelper
    private var jobId = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        durationTimeEditText = findViewById(R.id.duration_time)

        notificationHelper = ReminderNotificationHelper(this)
        serviceComponent = ComponentName(this, ReminderJobSchedulerService::class.java)

        findViewById<Button>(R.id.disable_button).setOnClickListener { cancelAllJobs() }
        findViewById<Button>(R.id.enable_button).setOnClickListener { scheduleJob() }
    }

    override fun onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        stopService(Intent(this, ReminderJobSchedulerService::class.java))
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        // Start service and provide it a way to communicate with this class.
        val startServiceIntent = Intent(this, ReminderJobSchedulerService::class.java)
        startService(startServiceIntent)
    }

    /**
     * Executed when user clicks on SCHEDULE JOB.
     */
    private fun scheduleJob() {
        val builder = JobInfo.Builder(jobId++, serviceComponent)

        var recurringMinutes = durationTimeEditText.text.toString()
        if (recurringMinutes.isEmpty() || recurringMinutes.toInt() < 15) {
            recurringMinutes = "15"
            Toast.makeText(this, getString(R.string.min_reminder_exceeded), Toast.LENGTH_SHORT)
                .show()
            durationTimeEditText.setText(recurringMinutes)
        }
        builder.setPeriodic(recurringMinutes.toLong() * TimeUnit.MINUTES.toMillis(1))


        // Schedule job
        Toast.makeText(
            this,
            "Reminder notifications will occur every ${recurringMinutes} minutes",
            Toast.LENGTH_SHORT
        ).show()
        Log.d(TAG, getString(R.string.reminders_enabled))
        (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).schedule(builder.build())
    }

    /**
     * Executed when user clicks on CANCEL ALL.
     */
    private fun cancelAllJobs() {
        (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).cancelAll()
        Log.d(TAG, getString(R.string.reminders_disabled))
        showToast(getString(R.string.reminders_disabled))
    }

    companion object {
        private val TAG = "MainActivity"
    }
}
