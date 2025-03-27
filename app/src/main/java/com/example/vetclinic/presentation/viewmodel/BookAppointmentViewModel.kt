package com.example.vetclinic.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.domain.entities.Appointment
import com.example.vetclinic.domain.entities.AppointmentStatus
import com.example.vetclinic.domain.entities.Day
import com.example.vetclinic.domain.entities.DayWithTimeSlots
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.domain.entities.TimeSlot
import com.example.vetclinic.domain.usecases.AddAppointmentUseCase
import com.example.vetclinic.domain.usecases.AddTimeSlotsUseCase
import com.example.vetclinic.domain.usecases.GetPetsUseCase
import com.example.vetclinic.domain.usecases.GetTimeSlotsUseCase
import com.example.vetclinic.domain.usecases.UpdateTimeSlotUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.util.UUID


class BookAppointmentViewModel @Inject constructor(
    private val getTimeSlotsUseCase: GetTimeSlotsUseCase,
    private val addTimeSlotsUseCase: AddTimeSlotsUseCase,
    private val getPetsUseCase: GetPetsUseCase,
    private val addAppointmentUseCase: AddAppointmentUseCase,
    private val updateTimeSlotUseCase: UpdateTimeSlotUseCase,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _bookAppointmentState = MutableLiveData<BookAppointmentState>()
    val bookAppointmentState: LiveData<BookAppointmentState> get() = _bookAppointmentState


    private var allDaysWithTimeSlots: List<DayWithTimeSlots> = emptyList()
    private var filteredTimeSlots: List<TimeSlot> = emptyList()
    private var pets: List<Pet> = emptyList()
    var selectedDay: Day? = null
    var selectedTimeSlot: TimeSlot? = null


    init {
        viewModelScope.launch {
            val userId = userDataStore.getUserId() ?: ""
            Log.d(TAG, "Received userId: $userId")
            getPets(userId)
        }
    }

    fun getPetsFromCurrentState(): List<Pet> {
        return when (val currentState = _bookAppointmentState.value) {
            is BookAppointmentState.Success -> currentState.pets
            else -> emptyList()
        }
    }


    private suspend fun addTimeSlotsToSupabaseDb(
        doctorId: String,
        serviceId: String,
        duration: String
    ) {
        _bookAppointmentState.value = BookAppointmentState.Loading
        val result = addTimeSlotsUseCase.addTimeSlots(doctorId, serviceId, duration)

        if (!result.isSuccess) {
            _bookAppointmentState.value =
                BookAppointmentState.Error(result.exceptionOrNull()?.message.toString())
        }
    }


    fun getTimeSlots(doctorId: String, serviceId: String, duration: String) {

        _bookAppointmentState.value = BookAppointmentState.Loading

        viewModelScope.launch {
            try {

                addTimeSlotsToSupabaseDb(doctorId, serviceId, duration)

                val result = getTimeSlotsUseCase.getTimeSlots(doctorId, serviceId)

                if (result.isSuccess) {
                    allDaysWithTimeSlots = result.getOrNull() ?: emptyList()

                    selectedDay =
                        allDaysWithTimeSlots.firstOrNull()?.day
                            ?.copy(isSelected = true)
                    Log.d(TAG, "timeslots were retrieved from Supabase")
                    applyDayFilter()

                    updateSuccessState()
                } else {
                    setErrorState(result)
                }
            } catch (e: Exception) {
                _bookAppointmentState.value = BookAppointmentState.Error(e.message.toString())
            }
        }
    }


    fun bookAppointment(petId: String, serviceId: String, doctorId: String, dateTime: String) {
        _bookAppointmentState.value = BookAppointmentState.Loading


        viewModelScope.launch {
            val userId = userDataStore.getUserId() ?: ""
            val appointment = Appointment(
                UUID.randomUUID().toString(),
                userId,
                petId,
                doctorId,
                serviceId,
                dateTime,
                AppointmentStatus.SCHEDULED,
                false
            )

            val result = addAppointmentUseCase.addAppointment(appointment)
            if (result.isSuccess) {
                if (selectedDay != null) {

                    val timeSlot = selectedTimeSlot
                    if (timeSlot!= null) {
                        updateTimeSlotUseCase.updateTimeSlotStatusToBooked(timeSlot.id)
                        Log.d(TAG, "selected time slot $timeSlot")
                    }


                }
                _bookAppointmentState.value = BookAppointmentState.AppointmentAdded
            } else {
                _bookAppointmentState.value = BookAppointmentState.Error(
                    result.exceptionOrNull()
                        ?.message ?: "Неизвестная ошибка"
                )
            }
        }

    }


    private fun applyDayFilter() {
        if (selectedDay != null) {
            Log.d(TAG, "Selected Day ID: ${selectedDay?.id}")
            Log.d(TAG, "Total Days with TimeSlots: ${allDaysWithTimeSlots.size}")

            val matchingDayWithTimeSlots = allDaysWithTimeSlots
                .find { it.day.id == selectedDay?.id }

            Log.d(TAG, "Matching Day Found: ${matchingDayWithTimeSlots != null}")

            filteredTimeSlots = matchingDayWithTimeSlots?.timeSlots ?: emptyList()
            Log.d(TAG, "Filtered TimeSlots Count: ${filteredTimeSlots.size}")
        }
    }


    fun onDaySelected(day: Day) {

        if (selectedDay?.id != day.id) {
            selectedDay = day.copy(isSelected = true)

            selectedTimeSlot = null

            applyDayFilter()
            updateSuccessState()
        }
    }


    fun onTimeSlotSelected(timeSlot: TimeSlot) {
        selectedTimeSlot = timeSlot
        updateSuccessState()
    }


    private suspend fun getPets(userId: String) {

        _bookAppointmentState.value = BookAppointmentState.Loading

        val result = getPetsUseCase.getPetsFromSupabaseDb(userId)
        if (result.isSuccess) {
            pets = result.getOrNull() ?: emptyList()
            updateSuccessState()
        } else {
            setErrorState(result)
        }
    }


    private fun updateSuccessState() {
        _bookAppointmentState.value =
            BookAppointmentState.Success(
                allDaysWithTimeSlots,
                filteredTimeSlots,
                pets,
                selectedDay,
                selectedTimeSlot
            )
    }

    private fun <T> setErrorState(result: Result<T>) {
        val errorMessage = result.exceptionOrNull().toString()
        _bookAppointmentState.value = BookAppointmentState.Error(errorMessage)
    }

    companion object {
        private const val TAG = "BookAppointmentViewModel"
    }
}