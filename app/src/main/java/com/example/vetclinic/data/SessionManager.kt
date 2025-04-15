package com.example.vetclinic.data

import android.content.Context
import android.util.Log
import androidx.work.WorkManager
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val context: Context
) {


     fun onLogin(role: String) {
        when (role.lowercase()) {
            "admin" -> {
                cancelReminderWorkers()
                Log.d("SessionManager", "Admin login detected, reminders cancelled")
            }

            "user" -> {
                Log.d("SessionManager", "User login detected, reminders allowed")
            }

            else -> {
                Log.d("SessionManager", "Unknown role: $role, no action taken")
            }
        }
    }

    private fun cancelReminderWorkers() {
        WorkManager.getInstance(context).cancelAllWorkByTag("reminders")
    }


}