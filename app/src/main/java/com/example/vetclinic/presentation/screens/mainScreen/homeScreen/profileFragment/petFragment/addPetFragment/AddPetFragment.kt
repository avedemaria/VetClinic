package com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment.addPetFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vetclinic.R
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.databinding.FragmentAddPetBinding
import com.example.vetclinic.presentation.providers.ViewModelFactory
import com.example.vetclinic.presentation.screens.mainScreen.homeScreen.profileFragment.petFragment.PetFragment
import jakarta.inject.Inject


class AddPetFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: AddPetViewModel by viewModels {
        viewModelFactory
    }


    private var _binding: FragmentAddPetBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentAddPetBinding is null"
        )


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddPetBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.inject(this)
//
//        setUpPetTypeSpinner()
//        setUpPetGenderSpinner()

        binding.btnAddPet.setOnClickListener {
            onAddPetButtonClick()
        }

        observeViewModel()

    }


    private fun onAddPetButtonClick() {

        val petData = binding.petInput.collectPetInput()

        if (petData.type == CHOOSE_TYPE || petData.gender == CHOOSE_GENDER ||
            petData.name.isBlank() || petData.bDay.isBlank()
        ) {
            Toast.makeText(
                requireContext(), "Заполните все обязательные поля",
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        viewModel.addPetData(petData.name, petData.type, petData.gender, petData.bDay)

    }


    private fun observeViewModel() {
        viewModel.addPetState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AddPetUiState.Error -> Toast.makeText(
                    requireContext(),
                    "An error has occurred: ${state.message}",
                    Toast.LENGTH_SHORT
                ).show()

                AddPetUiState.Loading -> Log.d(TAG, "Заглушка для AddPetUiState.Loading")
                AddPetUiState.Success -> parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, PetFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }


//    private fun showDatePickerDialog() {
//        CustomDatePicker(requireContext()) { selectedDate ->
//            binding.tvBday.setText(selectedDate)
//        }.show()
//    }


//    private fun setUpPetTypeSpinner() {
//        val petTypes = arrayOf(CHOOSE_TYPE, "Кот", "Собака", "Грызун")
//        val spinnerAdapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_item, petTypes
//        )
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerPetType.adapter = spinnerAdapter
//
//        binding.spinnerPetType.setSelection(0)
//
//    }
//
//
//    private fun setUpPetGenderSpinner() {
//        val genders = arrayOf(CHOOSE_GENDER, "Мальчик", "Девочка")
//        val spinnerAdapter = ArrayAdapter(
//            requireContext(), android.R.layout.simple_spinner_item,
//            genders
//        )
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerPetGender.adapter = spinnerAdapter
//
//        binding.spinnerPetGender.setSelection(0)
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TAG = "AddPetFragment"
        private const val CHOOSE_TYPE = "Выберите тип питомца"
        private const val CHOOSE_GENDER = "Выберите пол питомца"
    }
}







