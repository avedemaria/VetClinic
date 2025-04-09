package com.example.vetclinic.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.authFeature.ResetPasswordUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _resetPasswordState = MutableLiveData<ResetPasswordState>()
    val resetPasswordState: LiveData<ResetPasswordState> = _resetPasswordState


    fun sendResetLink(email: String) {
        _resetPasswordState.value = ResetPasswordState.Loading

        viewModelScope.launch {
            try {
                resetPasswordUseCase.sendResetLink(email)
                _resetPasswordState.value = ResetPasswordState.Success
            } catch (e: Exception) {
                _resetPasswordState.value = ResetPasswordState.Error(e.message.toString())
            }
        }
    }

    fun updatePassword(newPassword: String, token:String) {
        _resetPasswordState.value = ResetPasswordState.Loading

        viewModelScope.launch {

            if (newPassword.isBlank()) {
                _resetPasswordState.value =
                    ResetPasswordState.Error("Пароль не может быть пустым")
                return@launch
            }

            if (newPassword.length < 6) {
                _resetPasswordState.value =
                    ResetPasswordState.Error("Пароль должен содержать не менее 6 символов")
                return@launch
            }

            val result = resetPasswordUseCase.updatePassword(newPassword, token)
            if (result.isSuccess) {
                _resetPasswordState.value = ResetPasswordState.Success
            } else {
                val errorMessage = result.exceptionOrNull() ?: "Неизвестная ошибка"
                _resetPasswordState.value =
                    ResetPasswordState.Error("Ошибка загрузки: ${errorMessage.toString()}")
            }
        }
    }
}