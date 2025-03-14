package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentAddPetBinding
import com.example.vetclinic.databinding.FragmentPetBinding
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.adapter.PetAdapter
import com.example.vetclinic.presentation.viewmodel.PetUiState
import com.example.vetclinic.presentation.viewmodel.PetViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject


class AddPetFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: PetViewModel by viewModels {
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPetBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setUpPetTypeSpinner()
        setUpPetGenderSpinner()

        onAddPetButtonClick()

        observeViewModel()

    }


    private fun onAddPetButtonClick() {

        val petName = binding.etPetName.text.toString()
        val petType = binding.spinnerPetType.selectedItem.toString()
        val petGender = binding.spinnerPetGender.selectedItem.toString()
        val petBirthday = "${binding.etDay.text}-${binding.spinnerMonth.selectedItem}" +
                "-${binding.etYear.text}"


        if (petType == CHOOSE_TYPE) {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_choose_pet_type), Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        if (petGender == CHOOSE_GENDER) {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_choose_pet_gender), Toast.LENGTH_SHORT
            )
                .show()
        }

        if (petName.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_enter_pet_name), Toast.LENGTH_SHORT
            )
                .show()
            return
        }



//        binding.btnAddPet.setOnClickListener {
//            viewModel.addPet()
//            parentFragmentManager.popBackStack()
//        }
    }


    private fun observeViewModel() {
        viewModel.petState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PetUiState.Error -> Toast.makeText(
                    requireContext(),
                    "An error has occurred: ${state.message}",
                    Toast.LENGTH_SHORT
                ).show()

                PetUiState.Loading -> Log.d(TAG, "Заглушка для PetUiState.Loading")
                is PetUiState.Success -> Log.d(TAG, "Заглушка для PetUiState.Success")
            }
        }
    }

    private fun setUpPetTypeSpinner() {
        val petTypes = arrayOf(CHOOSE_TYPE, "Кот", "Собака", "Грызун")
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, petTypes
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPetType.adapter = spinnerAdapter

        binding.spinnerPetType.setSelection(0)

    }


    private fun setUpPetGenderSpinner() {
        val genders = arrayOf(CHOOSE_GENDER, "Мальчик", "Девочка")
        val spinnerAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item,
            genders
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPetGender.adapter = spinnerAdapter

        binding.spinnerPetGender.setSelection(0)
    }


    companion object {
        private const val TAG = "AddPetFragment"
        private const val CHOOSE_TYPE = "Выберите тип питомца"
        private const val CHOOSE_GENDER = "Выберите пол питомца"
    }
}







