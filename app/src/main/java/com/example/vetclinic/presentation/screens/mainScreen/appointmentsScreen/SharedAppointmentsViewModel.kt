package com.example.vetclinic.presentation.screens.mainScreen.appointmentsScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.repository.UserDataStore
import com.example.vetclinic.domain.entities.appointment.Appointment
import com.example.vetclinic.domain.entities.appointment.AppointmentWithDetails
import com.example.vetclinic.domain.usecases.AppointmentUseCase
import com.example.vetclinic.domain.usecases.SessionUseCase
import com.example.vetclinic.presentation.screens.UiEvent
import com.example.vetclinic.utils.toLocalDateOrNull
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate

class SharedAppointmentsViewModel @Inject constructor(
    private val appointmentUseCase: AppointmentUseCase,
    private val sessionUseCase: SessionUseCase,
) : ViewModel() {

    private val _appointmentsState =
        MutableStateFlow<SharedAppointmentsState>(SharedAppointmentsState.Loading)
    val appointmentsState: MutableStateFlow<SharedAppointmentsState> get() = _appointmentsState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()


    init {
        viewModelScope.launch {
            val userId = sessionUseCase.getUserId().orEmpty()
            getAppointmentsByUserId(userId)
            observeAppointmentsByUserId(userId)
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
        Timber.tag(TAG).d("lastCompletedAppointmentDate: $lastCompletedAppointmentDate")
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

    private fun observeAppointmentsByUserId(userId: String) {
        _appointmentsState.value = SharedAppointmentsState.Loading
        appointmentUseCase.observeAppointmentsInRoomByUserId(userId)
            .onEach { appointments ->
                if (appointments.isEmpty()) {
                    _appointmentsState.value = SharedAppointmentsState.Empty
                } else {
                    _appointmentsState.value = SharedAppointmentsState.Success(appointments)
                    setLastCompletedAppointmentDate(appointments)
                    selectedDate = lastCompletedAppointmentDate

                }
            }.catch { e -> _appointmentsState.value = SharedAppointmentsState.Error
                _uiEvent.emit(UiEvent.ShowSnackbar(e.message.toString()))
            }.launchIn(viewModelScope)
    }


    fun updateAppointmentStatus(updatedAppointment: AppointmentWithDetails) {
        viewModelScope.launch {
            val result = appointmentUseCase.updateAppointmentStatus(updatedAppointment)
            if (!result.isSuccess) { _appointmentsState.value = SharedAppointmentsState.Error
                _uiEvent.emit(UiEvent.ShowSnackbar("Ошибка обновления статуса"))
            }
        }
    }

   private fun subscribeToAppointmentChanges() {
        viewModelScope.launch {
            appointmentUseCase.subscribeToAppointmentChanges() { updatedAppointment ->
                _appointmentsState.value = _appointmentsState.value.let { currentState ->
                    when (currentState) {
                        is SharedAppointmentsState.Success -> {

                            val updatedList = currentState.appointments.map { appointment ->
                                if (appointment.id == updatedAppointment.id) {
                                    appointment.copy(
                                        status = updatedAppointment.status,
                                        isArchived = updatedAppointment.isArchived
                                    )
                                } else {
                                    appointment
                                }
                            }

                            updateAppointmentInRoom(updatedList, updatedAppointment)

                            SharedAppointmentsState.Success(updatedList, selectedDate)
                        }

                        else -> currentState
                    }
                }
            }
        }
    }


    private suspend fun getAppointmentsByUserId(userId: String) {
        _appointmentsState.value = SharedAppointmentsState.Loading
        appointmentUseCase.getAppointmentsByUserIdFromSupabase(userId).fold(
            onSuccess = { appointments ->
                _appointmentsState.value = SharedAppointmentsState.Success(appointments)
            },
            onFailure = { e ->
                _appointmentsState.value = SharedAppointmentsState.Error
                _uiEvent.emit(UiEvent.ShowSnackbar(e.message.toString()))
            })
    }

    private fun updateAppointmentInRoom(
        updatedList: List<AppointmentWithDetails>,
        updatedAppointment: Appointment,
    ) {
        val updatedAppointmentWithDetails = updatedList.first { it.id == updatedAppointment.id }
        Timber.tag(TAG).d("updated app with details $updatedAppointmentWithDetails")
        viewModelScope.launch {
            appointmentUseCase.updateAppointmentInRoom(
                updatedAppointmentWithDetails
            )
        }
    }

    fun unsubscribeFromChanges() {
        viewModelScope.launch {
            appointmentUseCase.unsubscribeFromAppointmentChanges()
        }
    }

    override fun onCleared() {
        super.onCleared()
        unsubscribeFromChanges()
    }

    companion object {
        private const val TAG = "SharedAppointmentsViewModel"
    }


}