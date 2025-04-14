package com.example.vetclinic.data.workers

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import jakarta.inject.Inject
import javax.inject.Provider


class AppointmentReminderWorkerFactory @Inject constructor(
    private val workerProviders: @JvmSuppressWildcards Map<Class<out ListenableWorker>,
            Provider<ChildWorkerFactory>>,
) : WorkerFactory() {

    init {
        Log.d("AppointmentReminderWorkerFactory", "WorkerProviders: $workerProviders")
    }
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            AppointmentReminderWorker::class.qualifiedName -> {
                val childWorkerFactory =
                    workerProviders[AppointmentReminderWorker::class.java]?.get()
                return childWorkerFactory?.create(appContext, workerParameters)
            }

            else -> null
        }
    }
}