package com.example.vetclinic.presentation.screens.updatePasswordScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.usecases.ResetPasswordUseCase
import com.example.vetclinic.domain.usecases.SessionUseCase
import com.example.vetclinic.utils.FieldValidator
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class UpdatePasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val sessionUseCase: SessionUseCase,
    private val fieldValidator: FieldValidator,
) : ViewModel() {


    private val _updatePasswordState = MutableLiveData<UpdatePasswordState>()
    val updatePasswordState: LiveData<UpdatePasswordState> = _updatePasswordState

    fun updatePassword(newPassword: String, confirmPassword:String) {
        _updatePasswordState.value = UpdatePasswordState.Loading

        viewModelScope.launch {

            val isValid = validatePassword(newPassword, confirmPassword)

            if (!isValid) return@launch

            _updatePasswordState.value = UpdatePasswordState.Loading

            val token = sessionUseCase.getAccessToken()
            val refreshToken = sessionUseCase.getRefreshToken()

            resetPasswordUseCase.updatePassword(newPassword, token, refreshToken)
                .onSuccess {
                    _updatePasswordState.value = UpdatePasswordState.Success
                }.onFailure { errorMessage ->
                    _updatePasswordState.value =
                        UpdatePasswordState.Error("Ошибка загрузки: ${errorMessage.message}")
                }
        }
    }


    private fun validatePassword(newPassword: String, confirmPassword: String): Boolean {
        if (newPassword.isBlank()) {
            _updatePasswordState.value =
                UpdatePasswordState.Error("Пароль не может быть пустым")
            return false
        }

        if (confirmPassword.isBlank()) {
            _updatePasswordState.value =
                UpdatePasswordState.Error("Повторите пароль")
            return false
        }

        if (newPassword != confirmPassword) {
            _updatePasswordState.value =
                UpdatePasswordState.Error("Пароли не совпадают")
            return false
        }

        val passwordError = fieldValidator.validatePassword(newPassword)
        if (passwordError != null) {
            _updatePasswordState.value = UpdatePasswordState.Error(passwordError)
            return false
        }

        return true
    }

}