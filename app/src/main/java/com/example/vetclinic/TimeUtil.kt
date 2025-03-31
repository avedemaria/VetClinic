package com.example.vetclinic

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun String.extractDayOfMonth(): Int {
    val parsedDate = LocalDate.parse(this)
    return parsedDate.dayOfMonth
}


fun String.extractTime(): String {
    val parsedTime = LocalTime.parse(this.substring(11, 19))
    return parsedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
}


fun LocalDate.formatDateTime(timeSlot: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    val timeParts = timeSlot.split(":")
    val hour = timeParts[0].toInt()
    val minute = timeParts[1].toInt()

    return LocalDateTime.of(this.year, this.monthValue, this.dayOfMonth, hour, minute, 0)
        .format(formatter)
}


fun String.toLocalDateDefault(): LocalDate {
    // Assuming the date is in a format like "2025-03-27"
    return LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

fun String.formatAppointmentDateTime(): String {
    val inputFormatters = listOf(
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
    )

    val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    for (formatter in inputFormatters) {
        try {
            val parsedDateTime = LocalDateTime.parse(this, formatter)
            return parsedDateTime.format(outputFormatter)
        } catch (e: Exception) {
            // Continue to next formatter
            continue
        }
    }
    // If all parsing attempts fail, return the original string
    return this
}

fun LocalDate.toFormattedString(): String {
    return this.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
}


fun String.toEpochMilli(): Long {
    val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val localDateTime = LocalDateTime.parse(this, dateTimeFormatter)
    return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
}

fun String.formatToLocalDateTime(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    val localDateTime = LocalDateTime.parse(this, formatter)
    return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}

fun String?.toLocalDateOrNull(format: String = "yyyy-MM-dd"): LocalDate? {
    return try {
        if (this.isNullOrEmpty()) {
            null
        } else {
            val formatter = DateTimeFormatter.ofPattern(format)
            LocalDate.parse(this, formatter)
        }
    } catch (e: Exception) {
        null
    }
}