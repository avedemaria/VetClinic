package com.example.vetclinic.presentation.screens.loginScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.repository.UserDataStore
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class LoadingViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
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



    fun clearUserSession() {
        _loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            userDataStore.clearUserSession()
        }
    }


}