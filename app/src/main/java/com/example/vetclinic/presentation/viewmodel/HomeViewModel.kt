package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.usecases.AddUserUseCase
import com.example.vetclinic.domain.usecases.GetUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val addUserUseCase: AddUserUseCase

) : ViewModel() {


    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    private val _homeState = MutableLiveData<HomeState>()
    val homeState: LiveData<HomeState> get() = _homeState


    fun setUserId(id: String) {
        _userId.value = id
        loadUserName(id)
    }


    fun loadUserName(userId: String) {

        viewModelScope.launch {
            _homeState.value = HomeState.Loading

            delay(1000)

            getUserUseCase.getUserFromSupabaseDb(userId)
                .onSuccess { user ->
                    Log.d("HomeViewModel", "Loaded user: $user")
                    if (user != null)
                        _homeState.value = HomeState.Result(user.userName)
                }
                .onFailure { error ->
                    Log.e("HomeViewModel", "Error loading user: $error")
                    _homeState.value = HomeState.Error(error.message ?: "Неизвестная ошибка")


                }
        }
    }
}


