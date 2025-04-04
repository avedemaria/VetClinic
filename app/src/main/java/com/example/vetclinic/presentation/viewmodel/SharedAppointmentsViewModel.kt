package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.usecases.GetAppointmentUseCase
import com.example.vetclinic.domain.usecases.UpdateAppointmentUseCase
import com.example.vetclinic.toLocalDateOrNull
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class SharedAppointmentsViewModel @Inject constructor(
    private val getAppointmentUseCase: GetAppointmentUseCase,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
    private val userDataStore: UserDataStore,
) : ViewModel() {

    private val _appointmentsState =
        MutableStateFlow<SharedAppointmentsState>(SharedAppointmentsState.Loading)
    val appointmentsState: MutableStateFlow<SharedAppointmentsState> get() = _appointmentsState

//    private var storedItems = listOf<AppointmentWithDetails>()



    init {
        viewModelScope.launch {
            val userId = userDataStore.getUserId().orEmpty()
            Log.d(TAG, "userId: $userId")
            getAppointmentsByUserId(userId)
            subscribeToAppointmentChanges()
        }
    }


    private var selectedDate: LocalDate? = null
    private var lastCompletedAppointmentDate: LocalDate? = null


    fun getLastCompletedAppointmentDate(): LocalDate? {
        return lastCompletedAppointmentDate
    }


    private fun setLastCompletedAppointmentDate(appointments: List<AppointmentWithDetails>) {
        val lastCompletedAppointment =
            appointments.maxByOrNull { it.dateTime }
        lastCompletedAppointmentDate =
            lastCompletedAppointment?.dateTime?.toLocalDateOrNull("yyyy-MM-dd'T'HH:mm:ss")
        Log.d(TAG, "lastCompletedAppointmentDate: $lastCompletedAppointmentDate")
    }


    fun setUpSelectedDate(date: LocalDate) {
        selectedDate = date
        _appointmentsState.value.let { currentState ->
            if (currentState is SharedAppointmentsState.Success) {
                _appointmentsState.value = SharedAppointmentsState.Success(
                    currentState.appointments,
                    selectedDate
                )
            }
        }
    }

    private suspend fun getAppointmentsByUserId(userId: String) {
        _appointmentsState.value = SharedAppointmentsState.Loading
        getAppointmentUseCase.getAppointmentsByUserIdFromRoom(userId).fold(
            onSuccess = { appointments ->
                if (appointments.isEmpty()) {
                    _appointmentsState.value = SharedAppointmentsState.Empty
                } else {
                    _appointmentsState.value = SharedAppointmentsState.Success(appointments)
                    setLastCompletedAppointmentDate(appointments)
                    selectedDate = lastCompletedAppointmentDate
                }
            },
            onFailure = { e ->
                _appointmentsState.value =
                    SharedAppointmentsState.Error(e.message ?: "Неизвестная ошибка")
            })
    }


    fun updateAppointmentStatus(updatedAppointment: AppointmentWithDetails) {
        _appointmentsState.value = SharedAppointmentsState.Loading
        viewModelScope.launch {
            val result = updateAppointmentUseCase.updateAppointmentStatus(updatedAppointment)
            if (result.isSuccess) {
                val userId = userDataStore.getUserId() ?: throw Exception("UserId is not found")
                getAppointmentsByUserId(userId)
            } else {
                _appointmentsState.value =
                    SharedAppointmentsState.Error(result.exceptionOrNull()?.message.toString())
            }
        }
    }

  fun subscribeToAppointmentChanges() {

        viewModelScope.launch {

            updateAppointmentUseCase.subscribeToAppointmentChanges().collect { updatedAppointment ->
                _appointmentsState.value = _appointmentsState.value.let { currentState ->
                    when (currentState) {
                        is SharedAppointmentsState.Success -> {

                            val appointmentsWithDetails = currentState.appointments

                            val updatedList = appointmentsWithDetails.map { appointment ->
                                if (appointment.id == updatedAppointment.id) {
                                    appointment.copy(
                                        status = updatedAppointment.status,
                                        isArchived = updatedAppointment.isArchived
                                    )
                                } else {
                                    appointment
                                }
                            }
                            val updatedAppointmentWithDetails =
                                updatedList.first { it.id == updatedAppointment.id }
                            updateAppointmentUseCase.updateAppointmentInRoom(
                                updatedAppointmentWithDetails
                            )
                            SharedAppointmentsState.Success(updatedList, selectedDate)
                        }
                        else -> currentState
                    }
                }
            }
        }
    }


  fun unsubscribeFromChanges() {
        viewModelScope.launch {
            updateAppointmentUseCase.unsubscribeFromAppointmentChanges()
        }
    }

    override fun onCleared() {
        super.onCleared()
        unsubscribeFromChanges()
    }

    companion object {
        private const val TAG = "DetailedAppointmentsViewModel"
    }


}