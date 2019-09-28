package com.frankmassi.posturereminder


import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.work.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    private lateinit var workManager: WorkManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = inflater.inflate(R.layout.fragment_settings, container, false)
        workManager = WorkManager.getInstance(this.requireContext())

        binding.duration_time.text =
            Editable.Factory.getInstance().newEditable(getRecurringMinutesPreference())
        binding.disable_button.setOnClickListener {cancelPeriodicWork(true)}
        binding.enable_button.setOnClickListener {scheduleJob()}
        return binding
    }

    private fun getRecurringMinutesPreference(): String {
        return PreferenceManager.getDefaultSharedPreferences(this.context)
            .getInt(
                getString(R.string.RecurringMinutesVariable),
                this.resources.getInteger(R.integer.defaultRecurringMinutes)
            ).toString()
    }

    private fun setRecurringMinutesPreference(value: Int) {
        PreferenceManager.getDefaultSharedPreferences(this.context)
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
            this.context,
            "Reminder notifications will occur every $recurringMinutes minutes",
            Toast.LENGTH_SHORT
        ).show()
        Log.d(MainActivity.TAG, getString(R.string.reminders_enabled))
    }

    private fun setRecurringMinutes(): Int {
        var recurringMinutes = this.duration_time.text.toString().toInt()
        if (recurringMinutes < this.resources.getInteger(R.integer.defaultRecurringMinutes)) {
            recurringMinutes = this.resources.getInteger(R.integer.defaultRecurringMinutes)
            Toast.makeText(this.context, getString(R.string.min_reminder_exceeded), Toast.LENGTH_SHORT)
                .show()
        }
        this.duration_time.text =
            Editable.Factory.getInstance().newEditable(recurringMinutes.toString())
        setRecurringMinutesPreference(recurringMinutes)
        return recurringMinutes
    }

    private fun enqueueWork(recurringMinutes: Int) {
        val constraints = Constraints.Builder().build()
        val work = PeriodicWorkRequestBuilder<ReminderWorker>(
            repeatInterval = Duration.ofMinutes(recurringMinutes.toLong())
        )
            .setInitialDelay(0, TimeUnit.MINUTES)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .setConstraints(constraints)
            .addTag(this.context?.applicationInfo!!.name)
            .build()
        workManager.enqueue(work)
    }

    /**
     * Executed when user clicks on CANCEL ALL.
     */
    private fun cancelPeriodicWork(showToast: Boolean) {
        workManager.cancelAllWorkByTag(this.context?.applicationInfo!!.name)
        Log.d(MainActivity.TAG, getString(R.string.reminders_disabled))
        if (showToast) Toast.makeText(this.context, getString(R.string.reminders_disabled), Toast.LENGTH_SHORT).show()
    }

}
