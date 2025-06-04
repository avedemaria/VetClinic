package com.example.vetclinic.di.modules

import com.example.vetclinic.data.dataSourceImpl.localSourceImpl.AppointmentLocalSourceImpl
import com.example.vetclinic.data.dataSourceImpl.localSourceImpl.PetLocalDataSourceImpl
import com.example.vetclinic.data.dataSourceImpl.localSourceImpl.UserLocalDataSourceImpl
import com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl.AppointmentRemoteSourceImpl
import com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl.AuthRemoteSourceImpl
import com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl.DepartmentRemoteSourceImpl
import com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl.DoctorRemoteSourceImpl
import com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl.PetRemoteDataSourceImpl
import com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl.ServiceRemoteSourceImpl
import com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl.UserRemoteDataSourceImpl
import com.example.vetclinic.data.localSource.interfaces.AppointmentLocalSource
import com.example.vetclinic.data.localSource.interfaces.PetLocalDataSource
import com.example.vetclinic.data.localSource.interfaces.UserLocalDataSource
import com.example.vetclinic.data.remoteSource.interfaces.AppointmentRemoteSource
import com.example.vetclinic.data.remoteSource.interfaces.AuthRemoteSource
import com.example.vetclinic.data.remoteSource.interfaces.DepartmentRemoteSource
import com.example.vetclinic.data.remoteSource.interfaces.DoctorRemoteSource
import com.example.vetclinic.data.remoteSource.interfaces.PetRemoteDataSource
import com.example.vetclinic.data.remoteSource.interfaces.ServiceRemoteSource
import com.example.vetclinic.data.remoteSource.interfaces.UserRemoteDataSource
import dagger.Binds
import dagger.Module


@Module
interface DataSourceModule {

    @Binds
    fun bindUserRemoteDataSource(impl: UserRemoteDataSourceImpl): UserRemoteDataSource

    @Binds
    fun bindUserLocalDataSource(impl: UserLocalDataSourceImpl): UserLocalDataSource

    @Binds
    fun bindPetRemoteDataSource(impl: PetRemoteDataSourceImpl): PetRemoteDataSource

    @Binds
    fun bindPetLocalDataSource(impl: PetLocalDataSourceImpl): PetLocalDataSource

    @Binds
    fun bindAppointmentRemoteSource(impl: AppointmentRemoteSourceImpl): AppointmentRemoteSource

    @Binds
    fun bindAppointmentLocalSource(impl: AppointmentLocalSourceImpl): AppointmentLocalSource

    @Binds
    fun bindAuthRemoteSource(impl: AuthRemoteSourceImpl): AuthRemoteSource

    @Binds
    fun bindDepartmentRemoteSource(impl: DepartmentRemoteSourceImpl): DepartmentRemoteSource

    @Binds
    fun bindDoctorRemoteSource(impl: DoctorRemoteSourceImpl): DoctorRemoteSource

    @Binds
    fun bindServiceRemoteSource(impl: ServiceRemoteSourceImpl): ServiceRemoteSource
}