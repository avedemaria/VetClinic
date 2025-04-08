package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.data.network.model.AppointmentDto
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.usecases.GetAppointmentUseCase
import com.example.vetclinic.domain.usecases.UpdateAppointmentUseCase
import com.example.vetclinic.toLocalDateOrNull
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

class SharedAppointmentsViewModel @Inject constructor(
    private val getAppointmentUseCase: GetAppointmentUseCase,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
    private val userDataStore: UserDataStore,
    private val supabaseApiService: SupabaseApiService,
) : ViewModel() {

    private val _appointmentsState =
        MutableStateFlow<SharedAppointmentsState>(SharedAppointmentsState.Loading)
    val appointmentsState: MutableStateFlow<SharedAppointmentsState> get() = _appointmentsState

//    private var storedItems = listOf<AppointmentWithDetails>()


    init {
        viewModelScope.launch {
            val userId = userDataStore.getUserId().orEmpty()
            Log.d(TAG, "userId: $userId")
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

    private fun observeAppointmentsByUserId(userId: String) {
        _appointmentsState.value = SharedAppointmentsState.Loading
        getAppointmentUseCase.observeAppointmentsInRoomByUserId(userId)
            .onEach { appointments ->
                if (appointments.isEmpty()) {
                    _appointmentsState.value = SharedAppointmentsState.Empty
                } else {
                    _appointmentsState.value = SharedAppointmentsState.Success(appointments)
                    setLastCompletedAppointmentDate(appointments)
                    selectedDate = lastCompletedAppointmentDate
                }
            }.catch { e ->
                _appointmentsState.value =
                    SharedAppointmentsState.Error(e.message ?: "Неизвестная ошибка")
            }.launchIn(viewModelScope)
    }


    fun updateAppointmentStatus(updatedAppointment: AppointmentWithDetails) {
        _appointmentsState.value = SharedAppointmentsState.Loading
        viewModelScope.launch {
            val result = updateAppointmentUseCase.updateAppointmentStatus(updatedAppointment)
            if (!result.isSuccess) {
                _appointmentsState.value =
                    SharedAppointmentsState.Error(result.exceptionOrNull()?.message.toString())
            }
        }
    }

    fun subscribeToAppointmentChanges() {

        viewModelScope.launch {

            updateAppointmentUseCase.subscribeToAppointmentChanges() { updatedAppointment ->
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

                            Log.d(TAG, "updated app with details $updatedAppointmentWithDetails")
                            viewModelScope.launch {
                                updateAppointmentUseCase.updateAppointmentInRoom(
                                    updatedAppointmentWithDetails
                                )
                            }
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

    fun addMockAppointmentToSupabase() {
        viewModelScope.launch {
            val currentDateTime = java.time.LocalDateTime.now() // текущее время
            val isoTime = currentDateTime.minusMinutes(14).toString() // Пример: "2025-04-04T16:37"

            supabaseApiService.addMockAppointment(
                AppointmentDto(
                    UUID.randomUUID().toString(),
                    userDataStore.getUserId().toString(),
                    "1ed4a58c-1269-47ec-a5c3-c56332924952",
                    "90c2725d-7897-49d5-a12e-f69fbaeec517",
                    "b535a27a-7597-4f75-ac95-0ae208f559df",
                    isoTime,
                    "SCHEDULED",
                    false,
                    false
                )
            )


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