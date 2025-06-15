package com.example.vetclinic.presentation.screens.loginScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.data.workers.ReminderManager
import com.example.vetclinic.domain.usecases.AppointmentUseCase
import com.example.vetclinic.domain.usecases.LoginUseCase
import com.example.vetclinic.domain.usecases.PetUseCase
import com.example.vetclinic.domain.usecases.SessionUseCase
import com.example.vetclinic.domain.usecases.UserUseCase
import com.example.vetclinic.presentation.screens.UiEvent
import io.github.jan.supabase.auth.user.UserSession
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
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

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun loginUser(email: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            authorizeUser(email, password).fold(
                onSuccess = { userSession ->
                    val userId =
                        userSession.user?.id ?: return@launch showError("ID пользователя не найден")

                    getAndSaveUserRole(userId)?.let { role ->
                        handleRoleWhileLogin(role, userId)
                        _loginState.value = LoginState.Result(userSession, role)
                    }
                },
                onFailure = { error ->
                    _loginState.value = LoginState.Error
                    showError(error.message ?: "Ошибка авторизации")
                }
            )
        }
    }


    private suspend fun authorizeUser(
        email: String,
        password: String,
    ): Result<UserSession> {
        return runCatching {
            val userSession = loginUserUseCase.loginUser(email, password).getOrThrow()
            saveUserSession(userSession).getOrThrow()
            userSession
        }
    }


    private suspend fun saveUserSession(userSession: UserSession): Result<Unit> {
        return try {
            sessionUseCase.saveUserSession(
                userSession.user?.id.orEmpty(),
                userSession.accessToken,
                userSession.refreshToken
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to save user session to datastore")
            Result.failure(e)
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
                _loginState.value = LoginState.Error
                return
            }
        }
    }


    private suspend fun showError(message: String) {
        _uiEvent.emit(UiEvent.ShowSnackbar(message))
    }

    companion object {
        private const val TAG = "LoginViewModel"
        private const val USER = "user"
        private const val ADMIN = "admin"
    }

}


