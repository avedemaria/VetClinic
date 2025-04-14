package com.example.vetclinic.data.repositoryImpl

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.vetclinic.data.workers.AppointmentReminderWorker
import com.example.vetclinic.domain.interfaces.ReminderRepository
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import jakarta.inject.Inject
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class ReminderRepositoryImpl @Inject constructor(
    private val context: Context,
) : ReminderRepository {

    override fun scheduleReminder(appointments: List<AppointmentWithDetails>) {

        val now = LocalDateTime.now()


        appointments.forEach { appointment ->

            val appointmentDateTime = parseAppointmentDateTime(appointment.dateTime)
            val reminderTime = appointmentDateTime.minusHours(1)

            if (reminderTime.isAfter(now)) {
                val timeUntilReminder = Duration.between(now, reminderTime)
                val workRequest = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
                    .setInitialDelay(timeUntilReminder.toMillis(), TimeUnit.MILLISECONDS)
                    .setInputData(
                        workDataOf(
                            DOCTOR_NAME to appointment.doctorName,
                            SERVICE_NAME to appointment.serviceName,
                            PET_NANE to appointment.petName,
                            APPOINTMENT_ID to appointment.id
                        )
                    )
                    .build()

                WorkManager.getInstance(context).enqueueUniqueWork(
                    "appointment_${appointment.id}",
                    ExistingWorkPolicy.REPLACE, workRequest
                )
                Log.d("ReminderRepository", "Scheduled reminder for appointment ${appointment.id} in ${timeUntilReminder.toMinutes()} minutes")
            }
        }
    }


    private fun parseAppointmentDateTime(dateTimeStr: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        return LocalDateTime.parse(dateTimeStr, formatter)
    }


    companion object {
        private const val DOCTOR_NAME = "doctorName"
        private const val SERVICE_NAME = "serviceName"
        private const val PET_NANE = "petName"
        private const val APPOINTMENT_ID = "appointmentId"

    }
}