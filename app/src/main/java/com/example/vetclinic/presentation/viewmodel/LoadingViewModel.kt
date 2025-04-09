package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.usecases.GetPetsUseCase
import com.example.vetclinic.domain.usecases.GetUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class LoadingViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val getUserUseCase: GetUserUseCase,
    private val getPetsUseCase: GetPetsUseCase,
) : ViewModel() {


    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingState

    init {
        viewModelScope.launch {
            val userId = userDataStore.getUserId() ?: ""
            Log.d("LoadingFragment", "userId: $userId")
            val userRole = userDataStore.getUserRole() ?: ""
            Log.d("LoadingFragment", "userRole: $userRole")
            _loadingState.value = LoadingState.Result(userId, userRole)

        }

    }


    fun addPetAndUserToRoomForAdmin(userId: String) {
        _loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            getUserUseCase.getUserFromSupabaseDb(userId)

            getPetsUseCase.getPetsFromSupabaseDb(userId)
        }
    }


    fun clearUserSession() {
        _loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            userDataStore.clearUserSession()
        }
    }


}