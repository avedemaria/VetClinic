package com.example.vetclinic.presentation.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentPetBinding
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.presentation.PetParameter
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.adapter.petAdapter.OnEditClickListener
import com.example.vetclinic.presentation.adapter.petAdapter.PetAdapter
import com.example.vetclinic.presentation.viewmodel.PetUiState
import com.example.vetclinic.presentation.viewmodel.PetViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject
import java.util.Calendar


class PetFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: PetViewModel by viewModels {
        viewModelFactory
    }


    private lateinit var petsAdapter: PetAdapter

    private var _binding: FragmentPetBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentPetBinding is null"
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
        _binding = FragmentPetBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        onAddPetButtonClicked()

        setUpAdapter()

        observeViewModel()

        viewModel.getPetsData()
    }


    private fun setUpAdapter() {

        petsAdapter = PetAdapter(object : OnEditClickListener {
            override fun onEditClick(pet: Pet, parameter: String) {
                when (parameter) {
                    PetParameter.NAME.name -> showPetNameDialog(pet)
                    PetParameter.TYPE.name -> showPetTypeDialog(pet)
                    PetParameter.BDAY.name -> showDatePickerDialog(pet)
                    PetParameter.GENDER.name -> showPetGenderDialog(pet)
                }

            }
        })

        binding.rvPets.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL, false
            )
            adapter = petsAdapter
        }

        setUpItemTouchHelper()
    }


    private fun observeViewModel() {
        viewModel.petState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PetUiState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "An error has occurred: ${state.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.petContent.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                }


                PetUiState.Loading -> {
                    binding.petContent.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }

                is PetUiState.Success -> {
                    binding.petContent.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    Log.d("PetFragment", "Data received: ${state.pets}")
                    petsAdapter.submitList(state.pets)
                }

                PetUiState.Deleted -> {
                    binding.petContent.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Питомец успешно удалён",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

        }
    }


    private fun onAddPetButtonClicked() {
        binding.btnAddPet.setOnClickListener {
            val addPetFragment = AddPetFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, addPetFragment) // Контейнер из ProfileFragment
                .addToBackStack(null) // Позволяет вернуться назад
                .commit()

        }
    }

    private fun setUpItemTouchHelper() {
        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val pet: Pet = petsAdapter.currentList[position]
                    showDeleteConfirmationDialog(pet, position)
                }
            })

        itemTouchHelper.attachToRecyclerView(binding.rvPets)
    }


    private fun showDeleteConfirmationDialog(pet: Pet, position: Int) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Удалить питомца")
            .setMessage("Вы уверены, что хотите удалить данные о питомце?")
            .setPositiveButton("Да") { _, _ ->
                viewModel.deletePet(pet)
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
                petsAdapter.notifyItemChanged(position)
            }

        dialogBuilder.create().show()

    }

    private fun showPetNameDialog(pet: Pet) {
        val editText = EditText(requireContext()).apply {
            setText(pet.petName)
            setPadding(32, 16, 32, 16)
        }

        val container = FrameLayout(requireContext()).apply {
            setPadding(48, 24, 48, 24)
            addView(editText)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Введите новое имя")
            .setView(container)
            .setPositiveButton("ОК") { dialog, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotBlank() && newName != pet.petName) {
                    val updatedPet = pet.copy(petName = newName)
                    viewModel.updatePet(pet.petId, updatedPet)

                }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()

    }

    private fun showPetTypeDialog(pet: Pet) {
        val petTypes = arrayOf("Кот", "Собака", "Грызун")

        AlertDialog.Builder(requireContext())
            .setTitle("Выберите тип питомца")
            .setItems(petTypes) { dialog, which ->
                val selectedType = petTypes[which]
                val updatedPet = pet.copy(petType = selectedType)
                viewModel.updatePet(pet.petId, updatedPet)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }


    private fun showDatePickerDialog(pet: Pet) {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate =
                    String.format(
                        "%02d-%02d-%04d",
                        selectedDay,
                        selectedMonth + 1,
                        selectedYear
                    )

                val updatedPet = pet.copy(petBDay = selectedDate)

                viewModel.updatePet(pet.petId, updatedPet)
            }, year, month, day)

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        datePickerDialog.show()


    }


    private fun showPetGenderDialog(pet: Pet) {
        val genders = arrayOf("Мальчик", "Девочка")

        AlertDialog.Builder(requireContext())
            .setTitle("Выберите пол питомца")
            .setItems(genders) { dialog, which ->
                val selectedGender = genders[which]
                val updatedPet = pet.copy(petGender = selectedGender)
                viewModel.updatePet( pet.petId, updatedPet)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
