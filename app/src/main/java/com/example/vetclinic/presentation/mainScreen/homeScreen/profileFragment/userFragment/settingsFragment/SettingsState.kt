package com.example.vetclinic.presentation.mainScreen.homeScreen.profileFragment.userFragment.settingsFragment

sealed class SettingsState {

    data object Loading : SettingsState()
    data object LoggedOut : SettingsState()
    data class Error(val message: String) : SettingsState()
}