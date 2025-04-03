package com.example.vetclinic.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.authFeature.LogInUserUseCase
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.usecases.GetAppointmentUseCase
import com.example.vetclinic.domain.usecases.UpdateAppointmentUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate


class AdminHomeViewModel @Inject constructor(
    private val getAppointmentUseCase: GetAppointmentUseCase,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
    private val loginUseCase: LogInUserUseCase
) : ViewModel() {

    private val _appointmentsState = MutableLiveData<AdminHomeState>()
    val appointmentState: LiveData<AdminHomeState> get() = _appointmentsState


    init {
        val date = LocalDate.now().toString()
        getAppointmentsByDate(date)
    }


    private fun getAppointmentsByDate(date: String) {
        _appointmentsState.value = AdminHomeState.Loading

        viewModelScope.launch {
            val result = getAppointmentUseCase.getAppointmentsByDate(date)
            if (result.isSuccess) {
                val appointments = result.getOrNull() ?: emptyList()
                _appointmentsState.value = AdminHomeState.Success(appointments, null)
            }
        }
    }


    fun updateAppointmentStatus(updatedAppointment: AppointmentWithDetails) {
        _appointmentsState.value = AdminHomeState.Loading

        viewModelScope.launch {
            val date = LocalDate.now().toString()
            val result = updateAppointmentUseCase.updateAppointmentStatus(updatedAppointment)
            if (result.isSuccess) {
                getAppointmentsByDate(date)
            } else {
                _appointmentsState.value =
                    AdminHomeState.Error(result.exceptionOrNull()?.message.toString())
            }
        }

    }

    fun logOut () {
        _appointmentsState.value = AdminHomeState.Loading
        viewModelScope.launch {
            loginUseCase.logOut()
            _appointmentsState.value = AdminHomeState.LoggedOut
        }
    }
}