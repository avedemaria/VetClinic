package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vetclinic.databinding.FragmentRegistrationBinding
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.RegistrationState
import com.example.vetclinic.presentation.viewmodel.RegistrationViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject


class RegistrationFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: RegistrationViewModel by viewModels {
        viewModelFactory
    }

    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    private var _binding: FragmentRegistrationBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentCreateAccountBinding is null"
        )


    override fun onAttach(context: Context) {
        super.onAttach(context)
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        observeViewModel()


        binding.btnCreateAccount.setOnClickListener {

            val name = binding.etName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val petName = binding.etPetName.text.toString()
            val phoneNumber = binding.etPhoneNumber.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()


            viewModel.registerUser(name, lastName, petName, phoneNumber, email, password)

        }

    }

    private fun observeViewModel() {
        viewModel.registrationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegistrationState.Error -> Toast.makeText(
                    requireContext(),
                    "An error has occurred: ${state.message}", Toast.LENGTH_SHORT
                ).show()

                is RegistrationState.Result -> {
                    launchMainScreenActivity(state.user.userName)
                }

            }
        }
    }


    private fun launchMainScreenActivity(userName: String) {
        findNavController().navigate(
            RegistrationFragmentDirections
                .actionRegistrationFragmentToMainFragment()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}







