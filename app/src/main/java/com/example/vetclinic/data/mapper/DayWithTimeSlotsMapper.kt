package com.example.vetclinic.data.mapper

import com.example.vetclinic.data.network.model.DayDto
import com.example.vetclinic.data.network.model.DayWithTimeSlotsDto
import com.example.vetclinic.data.network.model.TimeSlotDto
import com.example.vetclinic.domain.entities.Day
import com.example.vetclinic.domain.entities.DayWithTimeSlots
import com.example.vetclinic.domain.entities.TimeSlot
import jakarta.inject.Inject

class DayWithTimeSlotsMapper @Inject constructor() {


    fun dayDtoToEntity(dto: DayDto): Day {
        return Day(
            id = dto.id,
            date = dto.date
        )
    }

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


    fun dayWithTimeSlotsDtoToEntity(dto: DayWithTimeSlotsDto): DayWithTimeSlots {
        return DayWithTimeSlots(
            day = dayWithTimeSlotsDtoToDay(dto),
            timeSlots = dto.timeSlots.map(::timeSlotDtoToEntity)
        )
    }

    fun dayWithTimeSlotsDtoToDayDto(dayWithTimeSlotsDto: DayWithTimeSlotsDto): DayDto {
        return DayDto(id = dayWithTimeSlotsDto.id, date = dayWithTimeSlotsDto.date)
    }

    fun dayWithTimeSlotsDtoListToDayDtoList(dayWithTimeSlotsDtoList: List<DayWithTimeSlotsDto>):
            List<DayDto> {
        return dayWithTimeSlotsDtoList.map(::dayWithTimeSlotsDtoToDayDto)
    }

    private fun dayWithTimeSlotsDtoToDay(dayWithTimeSlotsDto: DayWithTimeSlotsDto): Day {
        return Day(id = dayWithTimeSlotsDto.id, date = dayWithTimeSlotsDto.dayOfMonth.toString())
    }


    fun formatToTimeStamp(date: String, time: String): String {
        return "$date $time:00"
    }

}