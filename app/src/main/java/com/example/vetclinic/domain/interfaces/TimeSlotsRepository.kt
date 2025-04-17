package com.example.vetclinic.domain.interfaces

import com.example.vetclinic.domain.entities.timeSlot.DayWithTimeSlots

interface TimeSlotsRepository {

    suspend fun getAvailableDaysAndTimeSlots(
        doctorId: String,
        serviceId: String
    ): Result<List<DayWithTimeSlots>>

    suspend fun generateAndSaveTimeSlots(
        doctorId: String,
        serviceId: String,
        duration: String
    ): Result<Unit>


    suspend fun updateTimeSlotStatusToBooked(timeSlotId: String): Result<Unit>
}