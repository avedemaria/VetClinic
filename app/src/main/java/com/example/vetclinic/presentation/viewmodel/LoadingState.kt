package com.example.vetclinic.presentation.viewmodel

sealed class LoadingState {
    data class Result(val userId: String, val userRole:String) : LoadingState()
    data class Error(val message: String) : LoadingState()
    object Loading : LoadingState()
}