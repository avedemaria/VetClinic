package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.userFragment

import com.example.vetclinic.domain.entities.user.User

sealed class UserUiState {

    data object Loading : UserUiState()
    data class Success(val user: User) : UserUiState()
    data class Error(val message: String) : UserUiState()
    data class EditingField(val field: FieldType) : UserUiState()

    enum class FieldType {
        PHONE
    }


}