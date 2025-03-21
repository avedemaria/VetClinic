package com.example.vetclinic.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vetclinic.domain.usecases.GetServiceUseCase
import com.example.vetclinic.domain.usecases.GetTimeSlotsUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class TimeSlotsViewModel @Inject constructor(
    private val getTimeSlotsUseCase: GetTimeSlotsUseCase,
    private val getServiceUseCase: GetServiceUseCase
) : ViewModel() {


    fun loadTimeSlots(doctorId: String, selectedDate: LocalDate, serviceId: String) {
        viewModelScope.launch {

            val serviceResult = getServiceUseCase.getServiceById(serviceId)

            if (serviceResult.isSuccess) {
                val serviceDuration = serviceResult.getOrNull()?.duration
            } else {
                //ошибка
            }

            val timeSlots = getTimeSlotsUseCase.getTimeSlots()
        }
    }
}