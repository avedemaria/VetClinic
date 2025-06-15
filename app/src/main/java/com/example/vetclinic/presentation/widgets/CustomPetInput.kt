package com.example.vetclinic.presentation.widgets

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.example.vetclinic.R
import com.example.vetclinic.databinding.PetInputBinding
import com.example.vetclinic.domain.entities.pet.PetInputData

class CustomPetInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: PetInputBinding

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.pet_input, this, true)
        binding = PetInputBinding.bind(view)
        setupPetTypeSpinner()
        setupPetGenderSpinner()
        setupDatePicker()
    }

    private fun setupDatePicker() {
        binding.etBday.apply {
            inputType = InputType.TYPE_NULL
            keyListener = null
            setOnClickListener {
                CustomDatePicker(context) { selectedDate ->
                    binding.etBday.setText(selectedDate)
                }.show()
            }
        }
    }

    private fun setupPetTypeSpinner() {
        val petTypes = arrayOf("Кот", "Собака", "Грызун")
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, petTypes)
        binding.autoPetType.setAdapter(adapter)
    }

    private fun setupPetGenderSpinner() {
        val genders = arrayOf("Мальчик", "Девочка")
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, genders)
        binding.autoPetGender.setAdapter(adapter)
    }

    fun collectPetInput(): PetInputData {
        return PetInputData(
            name = binding.etPetName.text.toString(),
            type = binding.autoPetType.text.toString(),
            bDay = binding.etBday.text.toString(),
            gender = binding.autoPetGender.text.toString()
        )
    }

}