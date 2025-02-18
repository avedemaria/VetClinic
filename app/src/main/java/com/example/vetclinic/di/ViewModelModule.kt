package com.example.vetclinic.di

import androidx.lifecycle.ViewModel
import com.example.vetclinic.presentation.viewmodel.RegistrationViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    @Binds
    fun bindDiaryPageViewModel(impl: RegistrationViewModel): ViewModel


}
