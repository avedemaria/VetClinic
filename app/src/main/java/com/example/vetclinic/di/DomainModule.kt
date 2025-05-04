package com.example.vetclinic.di

import com.example.vetclinic.data.repositoryImpl.AppointmentRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.AuthRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.DialogDataStoreImpl
import com.example.vetclinic.data.repositoryImpl.ReminderRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.RepositoryImpl
import com.example.vetclinic.data.repositoryImpl.TimeSlotsRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.UserDataStoreImpl
import com.example.vetclinic.domain.interfaces.AppointmentRepository
import com.example.vetclinic.domain.interfaces.AuthRepository
import com.example.vetclinic.domain.interfaces.DialogDataStore
import com.example.vetclinic.domain.interfaces.ReminderRepository
import com.example.vetclinic.domain.interfaces.Repository
import com.example.vetclinic.domain.interfaces.TimeSlotsRepository
import com.example.vetclinic.domain.interfaces.UserDataStore
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