package com.example.vetclinic.data.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.vetclinic.R
import jakarta.inject.Inject
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppointmentReminderWorker(
    private val appContext: Context, workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {

        return try {
            val appointmentData = extractAppointmentData() ?: return Result.failure()
            val appointmentDateTimeStr = inputData.getString(DATE_TIME) ?: return Result.failure()

            val appointmentDateTime = parseAppointmentDateTime(appointmentDateTimeStr)
            val now = LocalDateTime.now()

            if (appointmentDateTime.isBefore(now)) {
                Timber.tag(TAG).w("Appointment ${appointmentData.appointmentId} is in the past — skipping notification")
                return Result.success()
            }

            createNotificationChannel(appContext)
            sendReminderNotification(appointmentData)

            Timber.tag(TAG).d("Notification sent for appointment: ${appointmentData.appointmentId}")
            Result.success()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to send reminder notification")
            Result.failure()
        }
    }

    private fun extractAppointmentData(): AppointmentData? {
        val doctorName = inputData.getString(DOCTOR_NAME)
        val serviceName = inputData.getString(SERVICE_NAME)
        val petName = inputData.getString(PET_NAME)
        val appointmentId = inputData.getString(APPOINTMENT_ID)

        return if (doctorName != null && serviceName != null &&
            petName != null && appointmentId != null
        ) {
            AppointmentData(doctorName, serviceName, petName, appointmentId)
        } else {
            Timber.tag(TAG).w("Missing appointment data in worker input")
            null
        }
    }


    private fun sendReminderNotification(
        data: AppointmentData,
    ) {
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.pet_veterinary_logo).setContentTitle(TITLE)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "Прием состоится через час!\nВрач: ${data.doctorName}\nУслуга:" +
                                " ${data.serviceName}\nПитомец: ${data.petName}"
                    )
            ).setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()


        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(data.appointmentId.hashCode(), notification)
        Timber.tag(TAG).d("notification was sent for ${data.appointmentId.hashCode()}")
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



    private fun parseAppointmentDateTime(dateTimeStr: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        return LocalDateTime.parse(dateTimeStr, formatter)
    }


    private data class AppointmentData(
        val doctorName: String,
        val serviceName: String,
        val petName: String,
        val appointmentId: String,
    )


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
        private const val PET_NAME = "petName"
        private const val DATE_TIME = "appointmentDateTime"
        private const val APPOINTMENT_ID = "appointmentId"
        private const val TAG = "ReminderWorker"
    }
}