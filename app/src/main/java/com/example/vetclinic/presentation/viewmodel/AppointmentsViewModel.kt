package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.interfaces.UserDataStore
import com.example.vetclinic.domain.usecases.GetAppointmentUseCase
import com.example.vetclinic.domain.usecases.UpdateAppointmentUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AppointmentsViewModel @Inject constructor(
    private val getAppointmentUseCase: GetAppointmentUseCase,
    private val userDataStore: UserDataStore,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase
) : ViewModel() {


    private val _appointmentsState = MutableStateFlow<AppointmentsState>(AppointmentsState.Loading)
    val appointmentsState: MutableStateFlow<AppointmentsState> get() = _appointmentsState


    init {
        viewModelScope.launch {
            val userId = userDataStore.getUserId().orEmpty()
            Log.d(TAG, "userId: $userId")
            getAppointmentsByUserId(userId)
        }
    }


    private suspend fun getAppointmentsByUserId(userId: String) {
        _appointmentsState.value = AppointmentsState.Loading
        getAppointmentUseCase.getAppointmentsByUserIdFromSupabase(userId).fold(
            onSuccess = { appointments ->
                _appointmentsState.value = AppointmentsState.Success(appointments)
                },
            onFailure = { e ->
                _appointmentsState.value =
                    AppointmentsState.Error(e.message ?: "Неизвестная ошибка")
            })
    }

//   fun unsubscribeFromChanges() {
//        viewModelScope.launch {
//            updateAppointmentUseCase.unsubscribeFromAppointmentChanges()
////        }
////    }
//
//    override fun onCleared() {
//        super.onCleared()
//        Log.d(TAG, "unsubscribed")
//    }

    companion object {
        private const val TAG = "AppointmentsViewModel"
    }
}