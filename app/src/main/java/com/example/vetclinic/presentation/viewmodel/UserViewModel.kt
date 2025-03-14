package com.example.vetclinic.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.entities.User
import com.example.vetclinic.domain.usecases.GetUserUseCase
import com.example.vetclinic.domain.usecases.UpdateUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class UserViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private val _userState = MutableLiveData<UserUiState>()
    val userState: LiveData<UserUiState> get() = _userState


    fun getUserFromRoom(userId: String) {

        _userState.value = UserUiState.Loading
        viewModelScope.launch {
            getUserUseCase.getUserFromSupabaseDb(userId)
                .onSuccess { user ->
                    if (user!=null)
                    _userState.value = UserUiState.Success(user)
                }
                .onFailure { error ->
                    _userState.value = UserUiState.Error(error.message ?: "Неизвестная ошибка")
                }
        }
    }


    fun startEditingField(field: UserUiState.FieldType) {
        _userState.value = UserUiState.EditingField(field)
    }

    fun updateField(userId: String, field: String, newValue: String) {

        val currentUser = (userState.value as? UserUiState.Success)?.user

        if (currentUser == null) {
            _userState.value = UserUiState.Error("Нет данных о пользователе")
            return
        }


        if (newValue.isBlank()) {
            _userState.value = UserUiState.Error("Поле не может быть пустым")
            return
        }


        // Валидация данных
        val validationResult = validateField(field, newValue)
        if (validationResult != null) {
            _userState.value = UserUiState.Error(validationResult)
            return
        }

        // Обновляем пользователя
        val updatedUser = when (field) {
            UserField.PHONE_NUMBER.name -> currentUser.copy(phoneNumber = newValue)
            UserField.USER_NAME.name -> currentUser.copy(userName = newValue)
            else -> {
                _userState.value = UserUiState.Error("Неверное поле")
                return
            }
        }

        if (updatedUser != currentUser) {
            updateUserInSupabase(userId, updatedUser)

            updateUserInRoom(updatedUser)

        }
    }


    private fun updateUserInSupabase(userId: String, updatedUser: User) {
        _userState.value = UserUiState.Loading

        viewModelScope.launch {
            val updatedResult = updateUserUseCase.updateUserInSupabaseDb(userId, updatedUser)

            if (updatedResult.isSuccess) {
                _userState.value = UserUiState.Success(updatedUser)
            } else {
                _userState.value = UserUiState.Error("Не получилось обновить данные на сервере")
            }
        }
    }


    private fun updateUserInRoom(user: User) {
        viewModelScope.launch {
            val result = updateUserUseCase.updateUserInRoom(user)

            if (result.isSuccess) {
                _userState.value = UserUiState.Success(user)
            } else {
                _userState.value = UserUiState.Error("Не получилось обновить данные")
            }
        }

    }


    //Валидация


    fun validateField(field: String, value: String): String? {
        return when (field) {
            UserField.PHONE_NUMBER.name -> validatePhoneNumber(value)
            UserField.USER_NAME.name -> validateUserName(value)
            else -> "Неизвестное поле"
        }
    }


    private fun validatePhoneNumber(phone: String): String? {
        val phonePattern = "^\\+?\\d{10,15}$"
        return if (phone.matches(phonePattern.toRegex())) {
            null
        } else {
            "Неверный формат номера телефона"
        }
    }

    private fun validateUserName(name: String): String? {
        return if (name.isNotBlank()) {
            null
        } else {
            "Имя не может быть пустым"
        }
    }

}

enum class UserField {
    PHONE_NUMBER,
    USER_NAME

}