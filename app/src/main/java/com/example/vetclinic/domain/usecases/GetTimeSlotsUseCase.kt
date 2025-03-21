package com.example.vetclinic.domain.usecases

import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.entities.TimeSlot
import jakarta.inject.Inject

class GetTimeSlotsUseCase @Inject constructor(private val repository: Repository) {


    suspend fun getTimeSlots(): Result<List<TimeSlot>> {
        return repository.getTimeSlots()
    }

}