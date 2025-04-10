package com.example.vetclinic.di

import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import com.example.vetclinic.data.AppointmentReminderWorker
import com.example.vetclinic.data.AppointmentReminderWorkerFactory
import com.example.vetclinic.data.ChildWorkerFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider

//@Module
// class WorkerModule {
//
//
//   @Provides
//   fun provideWorkerFactoryMap(
//       appointmentReminderWorkerFactory: ChildWorkerFactory
//   ): Map<Class<out ListenableWorker>, Provider<ChildWorkerFactory>> {
//       return mapOf(AppointmentReminderWorker::class.java to Provider { appointmentReminderWorkerFactory })
//   }
//}