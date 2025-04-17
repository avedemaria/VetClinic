package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.entities.timeSlot.DayWithTimeSlots
import com.example.vetclinic.domain.interfaces.TimeSlotsRepository
import jakarta.inject.Inject

class TimeSlotsUseCase @Inject constructor(private val repository: TimeSlotsRepository) {

    suspend fun updateTimeSlotStatusToBooked(timeSlotId: String): Result<Unit> {
        return repository.updateTimeSlotStatusToBooked(timeSlotId)
    }

    suspend fun getTimeSlots(
        doctorId: String,
        serviceId: String
    ): Result<List<DayWithTimeSlots>> {
        return repository.getAvailableDaysAndTimeSlots(doctorId, serviceId)
    }

    suspend fun addTimeSlots(
        doctorId: String,
        serviceId: String,
        duration: String
    ): Result<Unit> {
        return repository.generateAndSaveTimeSlots(doctorId, serviceId, duration)
    }
}