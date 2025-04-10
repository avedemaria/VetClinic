package com.example.vetclinic.di

import androidx.work.WorkerFactory
import com.example.vetclinic.data.AppointmentReminderWorker
import com.example.vetclinic.data.AppointmentReminderWorkerFactory
import com.example.vetclinic.data.ChildWorkerFactory
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