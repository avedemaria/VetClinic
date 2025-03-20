package com.example.vetclinic.presentation.viewmodel

import com.example.vetclinic.domain.entities.Pet

sealed class SettingsState {

    object Loading : SettingsState()
    object LoggedOut : SettingsState()
    data class Error(val message: String) : SettingsState()
}