import com.example.vetclinic.domain.entities.Day
import com.example.vetclinic.domain.entities.TimeSlot

import java.time.LocalTime
import java.util.UUID

fun main () {


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









