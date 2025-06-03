package com.example.vetclinic.data.workers

import android.content.Context
import android.util.Log
import androidx.work.WorkManager
import jakarta.inject.Inject
import jakarta.inject.Singleton
import timber.log.Timber

@Singleton
class ReminderManager @Inject constructor(
    private val context: Context,
) {


     fun onLogin(role: String) {
        when (role.lowercase()) {
            "admin" -> {
                cancelReminderWorkers()
                Timber.tag(TAG).d("Admin login detected, reminders cancelled")
            }

            "user" -> {
                Timber.tag(TAG).d("User login detected, reminders allowed")
            }

            else -> {
                Timber.tag(TAG).d("Unknown role: $role, no action taken")
            }
        }
    }

    private fun cancelReminderWorkers() {
        WorkManager.getInstance(context).cancelAllWorkByTag("reminders")
    }


    companion object {
        private const val TAG = "ReminderManager"
    }

}