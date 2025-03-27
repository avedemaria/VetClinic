package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.usecases.GetAppointmentUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class CurrentAppointmentsViewModel @Inject constructor(
    private val getAppointmentUseCase: GetAppointmentUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {


    private val _appointmentsState = MutableLiveData<CurrentAppointmentsState>()
    val appointmentState: LiveData<CurrentAppointmentsState> get() = _appointmentsState


    init {
        viewModelScope.launch {
            val userId = userDataStore.getUserId() ?: ""
            Log.d(TAG, "userId: $userId")
            getAppointmentsByUserId(userId)
        }
    }


    private suspend fun getAppointmentsByUserId(userId: String) {

        _appointmentsState.value = CurrentAppointmentsState.Loading

        val result = getAppointmentUseCase.getAppointmentsByUserId(userId)
        if (result.isSuccess) {
            val appointments = result.getOrNull() ?: emptyList()
            _appointmentsState.value = CurrentAppointmentsState.Success(appointments)
            if (appointments.isEmpty()) {
                _appointmentsState.value = CurrentAppointmentsState.Empty
            }
        } else {
            _appointmentsState.value =
                CurrentAppointmentsState.Error(result.exceptionOrNull()?.message ?: "Неизвестная ошибка")
        }

    }


    companion object {
        private const val TAG = "CurrentAppointmentsViewModel"
    }

}