package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.User

sealed class MainState {

    data class Result(val user: User, val pets: List<Pet>) : MainState()
    data class Error(val message: String) : MainState()
    object Loading : MainState()
}