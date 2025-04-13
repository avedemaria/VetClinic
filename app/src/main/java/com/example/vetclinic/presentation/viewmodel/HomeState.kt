package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.User

sealed class HomeState {
    data class Result(val userName: String) : HomeState()
    data class Error(val message: String) : HomeState()
    object Loading: HomeState()
}