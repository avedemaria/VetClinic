package com.example.vetclinic.di

import android.app.Application
import com.example.vetclinic.presentation.MainActivity
import com.example.vetclinic.presentation.fragment.DetailedDoctorInfoFragment
import com.example.vetclinic.presentation.fragment.DoctorsFragment
import com.example.vetclinic.presentation.fragment.LoginFragment
import com.example.vetclinic.presentation.fragment.MainFragment
import com.example.vetclinic.presentation.fragment.PlainServicesFragment
import com.example.vetclinic.presentation.fragment.RegistrationFragment
import com.example.vetclinic.presentation.fragment.SelectionFragment
import com.example.vetclinic.presentation.fragment.ServicesWithDepFragment
import dagger.BindsInstance
import dagger.Component
import jakarta.inject.Singleton

@Singleton
@Component(modules = [DataModule::class, DomainModule::class, ViewModelModule::class])
interface AppComponent {


    fun inject(activity: MainActivity)

//    fun inject(activity: MainScreenActivity)

    fun inject(mainFragment: MainFragment)

    fun inject(registrationFragment: RegistrationFragment)

    fun inject(loginFragment: LoginFragment)

    fun inject(selectionFragment: SelectionFragment)

    fun inject(doctorsFragment: DoctorsFragment)

    fun inject(detailedDoctorInfoFragment: DetailedDoctorInfoFragment)

    fun inject(servicesWithDepFragment: ServicesWithDepFragment)

    fun inject(plainServicesFragment: PlainServicesFragment)


    @Component.Factory
    interface AppComponentFactory {
        fun create(
            @BindsInstance application: Application
        ): AppComponent
    }
}