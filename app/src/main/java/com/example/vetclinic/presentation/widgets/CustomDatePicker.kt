package com.example.vetclinic.presentation.widgets

import android.content.Context
import android.icu.util.Calendar
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.vetclinic.databinding.DialogDatePickerBinding

class CustomDatePicker(
    private val context: Context,
    private val onDateSet: (String) -> Unit,
) {


    private var dialog: AlertDialog? = null


    fun show() {
        val binding = DialogDatePickerBinding.inflate(LayoutInflater.from(context))
        val calendar = Calendar.getInstance()


        with(binding) {
            dayPicker.apply {
                minValue = 1
                maxValue = 31
                value = calendar.get(Calendar.DAY_OF_MONTH)
                setFormatter { value -> String.format("%02d", value) }
            }

            monthPicker.apply {
                minValue = 1
                maxValue = 12
                value = calendar.get(Calendar.MONTH) + 1
                displayedValues =
                    arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
            }


            yearPicker.apply {
                minValue = 1915
                maxValue = calendar.get(Calendar.YEAR)
                value = calendar.get(Calendar.YEAR)
            }
        }


       
        dialog = AlertDialog.Builder(context)
            .setTitle("Выберите дату")
            .setView(binding.root)
            .setPositiveButton("ОК") { _, _ ->
                val selectedDay = binding.dayPicker.value
                val selectedMonth = binding.monthPicker.value
                val selectedYear = binding.yearPicker.value

                val selectedDate =
                    String.format("%02d-%02d-%04d", selectedDay, selectedMonth, selectedYear)
                onDateSet(selectedDate)
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog?.show()
    }
}







