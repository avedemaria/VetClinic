package com.example.vetclinic.presentation.viewmodel.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.vetclinic.data.SessionManager
import com.example.vetclinic.domain.authFeature.LogInUserUseCase
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.interfaces.UserDataStore
import com.example.vetclinic.domain.usecases.GetAppointmentUseCase
import com.example.vetclinic.domain.usecases.UpdateAppointmentUseCase
import com.example.vetclinic.presentation.viewmodel.PetUiState
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.coroutines.cancellation.CancellationException


class AdminHomeViewModel @Inject constructor(
    private val getAppointmentUseCase: GetAppointmentUseCase,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
    private val loginUseCase: LogInUserUseCase,
    private val userDataStore: UserDataStore,


    ) : ViewModel() {

    private val _adminState = MutableStateFlow<AdminHomeState>(AdminHomeState.Empty)
    val adminState: StateFlow<AdminHomeState> get() = _adminState.asStateFlow()


    private var selectedDate: LocalDate? = null
    private var currentDate: LocalDate? = null

    private var currentLoadJob: Job? = null


    init {
        val today = LocalDate.now()
        currentDate = today
        getAppointmentsByDate(today)
    }


    fun getCurrentDate(): LocalDate? {
        return currentDate
    }


    fun setUpSelectedDate(date: LocalDate) {
        selectedDate = date
        getAppointmentsByDate(date)//status
    }


    private fun getAppointmentsByDate(date: LocalDate) {
        _adminState.value = AdminHomeState.Loading

        selectedDate = date
        val formattedDate = date.format(DateTimeFormatter.ISO_DATE)

        currentLoadJob?.cancel()
        currentLoadJob = viewModelScope.launch {
                getAppointmentUseCase.getAppointmentsByDate(formattedDate)
                    .cachedIn(viewModelScope)
                    .catch { e ->
                        if (e !is CancellationException) {
                            _adminState.value = AdminHomeState.Error("Ошибка загрузки: ${e.message}")
                        }
                    }
                    .collect { pagingData ->
                        Log.d(TAG, "Paging collected! $pagingData")
                        if (selectedDate == date)
                            currentDate = date
                        _adminState.value = AdminHomeState.Success(pagingData, formattedDate)
                    }

        }
    }


    fun refreshAppointments() {
        currentDate?.let { getAppointmentsByDate(it) }
    }


    fun toggleAppointmentStatus(appointmentWithDetails: AppointmentWithDetails) {
        viewModelScope.launch {
            try {
                val newStatus = !appointmentWithDetails.isConfirmed

                val result = updateAppointmentUseCase.updateAppointmentStatus(
                    appointmentWithDetails.copy(isConfirmed = newStatus)
                )
                if (!result.isSuccess) {
                    Log.d(TAG, "failed to update appointment status")
                }
            } catch (e: Exception) {
                _adminState.value = AdminHomeState.Error("Возникла ошибка: ${e.message}")
            }
        }
    }


    fun logOut() {
        _adminState.value = AdminHomeState.Loading

        viewModelScope.launch {
            val result = loginUseCase.logOut()
            if (result.isSuccess) {
                userDataStore.clearUserSession()
                _adminState.value = AdminHomeState.LoggedOut
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                _adminState.value = AdminHomeState.Error(errorMessage)
            }
        }
    }


    companion object {
        private const val TAG = "AdminHomeViewModel"
    }
}