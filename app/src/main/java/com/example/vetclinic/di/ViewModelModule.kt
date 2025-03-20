package com.example.vetclinic.di

import androidx.lifecycle.ViewModel
import com.example.vetclinic.presentation.viewmodel.AddPetViewModel
import com.example.vetclinic.presentation.viewmodel.DoctorViewModel
import com.example.vetclinic.presentation.viewmodel.HomeViewModel
import com.example.vetclinic.presentation.viewmodel.LoadingViewModel
import com.example.vetclinic.presentation.viewmodel.LoginViewModel
import com.example.vetclinic.presentation.viewmodel.MainViewModel
import com.example.vetclinic.presentation.viewmodel.PetViewModel
import com.example.vetclinic.presentation.viewmodel.PlainServiceViewModel
import com.example.vetclinic.presentation.viewmodel.RegistrationViewModel
import com.example.vetclinic.presentation.viewmodel.ResetPasswordViewModel
import com.example.vetclinic.presentation.viewmodel.ServiceWithDepViewModel
import com.example.vetclinic.presentation.viewmodel.SettingsViewModel
import com.example.vetclinic.presentation.viewmodel.UserViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module

interface ViewModelModule {

    @IntoMap
    @ViewModelKey(LoadingViewModel::class)
    @Binds
    fun bindLoadingViewModel(impl: LoadingViewModel): ViewModel

    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    @Binds
    fun bindRegistrationViewModel(impl: RegistrationViewModel): ViewModel

    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    @Binds
    fun bindLoginViewModel(impl: LoginViewModel): ViewModel

    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Binds
    fun bindMainViewModel(impl: MainViewModel): ViewModel

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

//    @IntoMap
//    @ViewModelKey(ProfileViewModel::class)
//    @Binds
//    fun bindProfileViewModel(impl: ProfileViewModel): ViewModel

    @IntoMap
    @ViewModelKey(UserViewModel::class)
    @Binds
    fun bindUserViewModel(impl: UserViewModel): ViewModel


    @IntoMap
    @ViewModelKey(PetViewModel::class)
    @Binds
    fun bindPetViewModel(impl: PetViewModel): ViewModel

    @IntoMap
    @ViewModelKey(AddPetViewModel::class)
    @Binds
    fun addPetViewModel(impl: AddPetViewModel): ViewModel


    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    @Binds
    fun bindSettingsViewModel(impl: SettingsViewModel): ViewModel

    @IntoMap
    @ViewModelKey(ResetPasswordViewModel::class)
    @Binds
    fun bindResetPasswordViewModel(impl: ResetPasswordViewModel): ViewModel


}
