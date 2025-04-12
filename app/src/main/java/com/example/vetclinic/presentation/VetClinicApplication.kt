package com.example.vetclinic.presentation

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.example.vetclinic.di.DaggerAppComponent
import javax.inject.Inject

class VetClinicApplication : Application(), Configuration.Provider {

    val component by lazy {
        DaggerAppComponent.factory().create(this)
    }

    @Inject
    lateinit var workerFactory: WorkerFactory


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG).setWorkerFactory(workerFactory).build()


    override fun onCreate() {
        super.onCreate()
        component.inject(this)

        if (!WorkManager.isInitialized()) {
            // Инициализируем WorkManager вручную
            WorkManager.initialize(this, workManagerConfiguration)
        }
    }

}