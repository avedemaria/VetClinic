package com.example.vetclinic.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.vetclinic.databinding.FragmentLoginBinding
import com.example.vetclinic.di.AppComponent
import com.example.vetclinic.domain.UserDataStore
import com.example.vetclinic.presentation.VetClinicApplication
import com.example.vetclinic.presentation.viewmodel.LoginState
import com.example.vetclinic.presentation.viewmodel.LoginViewModel
import com.example.vetclinic.presentation.viewmodel.ViewModelFactory
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            viewModel.loginUser(email, password)
        }

        binding.tvRegister.setOnClickListener {
            launchRegistrationFragment()
        }

        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString()
            launchResetPasswordWithEmailFragment(email)
        }

        binding.btnLogin.setOnLongClickListener {
            binding.etEmail.setText("test4396a0d3-404f-4942-9f33-4ddd1f2c89d4@test.com")
            binding.etPassword.setText("password")
            true

        }


//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                     // Возврат к предыдущему фрагменту
//                }
//            })

    }

    private fun observeViewModel() {
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginState.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    binding.btnLogin.isEnabled = true

                    Toast.makeText(
                        requireContext(), "${state.message}", Toast.LENGTH_SHORT
                    ).show()
                }

                is LoginState.Result -> {
                    binding.progressIndicator.visibility = View.GONE
                    binding.btnLogin.isEnabled = true

                    if (state.userRole == "admin") {
                        launchAdminFragment()
                    } else {
                        launchMainFragment()
                    }

                }

                LoginState.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }

            }
        }
    }


    private fun launchAdminFragment() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToAdminHomeFragment())
    }

    private fun launchMainFragment() {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToMainFragment()
        )
    }

    private fun launchRegistrationFragment() {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToRegistrationFragment()
        )
    }

    private fun launchResetPasswordWithEmailFragment(email: String) {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToResetPasswordWithEmailFragment(email)
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TAG = "LoginFragment"
    }
}