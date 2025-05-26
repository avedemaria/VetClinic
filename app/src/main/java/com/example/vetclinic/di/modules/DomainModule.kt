package com.example.vetclinic.di.modules

import com.example.vetclinic.data.LocalDataCleanerImpl
import com.example.vetclinic.data.repositoryImpl.AppointmentRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.AuthRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.DepartmentRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.DialogDataStoreImpl
import com.example.vetclinic.data.repositoryImpl.DoctorRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.PetRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.ReminderRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.ServiceRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.TimeSlotsRepositoryImpl
import com.example.vetclinic.data.repositoryImpl.UserDataStoreImpl
import com.example.vetclinic.data.repositoryImpl.UserRepositoryImpl
import com.example.vetclinic.domain.LocalDataCleaner
import com.example.vetclinic.domain.repository.AppointmentRepository
import com.example.vetclinic.domain.repository.AuthRepository
import com.example.vetclinic.domain.repository.DepartmentRepository
import com.example.vetclinic.domain.repository.DialogDataStore
import com.example.vetclinic.domain.repository.DoctorRepository
import com.example.vetclinic.domain.repository.PetRepository
import com.example.vetclinic.domain.repository.ReminderRepository
import com.example.vetclinic.domain.repository.ServiceRepository
import com.example.vetclinic.domain.repository.TimeSlotsRepository
import com.example.vetclinic.domain.repository.UserDataStore
import com.example.vetclinic.domain.repository.UserRepository
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {

    @Binds
    fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    fun bindPetRepository (impl: PetRepositoryImpl): PetRepository

    @Binds
    fun bindDoctorRepository (impl: DoctorRepositoryImpl): DoctorRepository

    @Binds
    fun bindDepartmentRepository (impl: DepartmentRepositoryImpl): DepartmentRepository

    @Binds
    fun bindServiceRepository (impl: ServiceRepositoryImpl): ServiceRepository

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

    @Binds
    fun bindLocalDataCleaner (impl: LocalDataCleanerImpl): LocalDataCleaner



}