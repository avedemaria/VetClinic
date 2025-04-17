package com.example.vetclinic.presentation.mainScreen.homeScreen

sealed class HomeState {
    data class Result(val userName: String) : HomeState()
    data class Error(val message: String) : HomeState()
    object Loading: HomeState()
}