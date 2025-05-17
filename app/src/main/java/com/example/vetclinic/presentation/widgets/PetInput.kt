package com.example.vetclinic.presentation.widgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.databinding.FragmentPetInputBinding
import com.example.vetclinic.domain.entities.pet.PetInputData


class PetInput : Fragment() {

    private var _binding: FragmentPetInputBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentPetInputBinding is null"
        )


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPetInputBinding.inflate(
            LayoutInflater.from(requireContext()),
            container,
            false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        component.inject(this)
    }


    fun collectPetInput(): PetInputData {
        return binding.customPetInput.collectPetInput()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TAG = "PetRegistrationForm"
    }

}