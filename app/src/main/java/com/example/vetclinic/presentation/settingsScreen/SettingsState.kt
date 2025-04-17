package com.example.vetclinic.presentation.settingsScreen

sealed class SettingsState {

    data object Loading : SettingsState()
    data object LoggedOut : SettingsState()
    data class Error(val message: String) : SettingsState()
}