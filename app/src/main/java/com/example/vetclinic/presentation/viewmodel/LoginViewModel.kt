package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.authFeature.LogInUserUseCase
import com.example.vetclinic.domain.usecases.GetPetsUseCase
import com.example.vetclinic.domain.usecases.GetUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LogInUserUseCase,
    private val userDataStore: UserDataStore,

    private val getUserUseCase: GetUserUseCase,
    private val getPetsUseCase: GetPetsUseCase,
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> get() = _loginState


    init {
        checkUserSession()
    }


    fun checkUserSession() {
        viewModelScope.launch {
            val isAuthenticated = loginUserUseCase.checkUserSession()
            _loginState.value = if (isAuthenticated) {
                LoginState.IsAuthenticated
            } else {
                LoginState.LoggedOut
            }
        }
    }

    fun loginUser(email: String, password: String) {

        //TODO: Loading in button
        viewModelScope.launch {
            loginUserUseCase.loginUser(email, password).onSuccess {

                userDataStore.saveUserSession(it.user?.id ?: "", it.accessToken)

                it.user?.id?.let { userId ->
                    getUserUseCase.getUserFromSupabaseDb(userId)
                    getPetsUseCase.getPetsFromSupabaseDb(userId)
                }

                Log.d("LoginViewModel", "Saving userId: ${it.user?.id}")

                // Avoid delay and implement like sync suspend methods
                delay(1500)
                _loginState.value = LoginState.Result(it)
            }.onFailure {
                _loginState.value = LoginState.Error(it.message)
            }
        }
    }


}


