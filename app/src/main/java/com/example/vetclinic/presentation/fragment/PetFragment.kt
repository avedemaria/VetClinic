package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vetclinic.R
import com.example.vetclinic.databinding.FragmentPetBinding
import com.example.vetclinic.databinding.FragmentUserBinding
import com.example.vetclinic.databinding.PetItemBinding
import com.example.vetclinic.domain.entities.Pet
import com.example.vetclinic.presentation.PetParameter
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.adapter.OnEditClickListener
import com.example.vetclinic.presentation.adapter.PetAdapter
import com.example.vetclinic.presentation.adapter.PetViewHolder
import com.example.vetclinic.presentation.viewmodel.PetUiState
import com.example.vetclinic.presentation.viewmodel.PetViewModel
import com.example.vetclinic.presentation.viewmodel.UserViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject


class PetFragment : Fragment() {


    private val userId by lazy {
        arguments?.getString(ProfileFragment.USER_ID)
            ?: throw IllegalArgumentException("UserId is null")


    }

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

        viewModel.getPetsFromRoom(userId)

        setUpAdapter()

        observeViewModel()

    }


    private fun setUpAdapter() {

        petsAdapter = PetAdapter(object : OnEditClickListener {
            override fun onEditClick(pet: Pet, parameter: String) {
                when (parameter) {
                    PetParameter.NAME.name -> Log.d("PetFragment", "NameEditing")
                    PetParameter.TYPE.name -> showPetTypeDialog(pet)
                    PetParameter.BDAY.name -> Log.d("PetFragment", "BdayEditing")
                }

            }
        })

        binding.rvPets.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL, false
            )
            adapter = petsAdapter
        }
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
                    petsAdapter.submitList(state.pets)
                }

            }

        }
    }


    private fun showPetTypeDialog(pet: Pet) {
        val petTypes = arrayOf("Кот", "Собака", "Грызун")

        AlertDialog.Builder(requireContext())
            .setTitle("Выберите тип питомца")
            .setItems(petTypes) { dialog, which ->
                val selectedType = petTypes[which]
                val updatedPet = pet.copy(petType = selectedType)
                viewModel.updatePet(userId, pet.petId, updatedPet)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
