package com.example.vetclinic.di

import androidx.work.WorkerFactory
import com.example.vetclinic.data.workers.AppointmentReminderWorker
import com.example.vetclinic.data.workers.AppointmentReminderWorkerFactory
import com.example.vetclinic.data.workers.ChildWorkerFactory
import com.example.vetclinic.di.keys.WorkerKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface WorkerBindsModule {

    @Binds
    fun bindWorkerFactory(factory: AppointmentReminderWorkerFactory): WorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(AppointmentReminderWorker::class)
    fun bindAppointmentReminderWorkerFactory(impl: AppointmentReminderWorker.Factory):
            ChildWorkerFactory


}