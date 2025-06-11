package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.userFragment.settingsFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.repository.UserDataStore
import com.example.vetclinic.domain.usecases.DeleteAccountUseCase
import com.example.vetclinic.domain.usecases.LoginUseCase
import com.example.vetclinic.domain.usecases.SessionUseCase
import com.example.vetclinic.presentation.screens.UiEvent
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel @Inject constructor(
    private val logInUserUseCase: LoginUseCase,
    private val sessionUseCase: SessionUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
) : ViewModel() {

    private val _settingsState = MutableLiveData<SettingsState>()
    val settingsState: LiveData<SettingsState> get() = _settingsState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()


    fun logOut() {
        _settingsState.value = SettingsState.Loading

        viewModelScope.launch {
            val result = logInUserUseCase.logOut()
            if (result.isSuccess) {
                sessionUseCase.clearSession()
                logInUserUseCase.clearAllLocalData()
                _settingsState.value = SettingsState.LoggedOut
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                _settingsState.value = SettingsState.Error
                _uiEvent.emit(UiEvent.ShowSnackbar(errorMessage))
            }
        }
    }


    fun deleteAccount() {
        _settingsState.value = SettingsState.Loading

        viewModelScope.launch {
            val result = deleteAccountUseCase.deleteAccount()
            if (result.isSuccess) {
                sessionUseCase.clearSession()
                logInUserUseCase.clearAllLocalData()
                _settingsState.value = SettingsState.LoggedOut
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                _settingsState.value = SettingsState.Error
                _uiEvent.emit(UiEvent.ShowSnackbar(errorMessage))
            }
        }
    }

}