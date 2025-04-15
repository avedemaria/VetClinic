package com.example.vetclinic.presentation.fragment

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.vetclinic.CustomDatePicker
import com.example.vetclinic.databinding.FragmentPetRegistrationFormBinding
import com.example.vetclinic.domain.entities.PetInputData
import com.example.vetclinic.presentation.VetClinicApplication


class PetRegistrationForm : Fragment() {



    private var _binding: FragmentPetRegistrationFormBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentPetRegistrationFormBinding is null"
        )


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPetRegistrationFormBinding.inflate(
            LayoutInflater.from(requireContext()),
            container,
            false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component.inject(this)

        setUpPetTypeSpinner()
        setUpPetGenderSpinner()


        binding.etBday.apply {
            inputType = InputType.TYPE_NULL
            keyListener = null
        }
            .setOnClickListener {
                Log.d(TAG, "clicked")
                showDatePickerDialog()
            }


//        val petName = binding.etPetName.text.toString()
//        val petBirthday = binding.etBday.text.toString()
//        val petType = binding.autoPetType.text.toString()
//        val petGender = binding.autoPetGender.text.toString()
//        viewModel.updatePetInputInState(
//            PetInputData(
//                name = petName,
//                type = petType,
//                bDay = petBirthday,
//                gender = petGender
//            )
//        )

    }


  fun collectPetInput(): PetInputData {
      val petName = binding.etPetName.text.toString()
      val petBirthday = binding.etBday.text.toString()
      val petType = binding.autoPetType.text.toString()
      val petGender = binding.autoPetGender.text.toString()
         return PetInputData(
              name = petName,
              type = petType,
              bDay = petBirthday,
              gender = petGender
          )
  }

    private fun showDatePickerDialog() {
        CustomDatePicker(requireContext()) { selectedDate ->
            binding.etBday.setText(selectedDate)
        }.show()
    }


    private fun setUpPetTypeSpinner() {
        val petTypes = arrayOf("Кот", "Собака", "Грызун")
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, petTypes
        )
        binding.autoPetType.setAdapter(spinnerAdapter)

    }


    private fun setUpPetGenderSpinner() {
        val genders = arrayOf("Мальчик", "Девочка")
        val spinnerAdapter = ArrayAdapter(
            requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            genders
        )
        binding.autoPetGender.setAdapter(spinnerAdapter)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TAG = "PetRegistrationForm"
    }

}