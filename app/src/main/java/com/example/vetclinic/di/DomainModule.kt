package com.example.vetclinic.di

import com.example.vetclinic.data.DetailedInfo
import com.example.vetclinic.data.repositoryImpl.AppointmentRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.RepositoryImpl
import com.example.vetclinic.data.repositoryImpl.TimeSlotsRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.UserDataStoreImpl
import com.example.vetclinic.domain.AppointmentRepository
import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.TimeSlotsRepository
import com.example.vetclinic.domain.UserDataStore
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {

    @Binds
    fun bindRepository(impl: RepositoryImpl): Repository

    @Binds
    fun bindTimeSlotsRepository(impl: TimeSlotsRepositoryImpl): TimeSlotsRepository

    @Binds
    fun bindAppointmentRepository(impl: AppointmentRepositoryImpl): AppointmentRepository

    @Binds
    fun bindUserDataStore(impl: UserDataStoreImpl): UserDataStore

}