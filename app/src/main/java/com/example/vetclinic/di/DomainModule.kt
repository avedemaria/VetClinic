package com.example.vetclinic.di

import com.example.vetclinic.data.RepositoryImpl
import com.example.vetclinic.data.TimeSlotsRepositoryImpl
import com.example.vetclinic.data.UserDataStoreImpl
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
    fun bindTimeSlotsRepository (impl:TimeSlotsRepositoryImpl): TimeSlotsRepository

    @Binds
    fun bindUserDataStore (impl: UserDataStoreImpl): UserDataStore
}