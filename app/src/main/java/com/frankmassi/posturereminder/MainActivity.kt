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

import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.work.*
import com.frankmassi.posturereminder.databinding.ActivityMainBinding
import java.time.Duration
import java.util.concurrent.TimeUnit
import androidx.work.PeriodicWorkRequestBuilder as PeriodicWorkRequestBuilder1

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var workManager: WorkManager

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        workManager = WorkManager.getInstance(this)

        binding.durationTime.text =
            Editable.Factory.getInstance().newEditable(getRecurringMinutesPreference())

        binding.disableButton.setOnClickListener {cancelPeriodicWork(true)}
        binding.enableButton.setOnClickListener {scheduleJob()}
    }

    private fun getRecurringMinutesPreference(): String {
        return PreferenceManager.getDefaultSharedPreferences(this)
            .getInt(
                getString(R.string.RecurringMinutesVariable),
                this.resources.getInteger(R.integer.defaultRecurringMinutes)
            ).toString()
    }

    private fun setRecurringMinutesPreference(value: Int) {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit().putInt(getString(R.string.RecurringMinutesVariable), value).apply()
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

    private fun setRecurringMinutes(): Int {
        var recurringMinutes = binding.durationTime.text.toString().toInt()
        if (recurringMinutes < this.resources.getInteger(R.integer.defaultRecurringMinutes)) {
            recurringMinutes = this.resources.getInteger(R.integer.defaultRecurringMinutes)
            Toast.makeText(this, getString(R.string.min_reminder_exceeded), Toast.LENGTH_SHORT)
                .show()
        }
        binding.durationTime.text =
            Editable.Factory.getInstance().newEditable(recurringMinutes.toString())
        setRecurringMinutesPreference(recurringMinutes)
        return recurringMinutes
    }

    private fun enqueueWork(recurringMinutes: Int) {
        val constraints = Constraints.Builder().build()
        val work = PeriodicWorkRequestBuilder1<ReminderWorker>(
            repeatInterval = Duration.ofMinutes(recurringMinutes.toLong())
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
