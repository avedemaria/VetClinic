package com.example.vetclinic.presentation.viewmodel.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.vetclinic.domain.authFeature.LogInUserUseCase
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.interfaces.UserDataStore
import com.example.vetclinic.domain.usecases.GetAppointmentUseCase
import com.example.vetclinic.domain.usecases.UpdateAppointmentUseCase
import com.example.vetclinic.presentation.viewmodel.PetUiState
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AdminHomeViewModel @Inject constructor(
    private val getAppointmentUseCase: GetAppointmentUseCase,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
    private val loginUseCase: LogInUserUseCase,
    private val userDataStore: UserDataStore,


    ) : ViewModel() {

    private val _adminState = MutableStateFlow<AdminHomeState>(AdminHomeState.Empty)
    val adminState: StateFlow<AdminHomeState> get() = _adminState.asStateFlow()

    private val _adminEvents = MutableSharedFlow<AdminHomeEvent>()
    val adminEvents: SharedFlow<AdminHomeEvent> get() = _adminEvents.asSharedFlow()

    private var selectedDate: LocalDate? = null
    private var currentDate: LocalDate? = null

    private var count = 0

    init {
        val today = LocalDate.now()
        currentDate = today
        Log.d(TAG, "init ${++count}")
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
        _adminState.value = AdminHomeState.Loading //reset(pagingdata) ->loading
        selectedDate = date

        val formattedDate = date.format(DateTimeFormatter.ISO_DATE)
        Log.d(TAG, "Fetching appointments for date: $formattedDate")
        viewModelScope.launch {
            try {
                getAppointmentUseCase.getAppointmentsByDate(formattedDate)
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        Log.d("getAppointmentsByDate", "Paging collected again! $pagingData")
                        currentDate = date
                        _adminState.value = AdminHomeState.Success(pagingData, formattedDate)
                    }

            } catch (e: Exception) {
                _adminState.value = AdminHomeState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }


    fun refreshAppointments() {
        currentDate?.let { getAppointmentsByDate(it) }
    }


    fun toggleAppointmentStatus(appointmentWithDetails: AppointmentWithDetails) {
        viewModelScope.launch {
            try {
                val newStatus = !appointmentWithDetails.isConfirmed  // Переключаем статус

                val result = updateAppointmentUseCase.updateAppointmentStatus(
                    appointmentWithDetails.copy(isConfirmed = newStatus)
                )
                if (result.isSuccess) {
                    _adminEvents.emit(
                        AdminHomeEvent.OnBellClicked(appointmentWithDetails.copy(isConfirmed = newStatus))
                    )
                } else {
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