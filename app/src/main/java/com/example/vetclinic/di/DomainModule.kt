package com.example.vetclinic.di

import com.example.vetclinic.data.repositoryImpl.AppointmentRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.DialogDataStoreImpl
import com.example.vetclinic.data.repositoryImpl.ReminderRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.RepositoryImpl
import com.example.vetclinic.data.repositoryImpl.TimeSlotsRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.UserDataStoreImpl
import com.example.vetclinic.domain.AppointmentRepository
import com.example.vetclinic.domain.DialogDataStore
import com.example.vetclinic.domain.ReminderRepository
import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.TimeSlotsRepository
import com.example.vetclinic.domain.UserDataStore
import dagger.Binds
import dagger.Module
import jakarta.inject.Singleton

@Module
interface DomainModule {

    @Binds
    fun bindRepository(impl: RepositoryImpl): Repository

    @Binds
    fun bindTimeSlotsRepository(impl: TimeSlotsRepositoryImpl): TimeSlotsRepository

    @Binds
    fun bindAppointmentRepository(impl: AppointmentRepositoryImpl): AppointmentRepository

    @Binds
    fun bindReminderRepository(impl: ReminderRepositoryImpl): ReminderRepository

    @Binds
    fun bindUserDataStore(impl: UserDataStoreImpl): UserDataStore

    @Binds
    fun bindDialogDataStore(impl: DialogDataStoreImpl): DialogDataStore

}