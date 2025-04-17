package com.example.vetclinic.presentation.sendResetLinkScreen

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.usecases.ResetPasswordUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class SendResetLinkViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : ViewModel() {

    private val _sendResetLinkState = MutableLiveData<SendResetLinkState>()
    val sendResetLinkState: LiveData<SendResetLinkState> = _sendResetLinkState


    fun sendResetLink(email: String) {
        _sendResetLinkState.value = SendResetLinkState.Loading

        viewModelScope.launch {
            try {
                val error = validateEmail(email)
                if (error != null) {
                    _sendResetLinkState.value = SendResetLinkState.Error(error)
                  return@launch
                }
                    resetPasswordUseCase.sendResetLink(email)
                _sendResetLinkState.value = SendResetLinkState.Success
            } catch (e: Exception) {
                _sendResetLinkState.value = SendResetLinkState.Error(e.message.toString())
            }
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Поле не может быть пустым"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Некорректный email"
            else -> null
        }
    }
}