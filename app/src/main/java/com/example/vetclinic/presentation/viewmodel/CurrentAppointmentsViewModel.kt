package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.usecases.GetAppointmentUseCase
import com.example.vetclinic.domain.usecases.UpdateAppointmentUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class CurrentAppointmentsViewModel @Inject constructor(
    private val getAppointmentUseCase: GetAppointmentUseCase,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {


    private val _appointmentsState = MutableLiveData<AppointmentsState>()
    val appointmentState: LiveData<AppointmentsState> get() = _appointmentsState


    init {
        viewModelScope.launch {
            val userId = userDataStore.getUserId() ?: ""
            Log.d(TAG, "userId: $userId")
            getCurrentAppointmentsByUserId(userId)
        }
    }


    private suspend fun getCurrentAppointmentsByUserId(userId: String) {

        _appointmentsState.value = AppointmentsState.Loading
        val result = getAppointmentUseCase.getAppointmentsByUserId(userId, false)

        if (result.isSuccess) {
            val appointments = result.getOrNull() ?: emptyList()
            _appointmentsState.value = AppointmentsState.Success(appointments)
            if (appointments.isEmpty()) {
                _appointmentsState.value = AppointmentsState.Empty
            }
        } else {
            _appointmentsState.value = AppointmentsState.Error(
                result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
            )
        }
    }


    fun updateAppointmentStatus(updatedAppointment: AppointmentWithDetails) {
        _appointmentsState.value = AppointmentsState.Loading

        viewModelScope.launch {
            val result = updateAppointmentUseCase.updateAppointmentStatus(updatedAppointment)
            if (result.isSuccess) {
                val userId = userDataStore.getUserId() ?: throw Exception("UserId is not found")
                getCurrentAppointmentsByUserId(userId)
            } else {
                _appointmentsState.value =
                    AppointmentsState.Error(result.exceptionOrNull()?.message.toString())
            }
        }

    }


    companion object {
        private const val TAG = "CurrentAppointmentsViewModel"
    }

}