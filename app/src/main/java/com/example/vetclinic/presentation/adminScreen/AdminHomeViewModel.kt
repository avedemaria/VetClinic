package com.example.vetclinic.presentation.adminScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails
import com.example.vetclinic.domain.interfaces.UserDataStore
import com.example.vetclinic.domain.usecases.AppointmentUseCase
import com.example.vetclinic.domain.usecases.LoginUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.coroutines.cancellation.CancellationException


data class AdminHomeViewModel @Inject constructor(
    private val appointmentUseCase: AppointmentUseCase,
    private val loginUseCase: LoginUseCase,
    private val userDataStore: UserDataStore


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
        Log.d(TAG, "setUpselecetedDate")
    }


    private fun getAppointmentsByDate(date: LocalDate) {
        _adminState.value = AdminHomeState.Loading

        selectedDate = date
        val formattedDate = date.format(DateTimeFormatter.ISO_DATE)

        currentLoadJob?.cancel()
        currentLoadJob = viewModelScope.launch {
                appointmentUseCase.getAppointmentsByDate(formattedDate)
                    .cachedIn(viewModelScope)
                    .catch { e ->
                        if (e !is CancellationException) {
                            _adminState.value =
                                AdminHomeState.Error("Ошибка загрузки: ${e.message}")
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
        Log.d(TAG, "refresh appointments")
    }


    fun toggleAppointmentStatus(appointmentWithDetails: AppointmentWithDetails) {
        viewModelScope.launch {
            try {
                val newStatus = !appointmentWithDetails.isConfirmed

                val result = appointmentUseCase.updateAppointmentStatus(
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


    fun afterLogout() {
        viewModelScope.launch {
            loginUseCase.clearAllData()
        }
    }


    companion object {
        private const val TAG = "AdminHomeViewModel"
    }
}