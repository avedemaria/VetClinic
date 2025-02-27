package com.example.vetclinic.di

import androidx.lifecycle.ViewModel
import com.example.vetclinic.presentation.viewmodel.DoctorViewModel
import com.example.vetclinic.presentation.viewmodel.LoginViewModel
import com.example.vetclinic.presentation.viewmodel.MainSharedViewModel
import com.example.vetclinic.presentation.viewmodel.PlainServiceViewModel
import com.example.vetclinic.presentation.viewmodel.RegistrationViewModel
import com.example.vetclinic.presentation.viewmodel.ServiceWithDepViewModel
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
    @ViewModelKey(MainSharedViewModel::class)
    @Binds
    fun bindMainSharedViewModel(impl: MainSharedViewModel): ViewModel

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


}
