package com.example.vetclinic.presentation.screens.mainScreen

import com.example.vetclinic.domain.entities.pet.Pet
import com.example.vetclinic.domain.entities.user.User

sealed class MainState {

    data class Result(
        val user: User?,
        val pets: List<Pet>
    ) : MainState()

    data class Error(val message: String) : MainState()
    object Loading : MainState()
}