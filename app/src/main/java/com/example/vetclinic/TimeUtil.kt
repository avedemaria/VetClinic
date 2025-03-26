package com.example.vetclinic

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun String.extractDayOfMonth(): Int {
    val parsedDate = LocalDate.parse(this)
    return parsedDate.dayOfMonth
}

fun String.extractTime(): String {
    val parsedTime = LocalTime.parse(this.substring(11, 19))
    return parsedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
}