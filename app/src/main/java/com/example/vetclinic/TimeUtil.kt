package com.example.vetclinic

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

fun String.extractDayOfMonth(): Int {
    val parsedDate = LocalDate.parse(this)
    return parsedDate.dayOfMonth
}


fun String.extractTime(): String {
    val parsedTime = LocalTime.parse(this.substring(11, 19))
    return parsedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
}


fun String.formatDayMonthYear(): String {
    return try {
        val parsedDate = LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)
        parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    } catch (e: Exception) {
        this
    }
}

fun String.toFormattedDateTime(): String {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateTime = LocalDateTime.parse(this, formatter)
    return dateTime.format(formatter)
}

fun String.formatToLocalDateTime(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    val localDateTime = LocalDateTime.parse(this, formatter)
    return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}