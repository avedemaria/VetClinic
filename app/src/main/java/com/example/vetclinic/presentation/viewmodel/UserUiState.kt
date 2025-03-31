package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.User

sealed class UserUiState {

    object Loading : UserUiState()
    data class Success(val user: User) : UserUiState()
    data class Error(val message: String) : UserUiState()
    data class EditingField(val field: FieldType): UserUiState()

    enum class FieldType {
         PHONE
    }


}