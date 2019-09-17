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
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.work.*
import java.time.Duration
import java.util.concurrent.TimeUnit
import androidx.work.PeriodicWorkRequestBuilder as PeriodicWorkRequestBuilder1

const val MIN_RECURRANCE_MINUTES = 15L
class MainActivity : Activity() {

    private lateinit var durationTimeEditText: EditText
    private lateinit var workManager: WorkManager

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        durationTimeEditText = findViewById(R.id.duration_time)

        workManager = WorkManager.getInstance(this)

        findViewById<Button>(R.id.disable_button).setOnClickListener { cancelPeriodicWork(true) }
        findViewById<Button>(R.id.enable_button).setOnClickListener { scheduleJob() }
    }

    /**
     * Executed when user clicks on SCHEDULE JOB.
     */
    private fun scheduleJob() {
        cancelPeriodicWork(false)
        val recurringMinutes = setRecurringMinutes()
        enqueueWork(recurringMinutes)

        // Schedule job
        Toast.makeText(
            this,
            "Reminder notifications will occur every $recurringMinutes minutes",
            Toast.LENGTH_SHORT
        ).show()
        Log.d(TAG, getString(R.string.reminders_enabled))
    }

    private fun setRecurringMinutes(): Long {
        var recurringMinutes: Long = durationTimeEditText.text.toString().toLong()
        if (recurringMinutes.toInt() < MIN_RECURRANCE_MINUTES) {
            recurringMinutes = MIN_RECURRANCE_MINUTES
            Toast.makeText(this, getString(R.string.min_reminder_exceeded), Toast.LENGTH_SHORT)
                .show()
            durationTimeEditText.setText(recurringMinutes.toString())
        }
        return recurringMinutes
    }

    private fun enqueueWork(recurringMinutes: Long) {
        val constraints = Constraints.Builder().build()
        val work = PeriodicWorkRequestBuilder1<ReminderWorker>(
            repeatInterval = Duration.ofMinutes(recurringMinutes)
        )
            .setInitialDelay(0, TimeUnit.MINUTES)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .setConstraints(constraints)
            .addTag(this.packageName)
            .build()
        workManager.enqueue(work)
    }

    /**
     * Executed when user clicks on CANCEL ALL.
     */
    private fun cancelPeriodicWork(showToast: Boolean) {
        workManager.cancelAllWorkByTag(this.packageName)
        Log.d(TAG, getString(R.string.reminders_disabled))
        if (showToast) showToast(getString(R.string.reminders_disabled))
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
