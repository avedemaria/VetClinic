package com.example.vetclinic.presentation.screens

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
}