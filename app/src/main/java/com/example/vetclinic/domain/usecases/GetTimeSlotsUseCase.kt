package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.TimeSlotsRepository
import com.example.vetclinic.domain.entities.DayWithTimeSlots
import jakarta.inject.Inject

class GetTimeSlotsUseCase @Inject constructor(private val repository: TimeSlotsRepository) {


    suspend fun getTimeSlots(
        doctorId: String,
        serviceId: String
    ): Result<List<DayWithTimeSlots>> {
        return repository.getAvailableDaysAndTimeSlots(doctorId, serviceId)
    }

}