package com.example.vetclinic.di

import androidx.lifecycle.ViewModel
import com.example.vetclinic.presentation.viewmodel.DoctorViewModel
import com.example.vetclinic.presentation.viewmodel.LoginViewModel
import com.example.vetclinic.presentation.viewmodel.HomeViewModel
import com.example.vetclinic.presentation.viewmodel.PetViewModel
import com.example.vetclinic.presentation.viewmodel.PlainServiceViewModel
import com.example.vetclinic.presentation.viewmodel.ProfileViewModel
import com.example.vetclinic.presentation.viewmodel.RegistrationViewModel
import com.example.vetclinic.presentation.viewmodel.ServiceWithDepViewModel
import com.example.vetclinic.presentation.viewmodel.UserViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module

interface ViewModelModule {

    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    @Binds
    fun bindRegistrationViewModel(impl: RegistrationViewModel): ViewModel

    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    @Binds
    fun bindLoginViewModel(impl: LoginViewModel): ViewModel

    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    @Binds
    fun bindMainSharedViewModel(impl: HomeViewModel): ViewModel

    @IntoMap
    @ViewModelKey(DoctorViewModel::class)
    @Binds
    fun bindDoctorViewModel(impl: DoctorViewModel): ViewModel

    @IntoMap
    @ViewModelKey(ServiceWithDepViewModel::class)
    @Binds
    fun bindServiceViewModel(impl: ServiceWithDepViewModel): ViewModel

    @IntoMap
    @ViewModelKey(PlainServiceViewModel::class)
    @Binds
    fun bindPlainServiceViewModel(impl: PlainServiceViewModel): ViewModel

    @IntoMap
    @ViewModelKey(UserViewModel::class)
    @Binds
    fun bindUserViewModel(impl: UserViewModel): ViewModel


    @IntoMap
    @ViewModelKey(PetViewModel::class)
    @Binds
    fun bindPetViewModel(impl: PetViewModel): ViewModel



}
