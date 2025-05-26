package com.example.vetclinic.data.workers

import android.content.Context
import android.util.Log
import androidx.work.WorkManager
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ReminderManager @Inject constructor(
    private val context: Context
) {


     fun onLogin(role: String) {
        when (role.lowercase()) {
            "admin" -> {
                cancelReminderWorkers()
                Log.d(TAG, "Admin login detected, reminders cancelled")
            }

            "user" -> {
                Log.d(TAG, "User login detected, reminders allowed")
            }

            else -> {
                Log.d(TAG, "Unknown role: $role, no action taken")
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