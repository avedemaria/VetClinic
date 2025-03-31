package com.example.vetclinic.data.mapper

import com.example.vetclinic.data.network.model.DayDto
import com.example.vetclinic.data.network.model.DayWithTimeSlotsDto
import com.example.vetclinic.data.network.model.TimeSlotDto
import com.example.vetclinic.domain.entities.Day
import com.example.vetclinic.domain.entities.DayWithTimeSlots
import com.example.vetclinic.domain.entities.TimeSlot
import com.example.vetclinic.toLocalDateDefault
import jakarta.inject.Inject

class DayWithTimeSlotsMapper @Inject constructor() {


    fun timeSlotDtoToEntity(dto: TimeSlotDto): TimeSlot {
        return TimeSlot(
            id = dto.id,
            doctorId = dto.doctorId,
            serviceId = dto.serviceId,
            dayId = dto.dayId,
            startTime = dto.formattedStartTime,
            endTime = dto.endTime,
            isBooked = dto.isBooked
        )
    }

    fun timeSlotEntityToDto(entity: TimeSlot): TimeSlotDto {
        return TimeSlotDto(
            id = entity.id,
            doctorId = entity.doctorId,
            serviceId = entity.serviceId,
            dayId = entity.dayId,
            startTime = entity.startTime,
            endTime = entity.endTime,
            isBooked = entity.isBooked
        )
    }


    fun dayWithTimeSlotsDtoToEntity(dto: DayWithTimeSlotsDto): DayWithTimeSlots {
        return DayWithTimeSlots(
            day = dayWithTimeSlotsDtoToDay(dto),
            timeSlots = dto.timeSlots.map(::timeSlotDtoToEntity)
        )
    }

    fun dayWithTimeSlotsEntityToDto(entity: DayWithTimeSlots): DayWithTimeSlotsDto {
        return DayWithTimeSlotsDto(
            id = entity.day.id,
            date = entity.day.date.toString(),
            timeSlots = entity.timeSlots.map(::timeSlotEntityToDto)
        )
    }

    fun dayWithTimeSlotsDtoToDayDto(dto: DayWithTimeSlotsDto): DayDto {
        return DayDto(id = dto.id, date = dto.date)
    }

    fun dayWithTimeSlotsDtoListToDayDtoList(dayWithTimeSlotsDtoList: List<DayWithTimeSlotsDto>):
            List<DayDto> {
        return dayWithTimeSlotsDtoList.map(::dayWithTimeSlotsDtoToDayDto)
    }

    private fun dayWithTimeSlotsDtoToDay(dayWithTimeSlotsDto: DayWithTimeSlotsDto): Day {
        return Day(
            id = dayWithTimeSlotsDto.id,
            date = dayWithTimeSlotsDto.date.toLocalDateDefault()
        )
    }


    fun formatToTimeStamp(date: String, time: String): String {
        return "$date $time:00"
    }

}