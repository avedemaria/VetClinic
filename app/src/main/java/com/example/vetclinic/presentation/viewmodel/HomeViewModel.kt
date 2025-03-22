package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.usecases.GetUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val userDataStore: UserDataStore

) : ViewModel() {

    private val _homeState = MutableLiveData<HomeState>()
    val homeState: LiveData<HomeState> get() = _homeState


    init {
        getUserIdAndLoadUserName()
    }


    fun getUserIdAndLoadUserName() {
        viewModelScope.launch {
//            userDataStore.userIdFlow.collect { userId ->
            val userId =  userDataStore.getUserId()
                Log.d(TAG, "received userId: $userId")
                if (!userId.isNullOrEmpty()) {
                    loadUserName(userId)
                } else {
                    _homeState.value = HomeState.Error("User not found")
                }
            }
        }

    private fun loadUserName(userId: String) {
        _homeState.value = HomeState.Loading
        viewModelScope.launch {
            getUserUseCase.getUserFromRoom(userId)
                .onSuccess { user ->
                    Log.d(TAG, "Loaded user: $user")
                    _homeState.value = HomeState.Result(user.userName)
                }
                .onFailure { error ->
                    Log.e(TAG, "Error loading user: $error")
                    _homeState.value = HomeState.Error(error.message ?: "Неизвестная ошибка")
                }
        }
    }



    companion object {
        private const val TAG = "HomeViewModel"
    }

    }






