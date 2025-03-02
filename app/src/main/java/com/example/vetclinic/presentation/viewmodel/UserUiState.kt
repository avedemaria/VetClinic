package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.DepartmentWithDoctors
import com.example.vetclinic.domain.entities.User

sealed class UserUiState {

    object Loading : UserUiState()
    data class Success(val user: User) : UserUiState()
    data class Error(val message: String) : UserUiState()


}