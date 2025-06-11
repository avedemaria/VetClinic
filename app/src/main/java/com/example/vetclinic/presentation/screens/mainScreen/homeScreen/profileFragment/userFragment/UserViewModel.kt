package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.userFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.entities.user.User
import com.example.vetclinic.domain.usecases.SessionUseCase
import com.example.vetclinic.domain.usecases.UserUseCase
import com.example.vetclinic.presentation.screens.UiEvent
import com.example.vetclinic.utils.FieldValidator
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class UserViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val sessionUseCase: SessionUseCase,
    private val fieldValidator: FieldValidator,

    ) : ViewModel() {

    private val _userState = MutableLiveData<UserUiState>()
    val userState: LiveData<UserUiState> get() = _userState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()


    private var currentUser: User? = null

    init {
        viewModelScope.launch {
            val userId = sessionUseCase.getUserId() ?: return@launch
            getUserData(userId)
        }

    }


    private fun getUserData(userId: String) {

        _userState.value = UserUiState.Loading
        viewModelScope.launch {
            userUseCase.getUserFromRoom(userId)
                .onSuccess { user ->
                    currentUser = user
                    _userState.value = UserUiState.Success(user)
                }
                .onFailure { error ->
                    _userState.value = UserUiState.Error
                    _uiEvent.emit(UiEvent.ShowSnackbar(error.message.toString()))
                }
        }
    }


    fun startEditingField(field: UserUiState.FieldType) {
        _userState.value = UserUiState.EditingField(field)
    }

    fun finishEditing() {
        this@UserViewModel.currentUser?.let {
            _userState.value = UserUiState.Success(it)
        }
    }

    fun updateField(field: String, newValue: String) {

        viewModelScope.launch {

            val currentUser = this@UserViewModel.currentUser

            if (currentUser == null) {
                _uiEvent.emit(UiEvent.ShowSnackbar("Нет данных о пользователе"))
                return@launch
            }

            if (newValue.isBlank()) {
                _uiEvent.emit(UiEvent.ShowSnackbar("Поле не может быть пустым"))
                return@launch
            }


            validateField(field, newValue)?.let {
                _userState.value = UserUiState.Error
                return@launch
            }

            val updatedUser = when (field) {
                UserField.PHONE_NUMBER.name -> currentUser.copy(phoneNumber = newValue)
                else -> {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Неверное поле"))
                    return@launch
                }
            }

            if (updatedUser != currentUser) {
                updateUser(updatedUser)
            }
        }

    }


    fun updateUser(updatedUser: User) {

        _userState.value = UserUiState.Loading

        viewModelScope.launch {
            val userId = sessionUseCase.getUserId() ?: ""
            val updatedResult = userUseCase.updateUserInSupabaseDb(userId, updatedUser)
            if (updatedResult.isSuccess) {
                _userState.value = UserUiState.Success(updatedUser)
            } else {
                _uiEvent.emit(UiEvent.ShowSnackbar("Не получилось обновить данные на сервере"))

            }
        }
    }


    private fun validateField(field: String, value: String): String? {
        return when (field) {
            UserField.PHONE_NUMBER.name -> fieldValidator.validatePhone(value)
            else -> "Неизвестное поле"
        }
    }


}

enum class UserField {
    PHONE_NUMBER
}