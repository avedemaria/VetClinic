package com.example.vetclinic.utils.impl

import android.util.Log
import com.example.vetclinic.utils.AgeUtils
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@Singleton
class AgeUtilsImpl @Inject constructor(): AgeUtils {

    override fun calculatePetAge(petBday: String?): String {
        return try {

            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val birthDate = LocalDate.parse(petBday, formatter)
            val currentDate = LocalDate.now()
            val period = Period.between(birthDate, currentDate)

            val year = period.years
            val month = period.months


            val yearText = if (year > 0) {
                "$year ${getYearSuffix(year)}"
            } else {
                ""
            }

            val monthText = if (month > 0 || year == 0) {
                "$month ${getMonthSuffix()}"
            } else {
                ""
            }

            listOf(yearText, monthText).filter {
                it.isNotEmpty()
            }.joinToString(" ")

        } catch (e: Exception) {
            Log.e("AgeUtilsImpl", "Error calculating pet age: ${e.message}")
            "0 мес."
        }
    }


    private fun getYearSuffix(years: Int): String {
        return when {
            years % 10 == 1 && years % 100 != 11 -> "год"
            years % 10 in 2..4 && (years % 100 !in 12..14) -> "года"
            else -> "лет"
        }
    }

    private fun getMonthSuffix(): String {
        return "мес."
    }

}