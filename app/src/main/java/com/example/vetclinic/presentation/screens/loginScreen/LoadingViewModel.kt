package com.example.vetclinic.presentation.screens.loginScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.repository.UserDataStore
import com.example.vetclinic.domain.usecases.SessionUseCase
import com.example.vetclinic.presentation.screens.loginScreen.registrationScreen.RegistrationViewModel
import com.example.vetclinic.presentation.screens.loginScreen.registrationScreen.RegistrationViewModel.Companion
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class LoadingViewModel @Inject constructor(
    private val sessionUseCase: SessionUseCase
) : ViewModel() {


    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingState

    init {
        viewModelScope.launch {

            val userId = withContext(Dispatchers.IO) { sessionUseCase.getUserId().orEmpty() }
            Log.d(TAG, "userId: $userId")
            val userRole = withContext(Dispatchers.IO){sessionUseCase.getUserRole().orEmpty()}
            Log.d(TAG, "userRole: $userRole")
            _loadingState.value = LoadingState.Result(userId, userRole)

        }

    }



    fun clearUserSession() {
        _loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            sessionUseCase.clearSession()
        }
    }


    companion object {
        private const val TAG = "LoadingViewModel"
    }


}