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

class MainSharedViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {


    private val _selectionState = MutableLiveData<SelectionState>()
    val selectionState: LiveData<SelectionState> get() = _selectionState



    fun loadUserName(userId: String) {

        viewModelScope.launch {
            _selectionState.value = SelectionState.Loading
                delay(2000)
            getUserUseCase.getUserFromRoom(userId)
                .onSuccess { user ->
                    Log.d("UserLoad", "Loaded user: $user")
                    _selectionState.postValue(SelectionState.Result(user.userName))
                }
                .onFailure { error ->
                    Log.e("UserLoad", "Error loading user: $error")
                    _selectionState.postValue(
                        SelectionState.Error(
                            error.message ?: "Unknown error"
                        )
                    )
                }
        }
    }
}


