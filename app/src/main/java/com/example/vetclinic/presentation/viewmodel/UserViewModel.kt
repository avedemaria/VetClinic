package com.example.vetclinic.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.entities.User
import com.example.vetclinic.domain.usecases.GetUserUseCase
import com.example.vetclinic.domain.usecases.UpdateUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class UserViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val userDataStore: UserDataStore

) : ViewModel() {

    private val _userState = MutableLiveData<UserUiState>()
    val userState: LiveData<UserUiState> get() = _userState


    private var currentUser: User? = null

    init {
        viewModelScope.launch {
            val userId = userDataStore.getUserId() ?: return@launch
            getUserData(userId)
        }

    }


    private fun getUserData(userId: String) {

        _userState.value = UserUiState.Loading
        viewModelScope.launch {
            getUserUseCase.getUserFromRoom(userId)
                .onSuccess { user ->
                    currentUser = user
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

    fun finishEditing() {
        this@UserViewModel.currentUser?.let {
            _userState.value = UserUiState.Success(it)
        }
    }

    fun updateField(field: String, newValue: String) {

        viewModelScope.launch {

            val currentUser = this@UserViewModel.currentUser

            if (currentUser == null) {
                _userState.value = UserUiState.Error("Нет данных о пользователе")
                return@launch
            }

            if (newValue.isBlank()) {
                _userState.value = UserUiState.Error("Поле не может быть пустым")
                return@launch
            }


            validateField(field, newValue)?.let {
                _userState.value = UserUiState.Error(it)
                return@launch
            }

            val updatedUser = when (field) {
                UserField.PHONE_NUMBER.name -> currentUser.copy(phoneNumber = newValue)
                else -> {
                    _userState.value = UserUiState.Error("Неверное поле")
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
            val userId = userDataStore.getUserId()?:""
            val updatedResult = updateUserUseCase.updateUserInSupabaseDb(userId, updatedUser)

            if (updatedResult.isSuccess) {

                _userState.value = UserUiState.Success(updatedUser)
            } else {
                _userState.value = UserUiState.Error("Не получилось обновить данные на сервере")
            }
        }
    }


    //Валидация


    private fun validateField(field: String, value: String): String? {
        return when (field) {
            UserField.PHONE_NUMBER.name -> validatePhoneNumber(value)
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


}

enum class UserField {
    PHONE_NUMBER
}