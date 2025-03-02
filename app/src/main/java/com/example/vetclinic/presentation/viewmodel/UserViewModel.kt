package com.example.vetclinic.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.usecases.GetUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class UserViewModel @Inject constructor(
   private val getUserUseCase: GetUserUseCase
):ViewModel() {

   private val _userState = MutableLiveData<UserUiState>()
   val userState: LiveData<UserUiState> get() = _userState



   fun getUserFromRoom (userId: String) {

       _userState.value = UserUiState.Loading
       viewModelScope.launch {
          getUserUseCase.getUserFromRoom(userId)
             .onSuccess { user ->
                _userState.value = UserUiState.Success(user)
             }
             .onFailure { error ->
                _userState.value = UserUiState.Error(error.message?:"Неизвестная ошибка")
             }
       }
    }




}