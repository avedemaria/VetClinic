package com.example.vetclinic.di.modules

import com.example.vetclinic.data.dataSourceImpl.localSourceImpl.PetLocalDataSourceImpl
import com.example.vetclinic.data.dataSourceImpl.localSourceImpl.UserLocalDataSourceImpl
import com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl.PetRemoteDataSourceImpl
import com.example.vetclinic.data.dataSourceImpl.remoteSourceImpl.UserRemoteDataSourceImpl
import com.example.vetclinic.data.localSource.interfaces.PetLocalDataSource
import com.example.vetclinic.data.localSource.interfaces.UserLocalDataSource
import com.example.vetclinic.data.remoteSource.interfaces.PetRemoteDataSource
import com.example.vetclinic.data.remoteSource.interfaces.UserRemoteDataSource
import dagger.Binds

interface DataSourceModule {

    @Binds
    fun bindUserRemoteDataSource(impl: UserRemoteDataSourceImpl): UserRemoteDataSource

    @Binds
    fun bindUserLocalDataSource(impl: UserLocalDataSourceImpl): UserLocalDataSource

    @Binds
    fun bindPetRemoteDataSource (impl:PetRemoteDataSourceImpl): PetRemoteDataSource

    @Binds
    fun bindPetLocalDataSource (impl: PetLocalDataSourceImpl): PetLocalDataSource
}