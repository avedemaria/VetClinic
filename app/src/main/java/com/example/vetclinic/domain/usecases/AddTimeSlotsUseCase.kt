package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.interfaces.TimeSlotsRepository
import jakarta.inject.Inject

class AddTimeSlotsUseCase @Inject constructor(private val repository: TimeSlotsRepository) {


    suspend fun addTimeSlots(
        doctorId: String,
        serviceId: String,
        duration: String
    ): Result<Unit> {
        return repository.generateAndSaveTimeSlots(doctorId, serviceId, duration)
    }
}