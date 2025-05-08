package com.example.vetclinic.presentation.screens.loginScreen.registrationFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vetclinic.databinding.FragmentUserRegistrationFormBinding
import com.example.vetclinic.domain.entities.user.UserInputData
import com.example.vetclinic.VetClinicApplication
import com.google.android.material.textfield.TextInputEditText


class UserInput : Fragment() {



    private var _binding: FragmentUserRegistrationFormBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentUserRegistrationFormBinding is null"
        )


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUserRegistrationFormBinding.inflate(
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

    fun collectUserInput(): UserInputData {
       return UserInputData(
            name = setUpInput(binding.etName).replaceFirstChar { it.uppercase() },
            lastName = setUpInput(binding.etLastName).replaceFirstChar { it.uppercase() },
            phone = setUpInput(binding.etPhoneNumber),
            email = setUpInput(binding.etEmail),
            password = setUpInput(binding.etPassword)
        )
    }


    private fun setUpInput(input: TextInputEditText): String {
        return input.text?.trim().toString()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

