package com.example.vetclinic.presentation

import android.app.Application
import com.example.vetclinic.di.DaggerAppComponent

class VetClinicApplication : Application() {

    val component by lazy {
        DaggerAppComponent.factory().create(this)
    }
}