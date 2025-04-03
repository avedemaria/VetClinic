package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.entities.Appointment
import com.example.vetclinic.domain.entities.AppointmentStatus
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.usecases.GetAppointmentUseCase
import com.example.vetclinic.domain.usecases.UpdateAppointmentUseCase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class CurrentAppointmentsViewModel @Inject constructor(
    private val getAppointmentUseCase: GetAppointmentUseCase,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
    private val userDataStore: UserDataStore,
) : ViewModel() {


    private val _appointmentsState = MutableLiveData<AppointmentsState>()
    val appointmentState: LiveData<AppointmentsState> get() = _appointmentsState

    private var storedItems = listOf<AppointmentWithDetails>()

//
//    init {
//        viewModelScope.launch {
//            val userId = userDataStore.getUserId() ?: ""
//            Log.d(TAG, "userId: $userId")
//            getCurrentAppointmentsByUserId(userId)
////            subscribeToAppointmentChanges()
//        }
//    }


//    private suspend fun getCurrentAppointmentsByUserId(userId: String) {
//
//        _appointmentsState.value = AppointmentsState.Loading
//        val result = getAppointmentUseCase.getAppointmentsByUserId(userId, false)
//
//        if (result.isSuccess) {
//            val appointments = result.getOrNull() ?: emptyList()
//            storedItems = appointments
//            _appointmentsState.value = AppointmentsState.Success(appointments)
//            if (appointments.isEmpty()) {
//                _appointmentsState.value = AppointmentsState.Empty
//            }
//        } else {
//            _appointmentsState.value = AppointmentsState.Error(
//                result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
//            )
//        }
//    }

//
//    fun updateAppointmentStatus(updatedAppointment: AppointmentWithDetails) {
//        _appointmentsState.value = AppointmentsState.Loading
//
//        viewModelScope.launch {
//            val result = updateAppointmentUseCase.updateAppointmentStatus(updatedAppointment)
//            if (result.isSuccess) {
//                val userId = userDataStore.getUserId() ?: throw Exception("UserId is not found")
//                getCurrentAppointmentsByUserId(userId)
//            } else {
//                _appointmentsState.value =
//                    AppointmentsState.Error(result.exceptionOrNull()?.message.toString())
//            }
//        }
//    }

//
//    private fun subscribeToAppointmentChanges() {
//        Log.d(TAG, "subscribe to appointments changes launched")
//        viewModelScope.launch {
//
//            updateAppointmentUseCase.subscribeToAppointmentChanges { updatedAppointment ->
//
//                val updatedList = storedItems.map { appointment ->
//                    if (appointment.id == updatedAppointment.id) {
//                        appointment.copy(
//                            status = updatedAppointment.status,
//                            isArchived = updatedAppointment.isArchived
//                        )
//                    } else {
//                        appointment
//                    }
//                }   .filter { !it.isArchived }
//
//                storedItems = updatedList
//                _appointmentsState.value = AppointmentsState.Success(updatedList)
//            }
//        }
//
//    }


    fun unsubscribeFromChanges () {
        viewModelScope.launch {
            updateAppointmentUseCase.unsubscribeFromAppointmentChanges()
        }
    }



override fun onCleared() {
    super.onCleared()
    viewModelScope.launch {
        Log.d(TAG, "unsubscribe from realtime in viewmodel")
        updateAppointmentUseCase.unsubscribeFromAppointmentChanges()
    }
}

companion object {
    private const val TAG = "CurrentAppointmentsViewModel"
}

}



