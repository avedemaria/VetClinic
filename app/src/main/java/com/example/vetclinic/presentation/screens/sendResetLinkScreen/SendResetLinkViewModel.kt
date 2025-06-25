package com.example.vetclinic.presentation.screens.sendResetLinkScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.usecases.ResetPasswordUseCase
import com.example.vetclinic.presentation.screens.UiEvent
import com.example.vetclinic.utils.FieldValidator
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SendResetLinkViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val fieldValidator: FieldValidator,
) : ViewModel() {

    private val _sendResetLinkState = MutableLiveData<SendResetLinkState>()
    val sendResetLinkState: LiveData<SendResetLinkState> = _sendResetLinkState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()


    fun sendResetLink(email: String) {
        _sendResetLinkState.value = SendResetLinkState.Loading

        viewModelScope.launch {
            try {
                val error = validateEmail(email)
                if (error != null) {
                    _sendResetLinkState.value = SendResetLinkState.Error
                    _uiEvent.emit(UiEvent.ShowSnackbar(error))
                    return@launch
                }
                resetPasswordUseCase.sendResetLink(email)
                _sendResetLinkState.value = SendResetLinkState.Success
                _uiEvent.emit(
                    UiEvent.ShowSnackbar(
                        "Ссылка для восстановления пароля " +
                                "была отправлена на ваш email"
                    )
                )
            } catch (e: Exception) {
                _sendResetLinkState.value = SendResetLinkState.Error
                _uiEvent.emit(UiEvent.ShowSnackbar(e.message.toString()))
            }
        }
    }


    private fun validateEmail(email: String): String? {
        if (email.isBlank()) return "Поле не может быть пустым"
        return fieldValidator.validateEmail(email)
    }
}