package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vetclinic.databinding.FragmentLoginBinding
import com.example.vetclinic.di.AppComponent
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.LoginState
import com.example.vetclinic.presentation.viewmodel.LoginViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject

class LoginFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: LoginViewModel by viewModels{viewModelFactory}

    private var _binding: FragmentLoginBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentLoginBinding is null"
        )


    private val component: AppComponent by lazy {
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        observeViewModel()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.loginUser(email, password)
        }

        binding.tvRegister.setOnClickListener {
            launchRegistrationFragment()
        }


    }

    private fun observeViewModel() {
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginState.Error -> Toast.makeText(
                    requireContext(),
                    "${state.message}",
                    Toast.LENGTH_SHORT
                ).show()

                is LoginState.IsAuthenticated ->
                    launchMainScreenActivity()

                is LoginState.LoggedOut -> Log.d("LoginFragment", "null")
                is LoginState.Result ->
                    launchMainScreenActivity()
            }
        }
    }

    private fun launchMainScreenActivity() {
        findNavController().navigate(
            LoginFragmentDirections
                .actionLoginFragmentToMainFragment()
        )
    }

    private fun launchRegistrationFragment() {
        findNavController().navigate(
            LoginFragmentDirections
                .actionLoginFragmentToRegistrationFragment()
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}