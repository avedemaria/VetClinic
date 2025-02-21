package com.example.vetclinic.di

import android.app.Application
import com.example.vetclinic.presentation.fragment.InfoFragment
import com.example.vetclinic.presentation.fragment.LoginFragment
import com.example.vetclinic.presentation.MainActivity
import com.example.vetclinic.presentation.fragment.DoctorsFragment
import com.example.vetclinic.presentation.fragment.MainFragment
import com.example.vetclinic.presentation.fragment.RegistrationFragment
import com.example.vetclinic.presentation.fragment.SelectionFragment
import dagger.BindsInstance
import dagger.Component
import jakarta.inject.Singleton

@Singleton
@Component(modules = [DataModule::class, DomainModule::class, ViewModelModule::class])
interface AppComponent {


    fun inject(activity: MainActivity)

    fun inject(mainFragment: MainFragment)

    fun inject(registrationFragment: RegistrationFragment)

    fun inject(loginFragment: LoginFragment)

    fun inject(selectionFragment: SelectionFragment)

    fun inject(infoFragment: InfoFragment)

    fun inject(doctorsFragment: DoctorsFragment)


    @Component.Factory
    interface AppComponentFactory {
        fun create(
            @BindsInstance application: Application
        ): AppComponent
    }
}