package com.example.vetclinic.presentation.viewmodel

sealed class HomeState {
    data class Result(val userName: String) : HomeState()
    data class Error(val message: String) : HomeState()
    object Loading: HomeState()
}