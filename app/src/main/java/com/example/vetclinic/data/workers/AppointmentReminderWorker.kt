package com.example.vetclinic.data.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.vetclinic.R
import jakarta.inject.Inject
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppointmentReminderWorker(
    private val appContext: Context, workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {

        val doctorName = inputData.getString(DOCTOR_NAME) ?: return Result.failure()
        val serviceName = inputData.getString(SERVICE_NAME) ?: return Result.failure()
        val petName = inputData.getString(PET_NANE) ?: return Result.failure()
        val appointmentId = inputData.getString(APPOINTMENT_ID) ?: return Result.failure()
        val appointmentDateTimeString =
            inputData.getString("appointmentDateTime") ?: return Result.failure()

        val appointmentDateTime =
            parseAppointmentDateTime(appointmentDateTimeString) // твоя функция
        val reminderTime = appointmentDateTime.minusHours(1)
        val now = LocalDateTime.now()

        if (now.isAfter(reminderTime.plus(Duration.ofMinutes(1)))) {
            Log.d("ReminderWorker", "It's too late for reminder — $appointmentId")
            return Result.success()
        }


        createNotificationChannel(appContext)
        sendReminderNotification(petName, doctorName, serviceName, appointmentId)
        Log.d("AppointmentReminderWorker", "Notification sent for appointment: $appointmentId")

        return Result.success()
    }


    private fun sendReminderNotification(
        petName: String,
        doctorName: String,
        serviceName: String,
        appointmentId: String,
    ) {
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.modern_pet_veterinary_clinic).setContentTitle(TITLE)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "Прием состоится через час!\nВрач: $doctorName\nУслуга:" +
                                " $serviceName\nПитомец: $petName"
                    )
            ).setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()


        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(appointmentId.hashCode(), notification)
        Log.d("AppointmentReminderWorker", "notification was sent for ${appointmentId.hashCode()}")
    }


    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = CHANNEL_ID
            val channelName = context.getString(R.string.appointment_notifications)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val notificationChannel = NotificationChannel(channelId, channelName, importance)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


    private fun parseAppointmentDateTime(datetimeStr: String): LocalDateTime {
        return LocalDateTime.parse(datetimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    class Factory @Inject constructor() : ChildWorkerFactory {
        override fun create(
            context: Context,
            workerParameters: WorkerParameters,
        ): ListenableWorker {
            return AppointmentReminderWorker(context, workerParameters)
        }
    }


    companion object {
        private const val CHANNEL_ID = "appointment_channel"
        private const val TITLE = "Напоминание о приёме"
        private const val DOCTOR_NAME = "doctorName"
        private const val SERVICE_NAME = "serviceName"
        private const val PET_NANE = "petName"
        private const val APPOINTMENT_ID = "appointmentId"
    }
}