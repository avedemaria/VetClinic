package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.entities.AppointmentWithDetails
import com.example.vetclinic.domain.usecases.GetAppointmentUseCase
import com.example.vetclinic.toLocalDateOrNull
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate

class ArchivedAppointmentsViewModel @Inject constructor(
    private val getAppointmentUseCase: GetAppointmentUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {


    private var lastCompletedAppointmentDate: LocalDate? = null

    private val _appointmentsState = MutableLiveData<ArchivedAppointmentsState>()
    val appointmentState: LiveData<ArchivedAppointmentsState> get() = _appointmentsState


    init {
        viewModelScope.launch {
            val userId = userDataStore.getUserId() ?: ""
            Log.d(TAG, "userId: $userId")
            getArchivedAppointmentsByUserId(userId)
        }
    }


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

    private suspend fun getArchivedAppointmentsByUserId(userId: String) {
        _appointmentsState.value = ArchivedAppointmentsState.Loading
        val result = runCatching {
            getAppointmentUseCase.getAppointmentsByUserId(userId, true)
        }
        result
            .onSuccess { handleSuccess(it) }
            .onFailure { handleFailure(it) }
    }

    private fun handleSuccess(result: Result<List<AppointmentWithDetails>>) {

        val appointments = result.getOrNull() ?: emptyList()

        if (appointments.isNotEmpty()) {
            val lastCompletedAppointment = appointments.maxByOrNull { it.dateTime }//берем самый большой элемент по дате
            Log.d(TAG, "last completed app: $lastCompletedAppointment")
            val lastDate =
                lastCompletedAppointment?.dateTime.toLocalDateOrNull("yyyy-MM-dd'T'HH:mm:ss")

            Log.d(TAG, "lastDate: $lastDate")

            lastCompletedAppointmentDate = lastDate
            _appointmentsState.value = ArchivedAppointmentsState.Success(appointments,
                null)// date null сначала чтобы загрузились все приемы, а потом уже череp datePicker будет сохраняться сюда
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

    companion object {
        private const val TAG = "ArchivedAppointmentsViewModel"
    }
}
