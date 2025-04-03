package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.entities.Appointment
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.usecases.GetAppointmentUseCase
import com.example.vetclinic.domain.usecases.UpdateAppointmentUseCase
import com.example.vetclinic.toLocalDateOrNull
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate

class ArchivedAppointmentsViewModel @Inject constructor(
    private val getAppointmentUseCase: GetAppointmentUseCase,
    private val userDataStore: UserDataStore,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
) : ViewModel() {


    private var lastCompletedAppointmentDate: LocalDate? = null

    private var storedItems = listOf<AppointmentWithDetails>()

    private val _appointmentsState = MutableLiveData<ArchivedAppointmentsState>()
    val appointmentState: LiveData<ArchivedAppointmentsState> get() = _appointmentsState


//    init {
//        viewModelScope.launch {
//            val userId = userDataStore.getUserId() ?: ""
//            Log.d(TAG, "userId: $userId")
//            getArchivedAppointmentsByUserId(userId)
////            subscribeToAppointmentChanges()
//        }
//    }


    fun setUpSelectedDate(date: LocalDate) {
        _appointmentsState.value?.let { currentState ->
            if (currentState is ArchivedAppointmentsState.Success) {
                _appointmentsState.value =
                    ArchivedAppointmentsState.Success(currentState.appointments, date)
            }
        }
    }


    fun getLastCompletedAppointmentDate(): LocalDate? {
        return lastCompletedAppointmentDate
    }

//    private suspend fun getArchivedAppointmentsByUserId(userId: String) {
//        _appointmentsState.value = ArchivedAppointmentsState.Loading
//        val result = runCatching {
//            getAppointmentUseCase.getAppointmentsByUserId(userId, true)
//        }
//        result
//            .onSuccess { handleSuccess(it) }
//            .onFailure { handleFailure(it) }
//    }

    private fun handleSuccess(result: Result<List<AppointmentWithDetails>>) {

        val appointments = result.getOrNull() ?: emptyList()
        storedItems = appointments

        if (appointments.isNotEmpty()) {
            val lastCompletedAppointment =
                appointments.maxByOrNull { it.dateTime }//берем самый большой элемент по дате
            Log.d(TAG, "last completed app: $lastCompletedAppointment")
            val lastDate =
                lastCompletedAppointment?.dateTime.toLocalDateOrNull("yyyy-MM-dd'T'HH:mm:ss")

            Log.d(TAG, "lastDate: $lastDate")

            lastCompletedAppointmentDate = lastDate
            _appointmentsState.value = ArchivedAppointmentsState.Success(
                appointments,
                null
            )// date null сначала чтобы загрузились все приемы, а потом уже череp datePicker будет сохраняться сюда
        } else {
            _appointmentsState.value = ArchivedAppointmentsState.Empty
        }
    }


    private fun handleFailure(throwable: Throwable) {
        _appointmentsState.value = ArchivedAppointmentsState.Error(
            throwable.message
                ?: "Неизвестная ошибка"
        )
    }

    private fun appointmentToAppointmentWithDetails(
        appointment: Appointment,
    ): AppointmentWithDetails {
        // Найдем в списке AppointmentWithDetails по id нужные дополнительные данные
        val details = storedItems.find { it.id == appointment.id }

        return AppointmentWithDetails(
            id = appointment.id,
            userId = appointment.userId,
            petId = appointment.petId,
            doctorId = appointment.doctorId,
            serviceId = appointment.serviceId,
            dateTime = appointment.dateTime,
            status = appointment.status,
            isArchived = appointment.isArchived,
            isConfirmed = appointment.isConfirmed,
            serviceName = details?.serviceName ?: "",  // Используем данные из storedItems
            doctorName = details?.doctorName ?: "",
            doctorRole = details?.doctorRole ?: "",
            petName = details?.petName ?: "",
            userName = details?.userName ?: ""
        )
    }


//    private fun subscribeToAppointmentChanges() {
//        Log.d(TAG, "subscribe to appointments changes launched")
//        viewModelScope.launch {
//
//            updateAppointmentUseCase.subscribeToAppointmentChanges { updatedAppointment ->
//
//                val exists = storedItems.any { it.id == updatedAppointment.id }
//
//                val updatedList = if (exists) {
//                    storedItems.map { appointment ->
//                        if (appointment.id == updatedAppointment.id) {
//                            // Преобразуем Appointment в AppointmentWithDetails с уже известными данными
//                            val updatedDetails =
//                                appointmentToAppointmentWithDetails(updatedAppointment)
//                            updatedDetails.copy(
//                                status = updatedAppointment.status,
//                                isArchived = updatedAppointment.isArchived
//                            )
//                        } else {
//                            appointment
//                        }
//                    }
//                } else {
//                    // Если приема нет в списке, создаем новый AppointmentWithDetails
//                    val newAppointmentWithDetails =
//                        appointmentToAppointmentWithDetails(updatedAppointment)
//                    storedItems + newAppointmentWithDetails
//                }
//
//                _appointmentsState.value = ArchivedAppointmentsState.Success(updatedList, null)
//            }
//        }
//    }


    fun unsubscribeFromChanges() {
        viewModelScope.launch {
            updateAppointmentUseCase.unsubscribeFromAppointmentChanges()
        }
    }

    companion object {
        private const val TAG = "ArchivedAppointmentsViewModel"
    }
}
