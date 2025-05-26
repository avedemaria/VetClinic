package com.example.vetclinic.presentation.screens.loginScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.data.workers.ReminderManager
import com.example.vetclinic.domain.repository.UserDataStore
import com.example.vetclinic.domain.usecases.AppointmentUseCase
import com.example.vetclinic.domain.usecases.LoginUseCase
import com.example.vetclinic.domain.usecases.PetUseCase
import com.example.vetclinic.domain.usecases.SessionUseCase
import com.example.vetclinic.domain.usecases.UserUseCase
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate

class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUseCase,
    private val sessionUseCase: SessionUseCase,
    private val userUseCase: UserUseCase,
    private val petUseCase: PetUseCase,
    private val appointmentUseCase: AppointmentUseCase,
    private val reminderManager: ReminderManager,
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> get() = _loginState


    fun loginUser(email: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            authorizeUser(email, password)?.let { userSession ->

                val userId =
                    userSession.user?.id ?: return@launch showError("ID пользователя не найден")

                getAndSaveUserRole(userId)?.let { role ->
                    handleRoleWhileLogin(role, userId)
                    _loginState.value = LoginState.Result(userSession, role)
                }
            }
        }
    }


    private suspend fun authorizeUser(email: String, password: String): UserSession? {
        return loginUserUseCase.loginUser(email, password)
            .onFailure { showError(it.message ?: "Ошибка авторизации") }
            .getOrNull()
            ?.also {
                sessionUseCase.saveUserSession(
                    it.user?.id.orEmpty(),
                    it.accessToken,
                    it.refreshToken
                )
            }
    }

    private suspend fun getAndSaveUserRole(userId: String): String? {
        val result = userUseCase.getUserFromSupabaseDb(userId)

        result.onFailure {
            it.message?.let { error -> showError(error) }
        }

        return result.getOrNull()?.role?.also { role ->
            sessionUseCase.saveUserRole(role)
            reminderManager.onLogin(role)
            Log.d(TAG, "saved user role: $role")
        }

    }


    private suspend fun handleRoleWhileLogin(userRole: String?, userId: String) {
        when (userRole) {
            USER -> {
                petUseCase.getPetsFromSupabaseDb(userId)
            }

            ADMIN -> {
                appointmentUseCase.getAppointmentsByDate(
                    LocalDate.now().toString()
                )
            }

            else -> {
                _loginState.value = LoginState.Error("Неизвестная роль")
                return
            }
        }
    }


    private fun showError(message: String) {
        _loginState.value = LoginState.Error(message)
    }

    companion object {
        private const val TAG = "LoginViewModel"
        private const val USER = "user"
        private const val ADMIN = "admin"
    }

}


