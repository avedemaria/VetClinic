package com.example.vetclinic.presentation.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.authFeature.LogInUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class SettingsViewModel @Inject constructor(
    private val logInUserUseCase: LogInUserUseCase,
    private val userDataStore: UserDataStore
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

}