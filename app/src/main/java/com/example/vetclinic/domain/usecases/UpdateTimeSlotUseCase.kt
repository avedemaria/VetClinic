package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.TimeSlotsRepository
import jakarta.inject.Inject

class UpdateTimeSlotUseCase @Inject constructor(private val repository: TimeSlotsRepository) {


    suspend fun updateTimeSlotStatusToBooked(timeSlotId: String): Result<Unit> {
        return repository.updateTimeSlotStatusToBooked(timeSlotId)
    }
}