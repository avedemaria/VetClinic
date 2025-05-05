package com.example.vetclinic.presentation.mainScreen.homeScreen.profileFragment.userFragment.settingsFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.interfaces.UserDataStore
import com.example.vetclinic.domain.usecases.DeleteAccountUseCase
import com.example.vetclinic.domain.usecases.LoginUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class SettingsViewModel @Inject constructor(
    private val logInUserUseCase: LoginUseCase,
    private val userDataStore: UserDataStore,
    private val deleteAccountUseCase: DeleteAccountUseCase,
) : ViewModel() {

    private val _settingsState = MutableLiveData<SettingsState>()
    val settingsState: LiveData<SettingsState> get() = _settingsState


    fun logOut() {
        _settingsState.value = SettingsState.Loading

        viewModelScope.launch {
            val result = logInUserUseCase.logOut()
            if (result.isSuccess) {
                userDataStore.clearUserSession()
                _settingsState.value = SettingsState.LoggedOut
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                _settingsState.value = SettingsState.Error(errorMessage)
            }
        }
    }


    fun deleteAccount() {
        _settingsState.value = SettingsState.Loading

        viewModelScope.launch {
            val result = deleteAccountUseCase.deleteAccount()
            if (result.isSuccess) {
                userDataStore.clearUserSession()
                _settingsState.value = SettingsState.LoggedOut
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                _settingsState.value = SettingsState.Error(errorMessage)
            }
        }
    }

}