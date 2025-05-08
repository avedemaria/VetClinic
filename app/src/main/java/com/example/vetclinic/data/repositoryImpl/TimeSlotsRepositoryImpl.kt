package com.example.vetclinic.data.repositoryImpl

import android.util.Log
import com.example.vetclinic.data.mapper.DayWithTimeSlotsMapper
import com.example.vetclinic.data.network.SupabaseApiService
import com.example.vetclinic.data.network.model.DayWithTimeSlotsDto
import com.example.vetclinic.data.network.model.TimeSlotDto
import com.example.vetclinic.domain.repository.TimeSlotsRepository
import com.example.vetclinic.domain.entities.timeSlot.DayWithTimeSlots
import jakarta.inject.Inject
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class TimeSlotsRepositoryImpl @Inject constructor(
    private val supabaseApiService: SupabaseApiService,
    private val dayWithTimeSlotsMapper: DayWithTimeSlotsMapper
) : TimeSlotsRepository {


    override suspend fun generateAndSaveTimeSlots(
        doctorId: String,
        serviceId: String,
        duration: String
    ): Result<Unit> = runCatching {

        val existingDataResult = getExistingDaysAndTimeSlots(doctorId, serviceId)
        val existingDaysWithTimeSlots = existingDataResult.getOrThrow()

        val dayWithTimeSlotsDtoList = generateDaysWithTimeSlotsForTwoWeeks(
            doctorId, serviceId, duration.toInt()
        )


        val newDays = dayWithTimeSlotsDtoList.filter { newDay ->
            existingDaysWithTimeSlots.none { it.date == newDay.date }
        }

        if (newDays.isNotEmpty()) {
            val daysResponse = supabaseApiService.insertDays(
                dayWithTimeSlotsMapper.dayWithTimeSlotsDtoListToDayDtoList(newDays)
            )
            if (!daysResponse.isSuccessful) {
                Log.e(
                    TAG,
                    "Failed to insert days: ${daysResponse.code()} ${daysResponse.message()}"
                )
                throw Exception("Failed to add days to SupabaseDb")
            }
            Log.d(TAG, "${newDays.size} new days added.")
        }

        val updatedDayWithTimeSlotsDtoList = dayWithTimeSlotsDtoList.map { day ->
            val insertedDay = existingDaysWithTimeSlots.find { it.date == day.date }
            day.copy(
                timeSlots = day.timeSlots.map { timeSlot ->
                    timeSlot.copy(
                        dayId = insertedDay?.id ?: timeSlot.dayId
                    ) // Используем новый day_id
                }
            )
        }

        val newTimeSlots =
            updatedDayWithTimeSlotsDtoList.flatMap { it.timeSlots }.filter { newTimeSlot ->
                existingDaysWithTimeSlots.flatMap { it.timeSlots }.none {
                    it.startTime == newTimeSlot.startTime
                            && it.endTime == newTimeSlot.endTime
                            && it.dayId == newTimeSlot.dayId
                            && it.doctorId == doctorId
                }
            }

        val timeSlotsForDays =
            formatTimeSlots(newTimeSlots, updatedDayWithTimeSlotsDtoList, doctorId, serviceId)


        if (timeSlotsForDays.isNotEmpty()) {
            val timeSlotsResponse = supabaseApiService.insertTimeSlots(timeSlotsForDays)
            if (!timeSlotsResponse.isSuccessful) {
                Log.e(
                    TAG,
                    "Failed to insert time slots: ${timeSlotsResponse.code()} ${timeSlotsResponse.message()}"
                )
                throw Exception("Failed to add new time slots to SupabaseDb")
            }
            Log.d(TAG, "${timeSlotsForDays.size} new time slots added.")
        }
    }
        .onFailure { e ->
            Log.e(TAG, "Error while adding days and timeslots to SupabaseDb", e)
        }

    override suspend fun getAvailableDaysAndTimeSlots(
        doctorId: String,
        serviceId: String
    ): Result<List<DayWithTimeSlots>> =
        kotlin.runCatching {

            val startDate = LocalDate.now().toString()
            val endDate = LocalDate.now().plusWeeks(2).toString()

            val doctorIdWithParam = "eq.$doctorId"
            val serviceIdWithParam = "eq.$serviceId"
            val dateRangeWithParam = "gte.$startDate, lte.$endDate"

            val response =
                supabaseApiService.getDaysWithTimeSlots(
                    doctorId = doctorIdWithParam,
                    serviceId = serviceIdWithParam,
                    dateRange = dateRangeWithParam
                )

            if (response.isSuccessful) {
                val dayWithTimeSlotsDto =
                    response.body() ?: throw Exception("Response body is null")


                dayWithTimeSlotsDto.map { dto ->
                    dayWithTimeSlotsMapper
                        .dayWithTimeSlotsDtoToEntity(dto)
                }
            } else {
                throw Exception(
                    "Failed to fetch day with timeslots. " +
                            "Error: ${response.code()} - ${response.message()}"
                )
            }
        }
            .onFailure { e ->
                Log.e(TAG, "Error while getting days and timeslots from SupabaseDb", e)

            }


    private fun formatTimeSlots(
        newTimeSlots: List<TimeSlotDto>,
        dayWithTimeSlotsDtoList: List<DayWithTimeSlotsDto>,
        doctorId: String,
        serviceId: String
    ): List<TimeSlotDto> {

        return newTimeSlots.mapNotNull { timeSlot ->

            val dayDto = dayWithTimeSlotsDtoList.find { it.id == timeSlot.dayId }
                ?: return@mapNotNull null

            val formattedStartTime =
                dayWithTimeSlotsMapper.formatToTimeStamp(
                    dayDto.date,
                    timeSlot.startTime
                )
            val formattedEndTime =
                dayWithTimeSlotsMapper.formatToTimeStamp(
                    dayDto.date,
                    timeSlot.endTime
                )

            TimeSlotDto(
                id = timeSlot.id,
                doctorId = doctorId,
                serviceId = serviceId,
                startTime = formattedStartTime,
                endTime = formattedEndTime,
                dayId = dayDto.id,
                isBooked = timeSlot.isBooked
            )
        }
    }


    private fun generateDaysWithTimeSlotsForTwoWeeks(
        doctorId: String,
        serviceId: String,
        duration: Int
    ): List<DayWithTimeSlotsDto> {

        val today = LocalDate.now()


        return (0 until 14).map { dayDates ->
            val date = today.plusDays(dayDates.toLong()).toString()
            val dayId = UUID.randomUUID().toString()
            val timeSlots =
                generateTimeSlotsForDay(
                    dayId,
                    doctorId,
                    serviceId,
                    duration)

            DayWithTimeSlotsDto(
                id = dayId,
                date = date,
                timeSlots = timeSlots
            )
        }
    }


    private fun generateTimeSlotsForDay(
        dayId: String,
        doctorId: String,
        serviceId: String,
        duration: Int
    ): List<TimeSlotDto> {

        val startTime = LocalTime.of(10, 0)
        val endTime = LocalTime.of(20, 0)

        val slots = mutableListOf<TimeSlotDto>()

        var slotStartTime = startTime
        var slotEndTime = startTime.plusMinutes(duration.toLong())

        while (slotEndTime.isBefore(endTime) || slotEndTime.equals(endTime)
        ) {
            slots.add(
                TimeSlotDto(
                    id = UUID.randomUUID().toString(),
                    doctorId = doctorId,
                    serviceId = serviceId,
                    dayId = dayId,
                    startTime = slotStartTime.toString(),
                    endTime = slotEndTime.toString(),
                    isBooked = false
                )
            )
            slotStartTime = slotEndTime
            slotEndTime = slotStartTime.plusMinutes(duration.toLong())
        }
        return slots
    }


    private suspend fun getExistingDaysAndTimeSlots(
        doctorId: String,
        serviceId: String
    ): Result<List<DayWithTimeSlotsDto>> {
        return try {

            val startDate = LocalDate.now().toString()
            val endDate = LocalDate.now().plusWeeks(2).toString()

            val doctorIdWithParam = "eq.$doctorId"
            val serviceIdWithParam = "eq.$serviceId"
            val dateRangeWithParam = "gte.$startDate, lte.$endDate"


            val response =
                supabaseApiService.getDaysWithTimeSlots(
                    doctorId = doctorIdWithParam,
                    serviceId = serviceIdWithParam,
                    dateRange = dateRangeWithParam
                )

            if (response.isSuccessful) {
                val existingDaysWithTimeSlots = response.body() ?: emptyList()

                Result.success(existingDaysWithTimeSlots)
            } else {
                Log.e(
                    TAG,
                    "Failed to fetch existing days and time slots: ${response.code()}" +
                            " ${response.message()}"
                )
                Result.failure(
                    Exception(
                        "Failed to fetch existing days and timeslots ${response.code()}" +
                                " ${response.message()}"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun updateTimeSlotStatusToBooked(timeSlotId: String): Result<Unit> =
        kotlin.runCatching {

            val timeSlotIdWithParam = "eq.$timeSlotId"
            val timeSlots = supabaseApiService.getTimeSlotById(timeSlotIdWithParam)

            val existingTimeSlot = timeSlots.firstOrNull()
                ?: throw Exception("No time slot found with ID: $timeSlotId")

            val updatedTimeSlot = existingTimeSlot.copy(isBooked = true)

            val response =
                supabaseApiService.updateTimeSlot(timeSlotIdWithParam, updatedTimeSlot)

            if (!response.isSuccessful) {
                throw Exception("${response.errorBody()?.string()}")
            }
        }
            .onFailure { e ->
                Log.e(TAG, "Failed to update timeSlot status $e", e)
            }

    companion object {
        private const val TAG = "TimeSlotsRepositoryImpl"
    }


}