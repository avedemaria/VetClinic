package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.data.SessionManager
import com.example.vetclinic.domain.interfaces.UserDataStore
import com.example.vetclinic.domain.authFeature.LogInUserUseCase
import com.example.vetclinic.domain.usecases.GetAppointmentUseCase
import com.example.vetclinic.domain.usecases.GetPetsUseCase
import com.example.vetclinic.domain.usecases.GetUserUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LogInUserUseCase,
    private val userDataStore: UserDataStore,
    private val getUserUseCase: GetUserUseCase,
    private val getPetsUseCase: GetPetsUseCase,
    private val getAppointmentUseCase: GetAppointmentUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> get() = _loginState


    private var userRole: String? = null


    fun loginUser(email: String, password: String) {


        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            loginUserUseCase.loginUser(email, password).onSuccess {

                userDataStore.saveUserSession(it.user?.id ?: "", it.accessToken)

                Log.d(TAG, "Saving userId: ${it.user?.id}, token: ${it.accessToken}")


                it.user?.id?.let { userId ->
                    val userResult = getUserUseCase.getUserFromSupabaseDb(userId)

                    if (userResult.isSuccess) {
                        userRole = userResult.getOrNull()?.role
                        Log.d(TAG, "saved user role: $userRole")
                        userDataStore.saveUserRole(userRole ?: "")


                        sessionManager.onLogin(userRole ?: "")

                        when (userRole) {
                            USER -> {
                                getPetsUseCase.getPetsFromSupabaseDb(userId)
                            }

                            ADMIN -> {
                                getAppointmentUseCase.getAppointmentsByDate(
                                    LocalDate.now().toString()
                                )
                            }

                            else -> {
                                _loginState.value = LoginState.Error("Неизвестная роль")
                                return@launch
                            }
                        }
                        _loginState.value = LoginState.Result(it, userRole ?: "")
                    } else {
                        _loginState.value = LoginState.Error(userResult.exceptionOrNull()?.message)
                    }


                }
            }
                .onFailure {
                    _loginState.value = LoginState.Error(it.message)
                }
        }
    }





    companion object {
        private const val TAG = "LoginViewModel"
        private const val USER = "user"
        private const val ADMIN = "admin"
    }

}


