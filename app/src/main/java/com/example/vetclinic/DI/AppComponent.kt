package com.example.vetclinic.DI

import android.app.Application
import com.example.vetclinic.presentation.CreateAccountFragment
import com.example.vetclinic.presentation.InfoFragment
import com.example.vetclinic.presentation.LoginFragment
import com.example.vetclinic.presentation.MainActivity
import com.example.vetclinic.presentation.MainFragment
import com.example.vetclinic.presentation.SelectionFragment
import dagger.BindsInstance
import dagger.Component

@Component(modules = [DataModule::class, DomainModule::class])
interface AppComponent {


    fun inject(activity: MainActivity)

    fun inject(mainFragment: MainFragment)

    fun inject(createAccountFragment: CreateAccountFragment)

    fun inject(loginFragment: LoginFragment)

    fun inject(selectionFragment: SelectionFragment)

    fun inject(infoFragment: InfoFragment)


    @Component.Factory
    interface AppComponentFactory {
        fun create(
            @BindsInstance application: Application
        ): AppComponent
    }
}