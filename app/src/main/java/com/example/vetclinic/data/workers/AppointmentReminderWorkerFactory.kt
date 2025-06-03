package com.example.vetclinic.data.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import jakarta.inject.Inject
import timber.log.Timber
import javax.inject.Provider


class AppointmentReminderWorkerFactory @Inject constructor(
    private val workerProviders: @JvmSuppressWildcards Map<Class<out ListenableWorker>,
            Provider<ChildWorkerFactory>>,
) : WorkerFactory() {

    init {
        Timber.tag(TAG).d("WorkerProviders: $workerProviders")
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


    companion object {
        private const val TAG = "AppointmentReminderWorkerFactory"
    }
}