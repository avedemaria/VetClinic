import com.example.vetclinic.data.RepositoryImpl
import com.example.vetclinic.domain.Repository
import com.example.vetclinic.domain.entities.Day
import com.example.vetclinic.domain.entities.TimeSlot
import com.example.vetclinic.domain.usecases.GetTimeSlotsUseCase
import java.time.LocalDate

import java.time.LocalTime
import java.util.UUID
import kotlin.random.Random

fun main() {




}


private fun generateTimeSlots(
    days: List<Day>,
    doctorId: String,
    serviceId: String,
    duration: Int
): List<TimeSlot> {

    return buildList {
        for (day in days) {
            val startTime = LocalTime.of(10, 0)
            val endTime = LocalTime.of(20, 0)

            var slotStartTime = startTime
            var slotEndTime = startTime.plusMinutes(duration.toLong())

            while (slotEndTime.isBefore(endTime) || slotEndTime.equals(endTime)
            ) {
                add(
                    TimeSlot(
                        id = UUID.randomUUID().toString(),
                        doctorId = doctorId,
                        serviceId = serviceId,
                        dayId = day.id,
                        startTime = slotStartTime.toString(),
                        endTime = slotEndTime.toString(),
                        isBooked = false
                    )
                )
                slotStartTime = slotEndTime
                slotEndTime = slotStartTime.plusMinutes(duration.toLong())
            }
        }
    }

}









