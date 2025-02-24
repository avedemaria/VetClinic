package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.User

sealed class SelectionState {
    data class Result(val userName: String) : SelectionState()
    data class Error(val message: String) : SelectionState()
    object Loading: SelectionState()
}