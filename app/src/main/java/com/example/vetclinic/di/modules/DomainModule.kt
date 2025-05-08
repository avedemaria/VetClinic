package com.example.vetclinic.di.modules

import com.example.vetclinic.data.repositoryImpl.AppointmentRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.AuthRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.DialogDataStoreImpl
import com.example.vetclinic.data.repositoryImpl.ReminderRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.RepositoryImpl
import com.example.vetclinic.data.repositoryImpl.TimeSlotsRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.UserDataStoreImpl
import com.example.vetclinic.domain.repository.AppointmentRepository
import com.example.vetclinic.domain.repository.AuthRepository
import com.example.vetclinic.domain.repository.DialogDataStore
import com.example.vetclinic.domain.repository.ReminderRepository
import com.example.vetclinic.domain.repository.Repository
import com.example.vetclinic.domain.repository.TimeSlotsRepository
import com.example.vetclinic.domain.repository.UserDataStore
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {

    @Binds
    fun bindRepository(impl: RepositoryImpl): Repository

    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

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