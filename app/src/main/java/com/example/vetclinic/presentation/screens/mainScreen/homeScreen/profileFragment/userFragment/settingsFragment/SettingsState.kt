package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.userFragment.settingsFragment

sealed class SettingsState {

    data object Loading : SettingsState()
    data object LoggedOut : SettingsState()
    data object Error : SettingsState()
}