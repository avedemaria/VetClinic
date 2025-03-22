package com.example.vetclinic.data.mapper

import com.example.vetclinic.data.network.model.TimeSlotDto
import com.example.vetclinic.domain.entities.TimeSlot
import jakarta.inject.Inject

class TimeSlotMapper @Inject constructor() {


    fun timeSlotDtoToTimeSlotEntity(dto: TimeSlotDto): TimeSlot {
        return TimeSlot(
            id = dto.id,
            doctorId = dto.doctorId,
            serviceId = dto.serviceId,
            startTime = dto.startTime,
            endTime = dto.endTime,
            isBooked = dto.isBooked,
            dayId = dto.dayId
        )
    }

    fun timeSlotDtoListToTimeSlotEntityList(timeSlotDtoList: List<TimeSlotDto>): List<TimeSlot> {
        return timeSlotDtoList.map(::timeSlotDtoToTimeSlotEntity)
    }
}