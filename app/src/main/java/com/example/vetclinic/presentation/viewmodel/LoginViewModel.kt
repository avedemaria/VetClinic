package com.example.vetclinic.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.authFeature.LogInUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LogInUserUseCase,
    private val userDataStore: UserDataStore
) :
    ViewModel() {

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

        viewModelScope.launch {
            loginUserUseCase.loginUser(email, password).onSuccess {
                _loginState.value = LoginState.Result(it)
                userDataStore.saveUserSession(it.user?.id ?: "", it.accessToken)
            }
                .onFailure {
                    _loginState.value = LoginState.Error(it.message)
                }

        }
    }


    fun logOutUser() {
        viewModelScope.launch {
            loginUserUseCase.logOut()
            _loginState.value = LoginState.LoggedOut
        }
    }
}


