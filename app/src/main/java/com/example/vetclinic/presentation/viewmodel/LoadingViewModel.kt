package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class LoadingViewModel @Inject constructor(
    userDataStore: UserDataStore
) : ViewModel() {


    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingState

    init {
        viewModelScope.launch {
            val userId = userDataStore.getUserId() ?: ""
            Log.d("LoadingFragment", "userId: $userId")
            _loadingState.value = LoadingState.Result(userId)
        }

    }

}