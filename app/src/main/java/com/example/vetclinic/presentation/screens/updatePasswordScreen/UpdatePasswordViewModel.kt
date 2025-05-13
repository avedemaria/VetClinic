package com.example.vetclinic.presentation.screens.updatePasswordScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.repository.UserDataStore
import com.example.vetclinic.domain.usecases.ResetPasswordUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class UpdatePasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {




    private val _updatePasswordState = MutableLiveData<UpdatePasswordState>()
    val updatePasswordState: LiveData<UpdatePasswordState> = _updatePasswordState

//    private val token: String =
//        savedStateHandle[TOKEN] ?: throw IllegalArgumentException("Token is missing")
//
//    private val refreshToken: String = savedStateHandle[REFRESH_TOKEN]
//        ?: throw IllegalArgumentException("Refresh token is missing")


    fun updatePassword(newPassword: String) {
        _updatePasswordState.value = UpdatePasswordState.Loading

        viewModelScope.launch {

            if (newPassword.isBlank()) {
                _updatePasswordState.value =
                    UpdatePasswordState.Error("Пароль не может быть пустым")
                return@launch
            }

            if (newPassword.length < 6) {
                _updatePasswordState.value =
                    UpdatePasswordState.Error("Пароль должен содержать не менее 6 символов")
                return@launch
            }

            val token = userDataStore.getAccessToken()
            val refreshToken = userDataStore.getRefreshToken()

            if (token.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                _updatePasswordState.value = UpdatePasswordState.Error("Токены не найдены")
                return@launch
            }

            val result = resetPasswordUseCase.updatePassword(newPassword, token, refreshToken)
            if (result.isSuccess) {
                _updatePasswordState.value = UpdatePasswordState.Success
            } else {
                val errorMessage = result.exceptionOrNull() ?: "Неизвестная ошибка"
                _updatePasswordState.value =
                    UpdatePasswordState.Error("Ошибка загрузки: ${errorMessage.toString()}")
            }
        }
    }



    companion object {
        private const val TOKEN = "token"
        private const val REFRESH_TOKEN = "refresh_token"
    }
}