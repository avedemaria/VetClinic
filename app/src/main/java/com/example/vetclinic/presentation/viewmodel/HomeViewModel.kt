package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.usecases.GetUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {


    private val _homeState = MutableLiveData<HomeState>()
    val homeState: LiveData<HomeState> get() = _homeState



    fun loadUserName(userId: String) {

        viewModelScope.launch {
            _homeState.value = HomeState.Loading

            getUserUseCase.getUserFromRoom(userId)
                .onSuccess { user ->
                    Log.d("UserLoad", "Loaded user: $user")
                    _homeState.postValue(HomeState.Result(user.userName))
                }
                .onFailure { error ->
                    Log.e("UserLoad", "Error loading user: $error")
                    _homeState.postValue(
                        HomeState.Error(
                            error.message ?: "Unknown error"
                        )
                    )
                }
        }
    }
}


