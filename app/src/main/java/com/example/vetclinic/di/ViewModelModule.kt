package com.example.vetclinic.di

import androidx.lifecycle.ViewModel
import com.example.vetclinic.presentation.viewmodel.DoctorViewModel
import com.example.vetclinic.presentation.viewmodel.LoginViewModel
import com.example.vetclinic.presentation.viewmodel.RegistrationViewModel
import dagger.Binds
import dagger.Module
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
    @ViewModelKey(DoctorViewModel::class)
    @Binds
    fun bindDoctorViewModel(impl: DoctorViewModel): ViewModel



}
