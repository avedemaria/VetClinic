package com.example.vetclinic.DI

import com.example.vetclinic.data.RepositoryImpl
import com.example.vetclinic.domain.Repository
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {

    @Binds
    fun bindAuthRepository(impl: RepositoryImpl): Repository
}