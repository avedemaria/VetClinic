package com.example.vetclinic.presentation.loginScreen

sealed class LoadingState {
    data class Result(val userId: String, val userRole:String) : LoadingState()
    data class Error(val message: String) : LoadingState()
    data object Loading : LoadingState()
}