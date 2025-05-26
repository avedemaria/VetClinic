package com.example.vetclinic.presentation.screens.updatePasswordScreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.vetclinic.VetClinicApplication
import com.example.vetclinic.databinding.FragmentUpdatePasswordBinding
import com.example.vetclinic.domain.usecases.ResetPasswordUseCase
import com.example.vetclinic.presentation.providers.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import jakarta.inject.Inject


class UpdatePasswordFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _binding: FragmentUpdatePasswordBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException(
            "FragmentUpdatePasswordBinding is null"
        )

    private val viewModel: UpdatePasswordViewModel by viewModels { viewModelFactory  }

    @Inject  lateinit var resetPasswordUseCase: ResetPasswordUseCase


    private val component by lazy {
        (requireActivity().application as VetClinicApplication).component
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        component.inject(this)

        binding.updatePasswordButton.setOnClickListener {
            val newPassword = binding.etConfirmNewPassword.text.toString()
                viewModel.updatePassword(newPassword)
        }
        observeViewModel()
    }



    private fun observeViewModel() {
        viewModel.updatePasswordState.observe(viewLifecycleOwner) { state ->

            when (state) {
                is UpdatePasswordState.Error -> Toast.makeText(
                    requireContext(),
                    "Возникла ошибка ${state.message}",
                    Toast.LENGTH_SHORT
                ).show()

                UpdatePasswordState.Loading -> Log.d(TAG, "Заглушка для Loading")
                UpdatePasswordState.Success -> {
                    Snackbar.make(binding.root,
                        "Пароль успешно обновлён",
                        Snackbar.LENGTH_SHORT
                    ).show()

                    launchLoginFragment()
                }

            }
        }
    }

    private fun launchLoginFragment() {
        findNavController().navigate(
            UpdatePasswordFragmentDirections
                .actionUpdatePasswordFragmentToLoginFragment()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val TAG = "UpdatePasswordFragment"
    }
}